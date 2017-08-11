package com.kumuluz.ee.common.jca;

import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;
import org.jboss.jca.embedded.dsl.InputStreamDescriptor;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import javax.naming.Context;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class JcaEmbeddedDeployer {

    private static final Charset DESCRIPTOR_CHARSET = Charset.forName("UTF-8");
    private static JcaEmbeddedDeployer INSTANCE;

    private Embedded container;

    private boolean started;
    private List<URL> resources;
    private List<ResourceAdapterArchive> resourceAdapters;
    private Map<Descriptor, InputStreamDescriptor> descriptors;

    private JcaEmbeddedDeployer() {
        /*
         * Setup JBoss JNDI
         */
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

		/*
		 * Prepare Embedded JCA container
		 */
        this.container = EmbeddedFactory.create(false);
        this.started = false;

        this.resources = new ArrayList<>();
        this.resourceAdapters = new ArrayList<>();
        this.descriptors = new LinkedHashMap<>();
    }

    public static JcaEmbeddedDeployer getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new JcaEmbeddedDeployer();
        }
        return INSTANCE;
    }

    private void checkState() {
        if (!started) {
            throw new KumuluzServerException("The JCA container is not started!");
        }
    }

    public void start() {
        try {
            container.startup();
            started = true;
        } catch (Throwable e) {
            throw new KumuluzServerException("An error occurred while starting the JCA container", e);
        }
    }

    public void deploy(Class<?> clazz, String name) {
        deploy(clazz.getClassLoader().getResource(name));
    }

    private void deploy(URL resource) {
        checkState();

        try {
            resources.add(resource);
            container.deploy(resource);
        } catch (Throwable e) {
            String msg = String.format("An errror occurred while deploying the resource: \"%s\"", resource.getFile());
            throw new KumuluzServerException(msg, e);
        }
    }

    public void deploy(ResourceAdapterArchive raa) {
        checkState();

        try {
            resourceAdapters.add(raa);
            container.deploy(raa);
        } catch (Throwable e) {
            String msg = String.format("An errror occurred while deploying the Resource Adapter: \"%s\"", raa.getName());
            throw new KumuluzServerException(msg, e);
        }
    }

    public void deploy(Descriptor descriptor) {
        checkState();

        try {
            InputStreamDescriptor isd = new InputStreamDescriptor(descriptor.getDescriptorName(),
                new ByteArrayInputStream(descriptor.exportAsString().getBytes(DESCRIPTOR_CHARSET))
            );
            descriptors.put(descriptor, isd);
            container.deploy(isd);
        } catch (Throwable e) {
            String msg = String.format("An error occurred while deploying the descriptor: \"%s\"", descriptor.getDescriptorName());
            throw new KumuluzServerException(msg, e);
        }
    }

    public void stop() {
        checkState();

        try {
            Iterator<Entry<Descriptor, InputStreamDescriptor>> descriptorsIterator = descriptors.entrySet().stream().sorted(Collections.reverseOrder()).iterator();

            while (descriptorsIterator.hasNext()) {
                container.undeploy(descriptorsIterator.next().getValue());
                descriptorsIterator.remove();
            }

            Iterator<ResourceAdapterArchive> resourceAdaptersIterator = resourceAdapters.stream().sorted(Collections.reverseOrder()).iterator();

            while (resourceAdaptersIterator.hasNext()) {
                container.undeploy(resourceAdaptersIterator.next());
                resourceAdaptersIterator.remove();
            }

            Iterator<URL> resourcesIterator = resources.stream().sorted(Collections.reverseOrder()).iterator();

            while (resourcesIterator.hasNext()) {
                container.undeploy(resourcesIterator.next());
                resourcesIterator.remove();
            }

            container.shutdown();
            started = false;
        } catch (Throwable e) {
            throw new KumuluzServerException("An error occurred while stopping the JCA container", e);
        }
    }
}