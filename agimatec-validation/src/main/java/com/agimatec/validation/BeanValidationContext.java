package com.agimatec.validation;

import com.agimatec.validation.model.*;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

/**
 * Description: Context during validation to help the {@link com.agimatec.validation.model.Validation}
 * and the {@link BeanValidator} do their jobs.
 * Used to bundle {@link BeanValidationContext} and {@link ValidationListener}
 * together <br/>
 * <b>This class is NOT thread-safe: a new instance will be created for each
 * validation
 * processing per thread.<br/></b>
 * <br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 12:30:01 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class BeanValidationContext implements ValidationContext {
    /** represent an unknown propertyValue. */
    private static final Object UNKNOWN = new Object();

    /** metainfo of current object. */
    private MetaBean metaBean;
    /** current object. */
    private Object bean;
    /** metainfo of current property. */
    private MetaProperty metaProperty;
    /**
     * cached value of current property.
     * Cached because of potential redundant access for different Validations
     */
    private Object propertyValue = UNKNOWN;

    /** field to retrieve value, if null use method or find field dynamically */
    private Field field;

    /** set of objects already validated to avoid endless loops. */
    protected IdentityHashMap validatedObjects = new IdentityHashMap();

    /**
     * true when value is fixed, so that it will NOT be dynamically
     * determined from the annotated element or the metaProperty.
     * <b><br>Note: When value is UNKNOWN, it will be determined THE FIRST TIME
     * IT IS ACCESSED.</b>
     */
    private boolean fixed;

    /** listener notified of validation constraint violations. */
    private ValidationListener listener;

    public BeanValidationContext(ValidationListener listener) {
        this.listener = listener;
    }

    public ValidationListener getListener() {
        return listener;
    }

    public void setListener(ValidationListener listener) {
        this.listener = listener;
    }

    /**
     * add the object to the collection of validated objects to keep
     * track of them to avoid endless loops during validation.
     *
     * @return true when the object was not already validated in this context
     */
    public boolean collectValidated() {
        return validatedObjects.put(getBean(), Boolean.TRUE) == null;
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

    public void setBean(Object aBean, MetaBean aMetaBean) {
        bean = aBean;
        metaBean = aMetaBean;
        metaProperty = null;
        unknownValue();
    }

    /**
     * get the cached value or access it somehow (via field or method)
     *
     * @return the current value of the property accessed by reflection
     * @throws IllegalArgumentException - error accessing attribute (config error, reflection problem)
     * @throws IllegalStateException    - when no property is currently set in the context (application logic bug)
     */
    public Object getPropertyValue() {
        return getPropertyValue(field);
    }

    /** get the value from the given reflection element */
    public Object getPropertyValue(Field theField)
          throws IllegalArgumentException, IllegalStateException {
        if (propertyValue == UNKNOWN || (this.field != theField && !fixed)) {
            if (metaProperty == null) throw new IllegalStateException();
            this.field = theField;
            if (theField != null) {
                try {
                    if (!theField.isAccessible()) {
                        // enable access of private/protected field
                        theField.setAccessible(true);
                    }
                    propertyValue = theField.get(bean);
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                          "cannot access " + metaProperty + " on " + bean, e);
                }
                return propertyValue;
            } else {
                try {
                    try {   // try public method
                        propertyValue = PropertyUtils
                              .getSimpleProperty(bean, metaProperty.getName());
                    } catch (NoSuchMethodException ex) {
                        try { // try public field
                            Field aField = bean.getClass().getField(metaProperty.getName());
                            propertyValue = aField.get(bean);
                            this.field = aField;
                        } catch (NoSuchFieldException ex2) {
                            // search for private/protected field up the hierarchy
                            Class theClass = bean.getClass();
                            while (theClass != null) {
                                try {
                                    Field aField = theClass
                                          .getDeclaredField(metaProperty.getName());
                                    if (!aField.isAccessible()) {
                                        aField.setAccessible(true);
                                    }
                                    propertyValue = aField.get(bean);
                                    this.field = aField;
                                    break;
                                } catch (NoSuchFieldException ex3) {
                                    // do nothing
                                }
                                theClass = theClass.getSuperclass();
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("cannot access " + metaProperty,
                          e);
                }
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

    public void setFixedValue(Object value) {
        setPropertyValue(value);
        fixed = true;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
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
     * This forces the BeanValidationContext to recompute the value
     * the next time it is accessed.
     * Use this method inside tests or when the propertyValue has been
     * changed during validation.
     */
    public void unknownValue() {
        propertyValue = UNKNOWN;
        field = null;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
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
        return "BeanValidationContext{ bean=" + bean + ", metaProperty=" + metaProperty +
              ", propertyValue=" + propertyValue + '}';
    }

    public void moveDown(MetaProperty prop) {
        setMetaProperty(prop);
        // TODO RSt - Fix bug: field-based access depending on element annotated with @Valid
        setBean(getPropertyValue(), prop.getMetaBean());
    }

    public void moveUp(Object bean, MetaBean aMetaBean) {
        setBean(bean, aMetaBean); // reset context state
    }

    public void setCurrentIndex(int index) {
        // do nothing
    }

    public void setCurrentKey(Object key) {
        // do nothing
    }

}
