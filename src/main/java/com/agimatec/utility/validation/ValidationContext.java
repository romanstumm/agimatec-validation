package com.agimatec.utility.validation;

import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;

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

    /**
     * @param aBean
     * @param aMetaBean
     * @return this
     */
    void setBean(Object aBean, MetaBean aMetaBean);

    void setMetaProperty(MetaProperty metaProperty);

    /**
     * step deeper into association at 'prop'
     * @param prop
     */
    void moveDown(MetaProperty prop);

    /**
     * step out from a validation of associated objects.
     * @param bean
     * @param metaBean
     */
    void moveUp(Object bean, MetaBean metaBean);
}
