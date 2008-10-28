package com.agimatec.validation.xml;

import com.agimatec.validation.Validation;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.Serializable;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:34:46 <br/>
 * Copyright: Agimatec GmbH 2008
 */
@XStreamAlias("validator")
public class XMLMetaValidator implements Serializable {
    @XStreamAsAttribute
    private String id;
    @XStreamAsAttribute
    private String java;  // implementation of Validation

    @XStreamAsAttribute
    private String jsFunction; // name of java script function

    @XStreamOmitField
    private Validation validation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJava() {
        return java;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Validation getValidation() {
        return validation;
    }

    public String getJsFunction() {
        return jsFunction;
    }

    public void setJsFunction(String jsFunction) {
        this.jsFunction = jsFunction;
    }
}
