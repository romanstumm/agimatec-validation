package com.agimatec.utility.validation.jsr303;

import javax.validation.ConstraintDescriptor;
import javax.validation.ElementDescriptor;
import java.lang.annotation.ElementType;
import java.util.Set;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:23:45 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class ElementDescriptorImpl implements ElementDescriptor {
    private ElementType elementType;
    private Class returnType;
    private boolean cascaded;
    private Set<ConstraintDescriptor> constraintDescriptors;
    private String propertyPath;

    /**
     * TODO RSt - generate annotation when descriptor is based on XML
     * TODO RSt - what when both field and method of property are annotated? - elementType can vary for each constraintdescriptor?
     */
    public ElementType getElementType() {
        return elementType;
    }

    public Class getReturnType() {
        return returnType;
    }

    public boolean isCascaded() {
        return cascaded;
    }

    public Set<ConstraintDescriptor> getConstraintDescriptors() {
        return constraintDescriptors;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setCascaded(boolean cascaded) {
        this.cascaded = cascaded;
    }

    public void setConstraintDescriptors(Set<ConstraintDescriptor> constraintDescriptors) {
        this.constraintDescriptors = constraintDescriptors;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public void setReturnType(Class returnType) {
        this.returnType = returnType;
    }

    public String toString() {
        return "ElementDescriptorImpl{" + "returnType=" + returnType + ", propertyPath='" +
                propertyPath + '\'' + '}';
    }
}
