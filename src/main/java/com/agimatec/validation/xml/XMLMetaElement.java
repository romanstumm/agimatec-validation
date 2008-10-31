package com.agimatec.validation.xml;

import static com.agimatec.validation.model.Features.Property.*;
import com.agimatec.validation.model.MetaProperty;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.apache.commons.lang.ClassUtils;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:48:36 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class XMLMetaElement extends XMLFeaturesCapable {

    @XStreamAsAttribute()
    private String name;
    @XStreamAsAttribute()
    private String mandatory;

    @XStreamAsAttribute()
    private Integer minLength;
    @XStreamAsAttribute()
    private Integer maxLength;
    @XStreamAsAttribute()
    private Boolean readonly;
    @XStreamAsAttribute()
    private Boolean hidden;
    @XStreamAsAttribute()
    private Boolean denied;
    /**
     * normally the type is determined by the implementation class.
     * in case, no implementation class is given, the xml can
     * contain the type directly.
     */
    @XStreamAsAttribute()
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public Boolean getDenied() {
        return denied;
    }

    public void setDenied(Boolean denied) {
        this.denied = denied;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void mergeInto(MetaProperty prop) throws ClassNotFoundException {
        mergeFeaturesInto(prop);
        if (getType() != null && getType().length() > 0) {
            prop.setType(ClassUtils.getClass(getType()));
        }
        if (getHidden() != null) {
            prop.putFeature(HIDDEN, getHidden().booleanValue());
        }
        if (getMandatory() != null) {
            prop.putFeature(MANDATORY, getMandatory().equals("true"));
        }
        if (getMaxLength() != null) {
            prop.putFeature(MAX_LENGTH, getMaxLength());
        }
        if (getMinLength() != null) {
            prop.putFeature(MIN_LENGTH, getMinLength());
        }
        if (getReadonly() != null) {
            prop.putFeature(READONLY, getReadonly());
        }
        if (getDenied() != null) {
            prop.putFeature(DENIED, getDenied());
        }
    }
}
