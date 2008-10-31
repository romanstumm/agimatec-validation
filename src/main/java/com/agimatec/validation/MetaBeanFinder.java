package com.agimatec.validation;

import com.agimatec.validation.model.MetaBean;

import java.util.Map;

/**
 * Description: Interface to find BeanInfos <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 16:17:20 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public interface MetaBeanFinder {
    /**
     * @param beanInfoId - symbolic unique name of Meta Info
     * @return BeanInfo
     * @throws IllegalArgumentException - when MetaBean not found
     */
    MetaBean findForId(String beanInfoId);

    /**
     * @param clazz - bean class
     * @return BeanInfo (never null)
     */
    MetaBean findForClass(Class clazz);

    /**
     * @return all MetaBeans for classes that have a xml descriptor:
     *         key = bean.id, value = MetaBean
     */
    public Map<String, MetaBean> findAll();
}
