package com.kumuluz.ee.jpa.common.utils;

import com.kumuluz.ee.jpa.common.TransactionType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class PersistenceUtils {

    private static final String PERSISTENCE_XML = "META-INF/persistence.xml";

    public static TransactionType getEntityManagerFactoryTransactionType(EntityManagerFactory emf) {

        EntityManager manager = emf.createEntityManager();

        try {
            manager.getTransaction();

            return TransactionType.RESOURCE_LOCAL;
        } catch (IllegalStateException e) {

            manager.close();
            return TransactionType.JTA;
        }
    }

    public static Optional<String> getDefaultUnitName() {

        Document document;

        try {
            Enumeration<URL> enumeration = PersistenceUtils.class.getClassLoader().getResources(PERSISTENCE_XML);

            if (!enumeration.hasMoreElements()) {
                return Optional.empty();
            }

            enumeration.nextElement();

            if (enumeration.hasMoreElements()) {
                return Optional.empty();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            document = builder.parse(PersistenceUtils.class.getClassLoader()
                    .getResourceAsStream(PERSISTENCE_XML));

        } catch (IOException | ParserConfigurationException | SAXException ex) {
            return Optional.empty();
        }

        String rootElementName = document.getDocumentElement().getTagName();

        if (!rootElementName.equals("persistence")) {
            return Optional.empty();
        }

        NodeList childNodes = document.getDocumentElement().getElementsByTagName("persistence-unit");

        if (childNodes.getLength() != 1) {
            return Optional.empty();
        }

        String pu = childNodes.item(0).getNodeName();

        if (!pu.equals("persistence-unit")) {
            return Optional.empty();
        }

        String puName = childNodes.item(0).getAttributes().getNamedItem("name").getNodeValue();

        if (puName != null && !puName.isEmpty()) {
            return Optional.of(puName);
        }

        return Optional.empty();
    }
}
