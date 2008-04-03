package com.agimatec.utility.validation;

import com.agimatec.utility.validation.model.FeaturesCapable;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Description: Context during validation to help the {@link Validation}
 * and the {@link BeanValidator} do their jobs <br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 12:30:01 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ValidationContext {
    private static final Object UNKNOWN = new Object();

    /** metainfo of current object */
    private MetaBean metaBean;
    /** current object */
    private Object bean;
    /** metainfo of current property */
    private MetaProperty metaProperty;
    /**
     * cached value of current property.
     * Cached because of potential redundant access for different Validations
     */
    private Object propertyValue = UNKNOWN;

    private IdentityHashMap validatedObjects = new IdentityHashMap();

    public ValidationContext() {
    }

    /**
     * add the object to the collection of validated objects to keep
     * track of them to avoid endless loops during validation.
     *
     * @return true when the object was not already validated in this context
     */
    public boolean collectValidated(Object object) {
        return validatedObjects.put(object, Boolean.TRUE) == null;
    }

    /** @return true when the object has already been validated in this context */
    public boolean isValidated(Object object) {
        return validatedObjects.containsKey(object);
    }

    /**
     * Clear map of validated objects (invoke when you want to 'reuse' the
     * context for different validations)
     */
    public void resetValidated() {
        validatedObjects.clear();
    }

    /** @return this */
    public ValidationContext setBean(Object aBean, MetaBean aMetaBean) {
        bean = aBean;
        metaBean = aMetaBean;
        metaProperty = null;
        unknownValue();
        return this;
    }

    /** get the value from the given reflection element */
    public Object getPropertyValue(AnnotatedElement element) {
        if (metaProperty == null) throw new IllegalStateException();
        try {
            if (element instanceof Field) {
                Field f = (Field)element;
                if(!f.isAccessible()) { f.setAccessible(true); }
                propertyValue = f.get(bean);
            } else if (element instanceof Method) {
                propertyValue = ((Method) element).invoke(bean);
            } else {
                return getPropertyValue();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot access " + metaProperty, e);
        }
        return propertyValue;
    }

    /**
     * get the cached value or access it somehow (via field or method)
     *
     * @return the current value of the property accessed by reflection
     * @throws IllegalArgumentException - error accessing attribute (config error, reflection problem)
     * @throws IllegalStateException    - when no property is currently set in the context (application logic bug)
     */
    public Object getPropertyValue() throws IllegalArgumentException, IllegalStateException {
        if (metaProperty == null) throw new IllegalStateException();
        if (propertyValue == UNKNOWN) {
            try {
                try {   // try public method
                    propertyValue = PropertyUtils.getSimpleProperty(bean, metaProperty.getName());
                } catch (NoSuchMethodException ex) {
                    try { // try public field
                        propertyValue = bean.getClass().getField(metaProperty.getName()).get(bean);
                    } catch (NoSuchFieldException ex2) {
                        // search for private/protected field up the hierarchy
                        Class theClass = bean.getClass();
                        while (theClass != null) {
                            try {
                                Field f = theClass.getDeclaredField(metaProperty.getName());
                                if(!f.isAccessible()) { f.setAccessible(true); }
                                propertyValue =f.get(bean);                                
                                break;
                            } catch (NoSuchFieldException ex3) {
                                // do nothing
                            }
                            theClass = theClass.getSuperclass();
                        }
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("cannot access " + metaProperty, e);
            }
        }
        return propertyValue;
    }

    /**
     * convenience method to access metaProperty.name
     *
     * @return null or the name of the current property
     */
    public String getPropertyName() {
        return metaProperty == null ? null : metaProperty.getName();
    }

    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * depending on whether we have a metaProperty or not,
     * this returns the metaProperty or otherwise the metaBean.
     * This is used to have a simple way to request features
     * in the Validation for both bean- and property-level validations.
     *
     * @return something that is capable to deliver features
     */
    public FeaturesCapable getMeta() {
        return (metaProperty == null) ? metaBean : metaProperty;
    }

    /**
     * mark the internal cachedValue as UNKNOWN.
     * This forces the ValidationContext to recompute the value
     * the next time it is accessed.
     * Use this method inside tests or when the propertyValue has been
     * changed during validation.
     */
    public void unknownValue() {
        propertyValue = UNKNOWN;
    }

    public MetaBean getMetaBean() {
        return metaBean;
    }

    public Object getBean() {
        return bean;
    }

    public MetaProperty getMetaProperty() {
        return metaProperty;
    }

    public void setMetaBean(MetaBean metaBean) {
        this.metaBean = metaBean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
        unknownValue();
    }

    public void setMetaProperty(MetaProperty metaProperty) {
        this.metaProperty = metaProperty;
        unknownValue();
    }

    public String toString() {
        return "ValidationContext{ bean=" + bean + ", metaProperty=" + metaProperty +
                ", propertyValue=" + propertyValue + '}';
    }

    protected void moveDown(MetaProperty prop) {
        setMetaProperty(prop);
        setBean(getPropertyValue(), prop.getMetaBean());
    }

    protected void moveUp(Object bean, MetaBean metaBean) {
        setBean(bean, metaBean); // reset context state
    }

}
