package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.util.PrivilegedActions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.validation.ValidationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * Description: uses jaxb to parse validation.xml<br/>
 * User: roman <br/>
 * Date: 24.11.2009 <br/>
 * Time: 16:48:55 <br/>
 * Copyright: Agimatec GmbH
 */
public class ValidationParser {
    private static final String VALIDATION_XML_FILE = "META-INF/validation.xml";
    private static final String VALIDATION_CONFIGURATION_XSD =
          "META-INF/validation-configuration-1.0.xsd";
    private static final Log log = LogFactory.getLog(ValidationParser.class);


    ValidationConfigType getValidationConfig() {
        try {
            InputStream inputStream = getInputStream();
            if (inputStream == null) {
                if(log.isDebugEnabled())
                    log.debug("No " + VALIDATION_XML_FILE +
                      " found. Using annotation based configuration only.");
                return null;
            }

            if (log.isInfoEnabled()) log.info(VALIDATION_XML_FILE + " found.");

            Schema schema = getValidationConfigurationSchema();
            JAXBContext jc = JAXBContext.newInstance(ValidationConfigType.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
            StreamSource stream = new StreamSource(inputStream);
            JAXBElement<ValidationConfigType> root =
                  unmarshaller.unmarshal(stream, ValidationConfigType.class);
            return root.getValue();
        } catch (JAXBException e) {
            log.error("Error parsing " + VALIDATION_XML_FILE, e);
            throw new ValidationException("Unable to parse " + VALIDATION_XML_FILE);
        } catch (IOException e) {
            log.error("Error looking for " + VALIDATION_XML_FILE, e);
            throw new ValidationException("Unable to parse " + VALIDATION_XML_FILE);
        }

    }

    private InputStream getInputStream() throws IOException {
        ClassLoader loader = PrivilegedActions.getClassLoader(getClass());
        Enumeration<URL> urls = loader.getResources(VALIDATION_XML_FILE);
        if (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (urls.hasMoreElements()) {
                // spec says: If more than one META-INF/validation.xml file
                // is found in the classpath, a ValidationException is raised.
                throw new ValidationException(
                      "More than one " + VALIDATION_XML_FILE + " is found in the classpath");
            }
            return url.openStream();
        } else {
            return null;
        }
    }

    private Schema getValidationConfigurationSchema() {
        ClassLoader loader = PrivilegedActions.getClassLoader(getClass());
        URL schemaUrl = loader.getResource(VALIDATION_CONFIGURATION_XSD);
        SchemaFactory sf =
              SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try {
            schema = sf.newSchema(schemaUrl);
        } catch (SAXException e) {
            log.warn("Unable to create schema for " + VALIDATION_CONFIGURATION_XSD, e);
        }
        return schema;
    }
}
