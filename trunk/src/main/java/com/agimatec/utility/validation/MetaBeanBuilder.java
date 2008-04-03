package com.agimatec.utility.validation;

import static com.agimatec.utility.validation.model.Features.Property.*;
import com.agimatec.utility.validation.model.FeaturesCapable;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;
import com.agimatec.utility.validation.routines.StandardValidation;
import com.agimatec.utility.validation.xml.*;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;

/**
 * Description: internal implementation class to construct metabeans from xml model<br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 16:26:30 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class MetaBeanBuilder {
    private static final Log log = LogFactory.getLog(MetaBeanBuilder.class);
    // use LinkedHashMap to keep sequence of loaders
    private final Map<XMLMetaBeanLoader, XMLMetaBeanInfos> resources =
            new LinkedHashMap();

    private StandardValidation standardValidation = StandardValidation.getInstance();

    /** XMLMetaBeanLoader are used to know "locations" where to get BeanInfos from. */
    public Collection<XMLMetaBeanLoader> getLoaders() {
        return resources.keySet();
    }

    public void addLoader(XMLMetaBeanLoader loader) {
        resources.put(loader, null);
    }

    public StandardValidation getStandardValidation() {
        return standardValidation;
    }

    /** customize the implementation of standardValidation for this builder. */
    public void setStandardValidation(StandardValidation standardValidation) {
        this.standardValidation = standardValidation;
    }

    public Map<String, MetaBean> buildAll() throws Exception {
        final Map<String, MetaBean> all = new HashMap<String, MetaBean>();
        visitXMLBeanMeta(null, new Visitor() {
            public void visit(XMLMetaBean empty, XMLMetaBeanInfos xmlInfos)
                    throws Exception {
                if (xmlInfos.getBeans() == null) return; // empty file, ignore

                for (XMLMetaBean xmlMeta : xmlInfos.getBeans()) {
                    MetaBean meta = all.get(xmlMeta.getId());
                    if (meta == null) {
                        meta = createMetaBean(xmlMeta);
                        all.put(xmlMeta.getId(), meta);
                    }
                    enrichMetaBean(meta, new XMLResult(xmlMeta, xmlInfos));
                }
            }

            public MetaBean getMetaBean() {
                return null;  // do nothing
            }
        });
        return all;
    }

    public Map<String, MetaBean> enrichCopies(Map<String, MetaBean> all,
                                              XMLMetaBeanInfos... infos)
            throws Exception {
        final Map<String, MetaBean> copies = new HashMap<String, MetaBean>(all.size());
        boolean nothing = true;
        for (XMLMetaBeanInfos each : infos) {
            if (each == null) continue;
            try {
                for (XMLMetaBean xmlMeta : each.getBeans()) {
                    nothing = false;
                    MetaBean copy = copies.get(xmlMeta.getId());
                    if (copy == null) { // ist noch nicht kopiert
                        MetaBean meta = all.get(xmlMeta.getId());
                        if (meta == null) { // gibt es nicht
                            copy = createMetaBean(xmlMeta);
                        } else { // gibt es, jetzt kopieren
                            copy = meta.copy();
                        }
                        copies.put(xmlMeta.getId(), copy);
                    }
                    enrichMetaBean(copy, new XMLResult(xmlMeta, each));
                }
            } catch (IOException e) {
                handleLoadException(each, e);
            }
        }
        if (nothing) return all;
        for (Map.Entry<String, MetaBean> entry : all.entrySet()) {
            /*
            * alle unveraenderten werden AUCH KOPIERT (nur zwar nur, wegen
            * potentieller CrossReferenzen durch Relationships)
            */
            if (!copies.containsKey(entry.getKey())) {
                if (entry.getValue().hasRelationships()) {
                    copies.put(entry.getKey(), (MetaBean) entry.getValue().copy());
                } else { // no relationship: do not clone()
                    copies.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return copies;
    }

    public MetaBean buildForId(String beanInfoId) throws Exception {
        final Visitor v;
        visitXMLBeanMeta(beanInfoId, v = new Visitor() {
            private MetaBean meta;

            public MetaBean getMetaBean() {
                return meta;
            }

            public void visit(XMLMetaBean xmlMeta, XMLMetaBeanInfos xmlInfos)
                    throws Exception {
                if (meta == null) {
                    meta = createMetaBean(xmlMeta);
                }
                enrichMetaBean(meta, new XMLResult(xmlMeta, xmlInfos));
            }


        });
        if (v.getMetaBean() == null) {
            throw new IllegalArgumentException("MetaBean " + beanInfoId + " not found");
        }
        return v.getMetaBean();
    }

    private MetaBean createMetaBean(XMLMetaBean xmlMeta) throws IntrospectionException {
        final Class clazz = findLocalClass(xmlMeta.getImpl());
        if (clazz != null) {
            return buildMetaBean(Introspector.getBeanInfo(clazz));
        } else { // no local class here
            return new MetaBean();
        }
    }

    protected Class findLocalClass(String className) {
        if (className != null) {
            try {
                return ClassUtils.getClass(className);
            } catch (ClassNotFoundException e) {
                log.warn("class not found: " + className, e);
            }
        }
        return null;
    }

    public MetaBean buildForClass(Class clazz) throws Exception {
        final MetaBean metaBean = buildMetaBean(Introspector.getBeanInfo(clazz));
        visitXMLBeanMeta(metaBean.getId(), new Visitor() {
            public void visit(XMLMetaBean xmlMeta, XMLMetaBeanInfos xmlInfos)
                    throws Exception {
                enrichMetaBean(metaBean, new XMLResult(xmlMeta, xmlInfos));
            }

            public MetaBean getMetaBean() {
                return metaBean;
            }
        });
        return metaBean;
    }

    protected MetaBean buildMetaBean(BeanInfo info) {
        MetaBean meta = new MetaBean();
        if (info.getBeanDescriptor() != null) {
            meta.setId(info.getBeanDescriptor()
                    .getBeanClass().getName()); // id = full class name!
            meta.setName(
                    info.getBeanDescriptor().getName()); // (display?)name = simple class name!
            meta.setBeanClass(info.getBeanDescriptor().getBeanClass());
        }
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (!pd.getName().equals("class")) { // except this one!
                MetaProperty metaProp = buildMetaProperty(pd);
                meta.putProperty(pd.getName(), metaProp);
            }
        }
        return meta;
    }

    protected MetaProperty buildMetaProperty(PropertyDescriptor pd) {
        MetaProperty meta = new MetaProperty();
        meta.setName(pd.getName());
//        meta.setDisplayName(pd.getDisplayName());
        meta.setType(pd.getPropertyType());
        if (pd.isHidden()) meta.putFeature(HIDDEN, Boolean.TRUE);
        if (pd.isPreferred()) meta.putFeature(PREFERRED, Boolean.TRUE);
        if (pd.isConstrained()) meta.putFeature(READONLY, Boolean.TRUE);

        Enumeration<String> enumeration = pd.attributeNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            Object value = pd.getValue(key);
            meta.putFeature(key, value);
        }
        return meta;
    }

    /**
     * find a bean by the bean-id (=bean.name)
     *
     * @return null or the bean found from the first loader that has it.
     */
    protected XMLResult findXMLBeanMeta(String beanId) {
        for (Map.Entry<XMLMetaBeanLoader, XMLMetaBeanInfos> entry : resources
                .entrySet()) {
            if (entry.getValue() == null) {
                // load when not already loaded
                try {
                    entry.setValue(entry.getKey().load());
                } catch (IOException e) {
                    handleLoadException(entry.getKey(), e);
                }
            }
            if (entry.getValue() != null) { // search in loaded infos for the 'name'
                XMLMetaBean found = entry.getValue().getBean(beanId);
                if (found != null) {
                    return new XMLResult(found, entry.getValue());
                }
            }
        }
        return null; // not found!
    }

    protected interface Visitor {
        /**
         * @param xmlMeta  - null or the bean found
         * @param xmlInfos - all infos in a single unit (xml file)
         * @throws Exception
         */
        void visit(XMLMetaBean xmlMeta, XMLMetaBeanInfos xmlInfos) throws Exception;

        MetaBean getMetaBean();
    }

    protected void visitXMLBeanMeta(String beanId, Visitor visitor) throws Exception {
        for (Map.Entry<XMLMetaBeanLoader, XMLMetaBeanInfos> entry : resources
                .entrySet()) {
            if (entry.getValue() == null) {
                // load when not already loaded
                try {
                    entry.setValue(entry.getKey().load());
                } catch (IOException e) {
                    handleLoadException(entry.getKey(), e);
                }
            }
            if (entry.getValue() != null) { // search in loaded infos for the 'name'
                if (beanId == null) {
                    visitor.visit(null, entry.getValue());
                } else {
                    XMLMetaBean found = entry.getValue().getBean(beanId);
                    if (found != null) {
                        visitor.visit(found, entry.getValue());
                    }
                }
            }
        }
    }

    protected void handleLoadException(Object loader, IOException e) {
        log.error("error loading " + loader, e);
    }

    protected void enrichMetaBean(MetaBean meta, XMLResult result) throws Exception {
        if (result.xmlMeta.getId() != null) {
            meta.setId(result.xmlMeta.getId());
        }
        if (result.xmlMeta.getName() != null) {
            meta.setName(result.xmlMeta.getName());
        }
/*        if (meta.getBeanClass() == null && result.xmlMeta.getImpl() != null) {
            meta.setBeanClass(findLocalClass(result.xmlMeta.getImpl()));
        }*/
        result.xmlMeta.mergeFeaturesInto(meta);
        enrichValidations(meta, result.xmlMeta, result, false);
        if (result.xmlMeta.getProperties() != null) {
            for (XMLMetaProperty xmlProp : result.xmlMeta.getProperties()) {
                enrichElement(meta, xmlProp, result);
            }
        }
        if (result.xmlMeta.getBeanRefs() != null) {
            for (XMLMetaBeanReference xmlRef : result.xmlMeta.getBeanRefs()) {
                enrichElement(meta, xmlRef, result);
            }
        }
    }

    protected MetaProperty enrichElement(MetaBean meta, XMLMetaElement xmlProp,
                                         XMLResult result) throws Exception {
        MetaProperty prop = meta.getProperty(xmlProp.getName());
        if (prop == null) {
            prop = new MetaProperty();
            prop.setName(xmlProp.getName());
            meta.putProperty(xmlProp.getName(), prop);
        }
        xmlProp.mergeInto(prop);
        enrichValidations(prop, xmlProp, result, true);
        return prop;
    }

    protected void enrichValidations(FeaturesCapable prop, XMLFeaturesCapable xmlProp,
                                     XMLResult result, boolean addStandard) throws Exception {
        if (xmlProp.getValidators() != null) {
            String[] func = prop.getFeature(JAVASCRIPT_VALIDATION_FUNCTIONS);
            List<String> jsValidators = new ArrayList<String>(
                    xmlProp.getValidators().size() + (func == null ? 0 : func.length));
            if (func != null && func.length > 0) {
                for (String each : func) {
                    jsValidators.add(each);
                }
            }
            boolean useStandard = true;
            for (XMLMetaValidatorReference valRef : xmlProp.getValidators()) {
                if (standardValidation != null &&
                        valRef.getRefId().equals(standardValidation.getValidationId())) {
                    useStandard = false;
                }
                XMLMetaValidator validator =
                        result.xmlInfos.getValidator(valRef.getRefId());
                if (validator != null) {
                    if (validator.getValidation() != null) {
                        prop.addValidation(validator.getValidation());
                    }
                    if (validator.getJsFunction() != null &&
                            !jsValidators.contains(validator.getJsFunction())) {
                        jsValidators.add(validator.getJsFunction());
                    }
                }
            }
            if (!jsValidators.isEmpty()) {
                prop.putFeature(JAVASCRIPT_VALIDATION_FUNCTIONS,
                        jsValidators.toArray(new String[jsValidators.size()]));
            }
            if (useStandard && standardValidation != null) {
                if (!prop.hasValidation(standardValidation))
                    prop.addValidation(standardValidation);
            }
        } else
        if (addStandard && standardValidation != null && !prop.hasValidation(standardValidation)) {
            prop.addValidation(standardValidation);
        }
    }

    protected static class XMLResult {
        XMLMetaBean xmlMeta;
        XMLMetaBeanInfos xmlInfos;

        public XMLResult(XMLMetaBean metaBean, XMLMetaBeanInfos metaInfos) {
            this.xmlMeta = metaBean;
            this.xmlInfos = metaInfos;
        }
    }
}