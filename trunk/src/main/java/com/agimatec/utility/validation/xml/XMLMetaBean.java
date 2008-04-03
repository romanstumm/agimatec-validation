package com.agimatec.utility.validation.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:25:19 <br/>
 *
 */
@XStreamAlias("bean")
public class XMLMetaBean extends XMLFeaturesCapable {
    @XStreamAsAttribute()
    private String id;
    @XStreamAsAttribute()
    private String name;
    @XStreamAsAttribute()
    private String impl;
    @XStreamImplicit
    private List<XMLMetaProperty> properties;
    @XStreamImplicit
    private List<XMLMetaBeanReference> beanRelations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public List<XMLMetaProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<XMLMetaProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(XMLMetaProperty property) {
        if (properties == null) properties = new ArrayList();
        properties.add(property);
    }

    public void putProperty(XMLMetaProperty property) {
        if (property.getName() != null) {
            XMLMetaProperty prop = findProperty(property.getName());
            if (prop != null) {
                properties.remove(prop);
            }
        }
        addProperty(property);
    }

    public XMLMetaProperty removeProperty(String name) {
        XMLMetaProperty prop = findProperty(name);
        if (prop != null) {
            properties.remove(prop);
        }
        return prop;
    }

    public XMLMetaProperty getProperty(String name) {
        return findProperty(name);
    }

    private XMLMetaProperty findProperty(String name) {
        if (properties == null) return null;
        for (XMLMetaProperty prop : properties) {
            if (name.equals(prop.getName())) return prop;
        }
        return null;
    }

    public List<XMLMetaBeanReference> getBeanRefs() {
        return beanRelations;
    }

    public void setBeanRefs(List<XMLMetaBeanReference> beanRelations) {
        this.beanRelations = beanRelations;
    }


    public void addBeanRef(XMLMetaBeanReference beanRelation) {
        if (beanRelations == null) beanRelations = new ArrayList();
        beanRelations.add(beanRelation);
    }

    public void putBeanRef(XMLMetaBeanReference beanRelation) {
        if (beanRelation.getName() != null) {
            XMLMetaBeanReference relation = findBeanRef(beanRelation.getName());
            if (relation != null) {
                beanRelations.remove(relation);
            }
        }
        addBeanRef(beanRelation);
    }

    public XMLMetaBeanReference removeBeanRef(String name) {
        XMLMetaBeanReference relation = findBeanRef(name);
        if (relation != null) {
            beanRelations.remove(relation);
        }
        return relation;
    }

    public XMLMetaBeanReference getBeanRef(String name) {
        return findBeanRef(name);
    }

    private XMLMetaBeanReference findBeanRef(String name) {
        if (beanRelations == null) return null;
        for (XMLMetaBeanReference relation : beanRelations) {
            if (name.equals(relation.getName())) return relation;
        }
        return null;
    }

}
