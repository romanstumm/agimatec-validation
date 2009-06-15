package com.agimatec.validation.jsr303;

import javax.validation.PropertyDescriptor;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 25.02.2009 <br/>
 * Time: 16:54:37 <br/>
 * Copyright: Agimatec GmbH
 */
class PropertyDescriptorImpl extends ElementDescriptorImpl implements PropertyDescriptor {
    private boolean cascaded;
    private String propertyPath;

    public void setCascaded(boolean cascaded) {
        this.cascaded = cascaded;
    }

    public boolean isCascaded() {
        return cascaded;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getPropertyName() {
        return propertyPath;
    }

    public String toString() {
        return "PropertyDescriptorImpl{" + "returnType=" + type + ", propertyPath='" +
              propertyPath + '\'' + '}';
    }
}
