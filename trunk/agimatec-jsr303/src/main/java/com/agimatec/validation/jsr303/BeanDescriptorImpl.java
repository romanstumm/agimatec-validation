package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.Validation;

import javax.validation.BeanDescriptor;
import javax.validation.PropertyDescriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 25.02.2009 <br/>
 * Time: 16:54:16 <br/>
 * Copyright: Agimatec GmbH
 */
class BeanDescriptorImpl extends ElementDescriptorImpl implements BeanDescriptor {
    
    BeanDescriptorImpl(MetaBean metaBean, Validation[] validations) {
        super(metaBean, validations);
    }

    /**
     * Returns true if the bean involves validation:
     * - a constraint is hosted on the bean itself
     * - a constraint is hosted on one of the bean properties
     * - or a bean property is marked for cascade (@Valid)
     *
     * @return true if the bean nvolves validation
     */
    public boolean isBeanConstrained() {
        if (hasConstraints()) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getMetaBean() != null &&
                  mprop.getFeature(Features.Property.REF_CASCADE, true)) return true;
        }
        return false;
    }

    /**
     * Return the property level constraints for a given propertyName
     * or null if either the property does not exist or has no constraint
     * The returned object (and associated objects including ConstraintDescriptors)
     * are immutable.
     *
     * @param propertyName property evaludated
     */
    public PropertyDescriptor getConstraintsForProperty(String propertyName) {
        MetaProperty prop = metaBean.getProperty(propertyName);
        if (prop == null) return null;
        return getPropertyDescriptor(prop);
    }

    private PropertyDescriptor getPropertyDescriptor(MetaProperty prop) {
        PropertyDescriptorImpl edesc =
              prop.getFeature(Jsr303Features.Property.PropertyDescriptor);
        if (edesc == null) {
            edesc = new PropertyDescriptorImpl();
            edesc.setType(
                  prop.getFeature(Features.Property.REF_BEAN_TYPE, prop.getTypeClass()));
            edesc.setCascaded(prop.getFeature(Features.Property.REF_CASCADE, false));
            edesc.setPropertyPath(prop.getName());
            edesc.createConstraintDescriptors(prop.getValidations());
            prop.putFeature(Jsr303Features.Property.PropertyDescriptor, edesc);
        }
        return edesc;
    }

    /** return the property descriptors having at least a constraint defined */
    public Set<String> getConstrainedProperties() {
        Set<String> validatedProperties = new HashSet();
        for (MetaProperty prop : metaBean.getProperties()) {
            if (prop.getValidations().length > 0 || (prop.getMetaBean() != null &&
                  prop.getFeature(Features.Property.REF_CASCADE, true))) {
                validatedProperties.add(getPropertyDescriptor(prop).getPropertyName());
            }
        }
        return validatedProperties;
    }

    public String toString() {
        return "BeanDescriptorImpl{" + "returnType=" + type + '}';
    }
}
