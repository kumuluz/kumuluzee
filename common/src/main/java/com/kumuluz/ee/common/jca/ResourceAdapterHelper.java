package com.kumuluz.ee.common.jca;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;

import java.util.Objects;

/**
 * @author Marcos Koch Salvador
 * @since 2.4.0
 */
public class ResourceAdapterHelper {

    private static JavaArchive ironJacamarJdbcJar;
    private static ResourceAdapterArchive jdbcLocalRar;
    private static ResourceAdapterArchive jdbcXaRar;

    private ResourceAdapterHelper() {
    }

    private static JavaArchive getJdbcResourceAdapter() {
        if (Objects.isNull(ironJacamarJdbcJar)) {
            ironJacamarJdbcJar = ShrinkWrap.create(JavaArchive.class, "ironjacamar-jdbc.jar")
                    .addPackages(true, org.jboss.jca.adapters.AdaptersBundle.class.getPackage())
                    .addAsResource("jdbc.properties");
        }
        return ironJacamarJdbcJar;
    }

    public static ResourceAdapterArchive getJdbcLocalRAR() {
        if (Objects.isNull(jdbcLocalRar)) {
            jdbcLocalRar = ShrinkWrap.create(ResourceAdapterArchive.class, "jdbc-local.rar")
                    .addAsManifestResource("jdbc-local-ra.xml", "ra.xml")
                    .addAsLibrary(getJdbcResourceAdapter());
        }
        return jdbcLocalRar;
    }

    public static ResourceAdapterArchive getJdbcXaRAR() {
        if (Objects.isNull(jdbcXaRar)) {
            jdbcXaRar = ShrinkWrap.create(ResourceAdapterArchive.class, "jdbc-xa.rar")
                    .addAsManifestResource("jdbc-xa-ra.xml", "ra.xml")
                    .addAsLibrary(getJdbcResourceAdapter());
        }
        return jdbcXaRar;
    }
}
