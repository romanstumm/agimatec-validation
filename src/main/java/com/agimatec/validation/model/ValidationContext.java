package com.agimatec.validation.model;

import java.lang.reflect.Field;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 09:36:02 <br/>
 * Copyright: Agimatec GmbH
 */
public interface ValidationContext {
    ValidationListener getListener();

    Object getPropertyValue() throws IllegalArgumentException, IllegalStateException;

    String getPropertyName();

    Object getPropertyValue(Field field);

    Object getBean();

    MetaBean getMetaBean();

    void setMetaBean(MetaBean metaBean);

    MetaProperty getMetaProperty();

    void setBean(Object bean);

    boolean collectValidated(Object object);

    /** @return this */
    void setBean(Object aBean, MetaBean aMetaBean);

    void setMetaProperty(MetaProperty metaProperty);

    /** step deeper into association at 'prop' */
    void moveDown(MetaProperty prop);

    /** step out from a validation of associated objects. */
    void moveUp(Object bean, MetaBean metaBean);

    /**
     * set the index of the object currently validated into the context.
     * used to create the propertyPath with [index] information for collections.
     */
    void setCurrentIndex(int index);
}
