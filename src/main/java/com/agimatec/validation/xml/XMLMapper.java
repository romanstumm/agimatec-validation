package com.agimatec.validation.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:54:13 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class XMLMapper {
    private static final XMLMapper instance = new XMLMapper();

    private final XStream xStream;

    private XMLMapper() {
        xStream = new XStream();
        Annotations.configureAliases(xStream, XMLFeaturesCapable.class,
                XMLMetaFeature.class, XMLMetaBean.class, XMLMetaBeanInfos.class,
                XMLMetaBeanReference.class, XMLMetaElement.class, XMLMetaProperty.class,
                XMLMetaValidator.class, XMLMetaValidatorReference.class);
        xStream.setMode(XStream.NO_REFERENCES);
    }

    public static XMLMapper getInstance() {
        return instance;
    }

    public XStream getXStream() {
        return xStream;
    }
}
