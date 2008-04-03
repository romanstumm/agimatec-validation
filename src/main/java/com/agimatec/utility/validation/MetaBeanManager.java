package com.agimatec.utility.validation;

import static com.agimatec.utility.validation.model.Features.Property.*;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;
import com.agimatec.utility.validation.xml.XMLMetaBeanInfos;
import com.agimatec.utility.validation.xml.XMLMetaBeanLoader;
import com.agimatec.utility.validation.xml.XMLMetaBeanRegistry;
import com.agimatec.utility.validation.xml.XMLMetaBeanURLLoader;

import java.util.Map;

/**
 * Description: Default implementation for the interface to find, register and
 * create MetaBeans. In most situations a single instance of this class is
 * sufficient and you can get this instance from the {@link MetaBeanManagerFactory}.
 * <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 16:19:43 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class MetaBeanManager implements MetaBeanFinder, XMLMetaBeanRegistry, MetaBeanEnricher {

    protected final MetaBeanCache cache = new MetaBeanCache();
    protected final MetaBeanBuilder builder;
    private boolean complete = false;

    public MetaBeanManager() {
        builder = new MetaBeanBuilder();
    }

    public MetaBeanManager(MetaBeanBuilder builder) {
        this.builder = builder;
    }

    public void addResourceLoader(String resource) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        if (classloader == null) classloader = getClass().getClassLoader();
        addLoader(new XMLMetaBeanURLLoader(classloader.getResource(resource)));
    }

    public synchronized void addLoader(XMLMetaBeanLoader loader) {
        builder.addLoader(loader);
        cache.clear(); // clear because new loaders can affect ALL MetaBeans already created!
        complete = false;
    }

    public MetaBeanBuilder getBuilder() {
        return builder;
    }

    public MetaBeanCache getCache() {
        return cache;
    }

    /**
     * @return all MetaBeans for classes that have a xml descriptor:
     *         key = bean.id, value = MetaBean
     */
    public Map<String, MetaBean> findAll() {
        if (!complete) {
            try {
                Map<String, MetaBean> allBuilt = builder.buildAll();
                for (MetaBean meta : allBuilt.values()) {
                    MetaBean cached = cache.findForId(meta.getId());
                    if (cached == null) {
                        cache.cache(meta);
                    }
                }
                Map<String, MetaBean> map = cache.findAll();
                for (Object oentry : map.values()) {
                    MetaBean meta = (MetaBean) oentry;
                    computeRelationships(meta, map);
                }
                complete = true;
                return map;
            } catch (Exception e) {
                throw new IllegalArgumentException("error creating beanInfos", e);
            }
        } else {
            return cache.findAll();
        }
    }

    /**
     * @param infos - the patches to apply
     * @return all MetaBeans for classes that have a xml descriptor and
     *         additional the MetaBeans loaded by the given loaders.
     *         The given loaders may also return patches for MetaBeans that have
     *         also been returned by other loaders. The beans with patches for
     *         references to patched beans will be copied.
     */
    public Map<String, MetaBean> enrichCopies(XMLMetaBeanInfos... infos) {
        Map<String, MetaBean> cached = findAll();
        try {
            Map<String, MetaBean> patched = builder.enrichCopies(cached, infos);
            for (Object oentry : patched.values()) {
                MetaBean meta = (MetaBean) oentry;
                computeRelationships(meta, patched);
            }
            return patched;
        } catch (Exception e) {
            throw new IllegalArgumentException("error enriching beanInfos", e);
        }
    }

    public MetaBean findForId(String beanInfoId) {
        MetaBean beanInfo = cache.findForId(beanInfoId);
        if (beanInfo != null) return beanInfo;
        try {
            beanInfo = builder.buildForId(beanInfoId);
            cache.cache(beanInfo);
            computeRelationships(beanInfo);
            return beanInfo;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("error creating beanInfo with id: " + beanInfoId, e);
        }
    }

    public MetaBean findForClass(Class clazz) {
        MetaBean beanInfo = cache.findForClass(clazz);
        if (beanInfo != null) return beanInfo;
        try {
            beanInfo = builder.buildForClass(clazz);
            cache.cache(beanInfo);
            computeRelationships(beanInfo);
            return beanInfo;
        } catch (Exception e) {
            throw new IllegalArgumentException("error creating beanInfo for " + clazz, e);
        }
    }

    /**
     * must be called AFTER cache.cache()
     * to avoid endless loop
     */
    protected void computeRelationships(MetaBean beanInfo) {
        for (MetaProperty prop : beanInfo.getProperties()) {
            String beanRef = (String) prop.getFeature(REF_BEAN_ID);
            if (beanRef != null) {
                prop.setMetaBean(findForId(beanRef));
            } else {
                Class beanType = prop.getFeature(REF_BEAN_TYPE);
                if (beanType != null) {
                    prop.setMetaBean(findForClass(beanType));
                } else if (prop.getFeature(REF_CASCADE, false)) {
                    prop.setMetaBean(findForClass(prop.getType()));
                }
            }
        }
    }

    private void computeRelationships(MetaBean beanInfo, Map<String, MetaBean> cached) {
        for (MetaProperty prop : beanInfo.getProperties()) {
            String beanRef = (String) prop.getFeature(REF_BEAN_ID);
            if (beanRef != null) {
                prop.setMetaBean(cached.get(beanRef));
            }
        }
    }
}
