package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.Validation;

import javax.validation.BeanDescriptor;
import javax.validation.ConstraintDescriptor;
import javax.validation.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:23:45 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ElementDescriptorImpl implements BeanDescriptor, PropertyDescriptor {
    //    private ElementType elementType;
    private MetaBean metaBean;
    private boolean cascaded;
    private Class type;
    private Set<ConstraintDescriptor> constraintDescriptors;
    private String propertyPath;

    public ElementDescriptorImpl(MetaBean metaBean,
                                 Validation[] validations) {
        this.metaBean = metaBean;
        this.type = metaBean.getBeanClass();
        createConstraintDescriptors(validations);
    }

    public ElementDescriptorImpl() {
    }

    /*
     * @deprecated not part of ElementDescriptor interface anymore
     * not done: generate annotation when descriptor is based on XML
     * not done: what when both field and method of property are annotated? - elementType can vary for each constraintdescriptor?
     public ElementType getElementType() {
        return elementType;
    }
    */

    public Class getType() {
        return type;
    }

    public boolean isCascaded() {
        return cascaded;
    }

    public Set<ConstraintDescriptor> getConstraintDescriptors() {
        return constraintDescriptors;
    }

    /**
     * return true if at least one constraint declaration is present for the given bean
     * or if one property is marked for validation cascade
     */
    public boolean hasConstraints() {
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
            if (mprop.getMetaBean() != null &&
                    mprop.getFeature(Features.Property.REF_CASCADE, true)) return true;
        }
        return false;
    }

    public boolean isBeanConstrained() {
        return false;  // do nothing
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
        ElementDescriptorImpl edesc = prop.getFeature(Jsr303Features.Property.ElementDescriptor);
        if (edesc == null) {
            edesc = new ElementDescriptorImpl();
            edesc.setType(prop.getFeature(Features.Property.REF_BEAN_TYPE, prop.getTypeClass()));
            edesc.setCascaded(prop.getFeature(Features.Property.REF_CASCADE, false));
            edesc.setPropertyPath(propertyName);
            edesc.createConstraintDescriptors(prop.getValidations());
            prop.putFeature(Jsr303Features.Property.ElementDescriptor, edesc);
        }
        return edesc;
    }

    /** return the property names having at least a constraint defined */
    public Set<String> getConstrainedProperties() {
        Set<String> validatedProperties = new HashSet();
        for (MetaProperty prop : metaBean.getProperties()) {
            if (prop.getValidations().length > 0 || (prop.getMetaBean() != null &&
                    prop.getFeature(Features.Property.REF_CASCADE, true))) {
                validatedProperties.add(prop.getName());
            }
        }
        return validatedProperties;
    }

    private void createConstraintDescriptors(Validation[] validations) {
        setConstraintDescriptors(new HashSet(validations.length));
        for (Validation validation : validations) {
            if (validation instanceof ConstraintValidation) {
                ConstraintValidation cval = (ConstraintValidation) validation;
                getConstraintDescriptors().add(cval);
            }
        }
    }

    public String getPropertyName() {
        return propertyPath;
    }

    public void setCascaded(boolean cascaded) {
        this.cascaded = cascaded;
    }

    public void setConstraintDescriptors(Set<ConstraintDescriptor> constraintDescriptors) {
        this.constraintDescriptors = constraintDescriptors;
    }

    /* public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }*/

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public void setType(Class returnType) {
        this.type = returnType;
    }

    public String toString() {
        return "ElementDescriptorImpl{" + "returnType=" + type + ", propertyPath='" +
                propertyPath + '\'' + '}';
    }
}
