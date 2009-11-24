/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.util.PrivilegedActions;
import org.xml.sax.SAXException;

import javax.validation.ValidationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;


/**
 * Uses JAXB to parse constraints.xml based on validation-mapping-1.0.xsd.<br>
 * Copyright: Agimatec GmbH, 2009
 */
public class ValidationMappingParser {

    private static final String VALIDATION_MAPPING_XSD = "META-INF/validation-mapping-1.0.xsd";

    /** @param xmlStreams One or more contraints.xml file streams to parse */
    public void parse(Set<InputStream> xmlStreams) throws ValidationException {
        for (InputStream in : xmlStreams) {
            ConstraintMappingsType mappings = getMappings(in);
            // TODO - finish

        }
    }

    /** @param in XML stream to parse using the validation-mapping-1.0.xsd */
    private ConstraintMappingsType getMappings(InputStream in) {
        ConstraintMappingsType mappings;
        try {
            JAXBContext jc = JAXBContext.newInstance(ConstraintMappingsType.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(getSchema());
            StreamSource stream = new StreamSource(in);
            JAXBElement<ConstraintMappingsType> root =
                  unmarshaller.unmarshal(stream, ConstraintMappingsType.class);
            mappings = root.getValue();
        } catch (JAXBException e) {
            throw new ValidationException("Failed to parse XML deployment descriptor file.",
                  e);
        }
        return mappings;
    }

    /** @return validation-mapping-1.0.xsd based schema */
    private Schema getSchema() {
        Schema schema;
        URL schemaUrl =
              PrivilegedActions.getClassLoader(getClass()).getResource(VALIDATION_MAPPING_XSD);
        SchemaFactory sf =
              SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = sf.newSchema(schemaUrl);
        } catch (SAXException e) {
            throw new ValidationException("Failed to parse schema.", e);
        }
        return schema;
    }

    // TODO - finish....

}
