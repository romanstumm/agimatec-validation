package com.agimatec.validation;

import com.agimatec.validation.model.MetaBean;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 28.04.2008 <br/>
 * Time: 11:04:21 <br/>
 * Copyright: Agimatec GmbH
 */
final class DynamicMetaBean extends MetaBean {
    private final MetaBeanFinder finder;

    public DynamicMetaBean(MetaBeanFinder finder) {
        this.finder = finder;
    }

    /**
     * different strategies with hints to find MetaBean of associated object can
     * be implemented here.
     */
    @Override
    public MetaBean resolveMetaBean(Object bean) {
        return bean instanceof Class ?
                finder.findForClass((Class) bean) : finder.findForClass(bean.getClass());
    }
}
