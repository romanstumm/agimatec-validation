package com.agimatec.utility.validation.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 15:32:13 <br/>
 * Copyright: Agimatec GmbH 2008
 */
@XStreamAlias("feature")
public class XMLMetaFeature implements Serializable {
    @XStreamAsAttribute
    private String key;
    @XStreamAsAttribute
    private Object value;

    public XMLMetaFeature(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public XMLMetaFeature() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
