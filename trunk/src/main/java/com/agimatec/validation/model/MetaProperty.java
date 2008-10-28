package com.agimatec.validation.model;

/**
 * Description: the meta description of a property of a bean. it supports a map
 * of features and multiple validations<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 09:58:57 <br/>
 * Copyright: Agimatec GmbH 2008
 * @see com.agimatec.validation.Validation
 * @see MetaBean
 */
public class MetaProperty extends FeaturesCapable implements Cloneable, Features.Property {
    private String name;

    private Class<?> type;
    private MetaBean metaBean;

    /** the meta info of the target bean (mainly for relationships) */
    public MetaBean getMetaBean() {
        return metaBean;
    }

    public void setMetaBean(MetaBean metaBean) {
        this.metaBean = metaBean;
    }

    public boolean isRelationship() {
        return metaBean != null;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isMandatory() {
        return getFeature(MANDATORY, Boolean.FALSE).booleanValue();
    }

    public void setMandatory(boolean mandatory) {
        putFeature(MANDATORY, Boolean.valueOf(mandatory));
    }

    public String[] getJavaScriptValidations() {
        return getFeature(JAVASCRIPT_VALIDATION_FUNCTIONS);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "MetaProperty{" + "name='" + name + '\'' + ", type=" + type + '}';
    }
}
