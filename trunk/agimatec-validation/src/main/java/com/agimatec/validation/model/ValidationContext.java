package com.agimatec.validation.model;

import com.agimatec.validation.util.AccessStrategy;

import java.lang.reflect.Field;

/**
 * Description: Interface of the context that holds all state information
 * during the validation process<br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 09:36:02 <br/>
 * Copyright: Agimatec GmbH
 */
public interface ValidationContext {
    Object getPropertyValue();

    @Deprecated
    Object getPropertyValue(Field field);

    /** get the value by using the given access strategy and cache it */
    Object getPropertyValue(AccessStrategy access);

    String getPropertyName();

    ValidationListener getListener();

    Object getBean();

    MetaBean getMetaBean();

    void setMetaBean(MetaBean metaBean);

    MetaProperty getMetaProperty();

    void setBean(Object bean);

    boolean collectValidated();

    void setBean(Object aBean, MetaBean aMetaBean);

    void setMetaProperty(MetaProperty metaProperty);

    /** step deeper into association at 'prop' */
    void moveDown(MetaProperty prop, AccessStrategy access);

    /** step out from a validation of associated objects. */
    void moveUp(Object bean, MetaBean metaBean);

    /**
     * set the index of the object currently validated into the context.
     * used to create the propertyPath with [index] information for collections.
     */
    void setCurrentIndex(int index);

    /**
     * set the key of the object in a map currently validated into the context.
     * used to create the propertyPath with [key] information for maps.
     */
    void setCurrentKey(Object key);
}
