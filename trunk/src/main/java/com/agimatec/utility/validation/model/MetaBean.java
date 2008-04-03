package com.agimatec.utility.validation.model;

import org.apache.commons.lang.ArrayUtils;

/**
 * Description: the meta description of a bean or class.
 * the class/bean itself can have a map of features and an array of metaproperties.<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 09:44:31 <br/>
 * Copyright: Agimatec GmbH 2008
 * @see MetaProperty
 */
public class MetaBean extends FeaturesCapable implements Cloneable, Features.Bean {
    private String id;
    private String name;
    private Class beanClass;
    private MetaProperty[] properties = new MetaProperty[0];

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public MetaProperty[] getProperties() {
        return properties;
    }

    public void setProperties(MetaProperty[] properties) {
        this.properties = properties;
    }

    public MetaProperty getProperty(String name) {
        for (MetaProperty p : properties) {
            if (name.equals(p.getName())) return p;
        }
        return null;
    }

    /**
     * @return true when at least one of the properties is a relationship 
     */
    public boolean hasRelationships() {
        for (MetaProperty p : properties) {
            if (p.isRelationship()) return true;
        }
        return false;
    }

    public boolean hasProperties() {
        return properties.length > 0;
    }

    public void putProperty(String name, MetaProperty property) {
        final MetaProperty oldProperty = getProperty(name);
        if (oldProperty == null) { // add
            if (properties.length == 0) {
                properties = new MetaProperty[1];
            } else {
                MetaProperty[] newproperties = new MetaProperty[properties.length + 1];
                System.arraycopy(properties, 0, newproperties, 0, properties.length);
                properties = newproperties;
            }
            properties[properties.length - 1] = property;
        } else { // replace
            int idx = ArrayUtils.indexOf(properties, oldProperty);
            properties[idx] = property;
        }
    }

    public String toString() {
        return "MetaBean{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", beanClass=" +
                beanClass + '}';
    }

    @Override
    protected <T extends FeaturesCapable> void copyInto(T target) {
        super.copyInto(target);
        final MetaBean copy = (MetaBean) target;
        if (properties != null) {
            copy.properties = properties.clone();
            for (int i = copy.properties.length - 1; i >= 0; i--) {
                copy.properties[i] = copy.properties[i].copy();
            }
        }
    }
}
