package com.agimatec.utility.validation;

import com.agimatec.utility.validation.model.MetaBean;
import org.apache.commons.collections.FastHashMap;

import java.io.Serializable;
import java.util.Map;

/**
 * Description: a cache to hold metabeans by id and by class.<br/>
 * User: roman.stumm <br/>
 * Date: 18.02.2008 <br/>
 * Time: 11:38:53 <br/>
 *
 */
public class MetaBeanCache implements MetaBeanFinder, Serializable {
    protected final FastHashMap cacheById;
    protected final FastHashMap cacheByClass;

    public MetaBeanCache() {
        this.cacheById = new FastHashMap();
        cacheByClass = new FastHashMap();
        cacheByClass.setFast(true);
        cacheById.setFast(true);
    }

    public MetaBeanCache(Map<String, MetaBean> beans) {
        this();
        for (MetaBean bean : beans.values()) {
            cache(bean);
        }
    }

    public void clear() {
        cacheById.clear();
        cacheByClass.clear();
    }

    public MetaBean findForId(String beanInfoId) {
        return (MetaBean) cacheById.get(beanInfoId);
    }

    public MetaBean findForClass(Class clazz) {
        return (MetaBean) cacheByClass.get(clazz);
    }

    public Map<String, MetaBean> findAll() {
        return cacheById;
    }

    public void cache(MetaBean beanInfo) {
        cacheById.put(beanInfo.getId(), beanInfo);
        if (beanInfo.getBeanClass() != null &&
                beanInfo.getId().equals(beanInfo.getBeanClass().getName())) {
            cacheByClass.put(beanInfo.getBeanClass(), beanInfo);
        }
    }

    public void removeFromCache(MetaBean beanInfo) {
        cacheById.remove(beanInfo.getId());
        if (beanInfo.getBeanClass() != null &&
                beanInfo.getId().equals(beanInfo.getBeanClass().getName())) {
            cacheByClass.remove(beanInfo.getBeanClass());
        }
    }

}
