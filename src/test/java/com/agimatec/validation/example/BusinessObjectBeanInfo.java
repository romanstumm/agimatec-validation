package com.agimatec.validation.example;

import com.agimatec.validation.model.Features;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Copyright: Agimatec GmbH 2008
 */
public class BusinessObjectBeanInfo extends SimpleBeanInfo {
    Class targetClass = BusinessObject.class;

    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        ExplicitBeanInfo bi = new ExplicitBeanInfo();
        bi.setPropertyDescriptors(_getPropertyDescriptors());
        return new BeanInfo[]{bi};
    }

    public PropertyDescriptor[] _getPropertyDescriptors() {
        try {
            PropertyDescriptor numericValue = new PropertyDescriptor("numericValue",
                    targetClass, "getNumericValue", "setNumericValue");
            numericValue.setValue(Features.Property.MAX_VALUE, new Integer(100));
            numericValue.setValue(Features.Property.MIN_VALUE, new Integer(-100));
            return new PropertyDescriptor[]{numericValue};
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

class ExplicitBeanInfo extends SimpleBeanInfo {
    private PropertyDescriptor[] propertyDescriptors;

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return propertyDescriptors;
    }

    public void setPropertyDescriptors(PropertyDescriptor[] propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors;
    }
}