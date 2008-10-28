package com.agimatec.validation.model;

import com.agimatec.validation.MetaBeanFinder;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 11:04:21 <br/>
 * Copyright: Agimatec GmbH
 */
public class DynamicMetaBean extends MetaBean {
    private final MetaBeanFinder finder;

    public DynamicMetaBean(MetaBeanFinder finder) {
        this.finder = finder;
    }

    /**
     * different strategies with hints to find MetaBean of associated object can
     * be implemented here.
     * @param bean
     */
    public MetaBean resolveMetaBean(Object bean) {
        return finder.findForClass(bean.getClass());
    }
}
