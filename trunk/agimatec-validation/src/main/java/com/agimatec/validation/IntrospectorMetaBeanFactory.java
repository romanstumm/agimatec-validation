package com.agimatec.validation;

import static com.agimatec.validation.model.Features.Property.*;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;

/**
 * Description: use information from java.beans.Introspector in MetaBeans.
 * The PropertyDescriptor can contain info about HIDDEN, PREFERRED, READONLY
 * and other features<br/>
 * User: roman <br/>
 * Date: 07.10.2009 <br/>
 * Time: 11:43:19 <br/>
 * Copyright: Agimatec GmbH
 */
public final class IntrospectorMetaBeanFactory implements MetaBeanFactory {

    public void buildMetaBean(MetaBean meta) throws Exception {
        if(meta.getBeanClass() == null) return; // handle only, when local class exists

        BeanInfo info = Introspector.getBeanInfo(meta.getBeanClass());
        if (info.getBeanDescriptor() != null) {
            meta.setId(info.getBeanDescriptor()
                  .getBeanClass().getName()); // id = full class name!
            meta.setName(
                  info.getBeanDescriptor().getName()); // (display?)name = simple class name!
        }
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (!pd.getName().equals("class")) { // except this one!
                MetaProperty metaProp = buildMetaProperty(pd);
                meta.putProperty(pd.getName(), metaProp);
            }
        }
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
}
