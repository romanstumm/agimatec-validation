package com.agimatec.utility.validation.xml;

import com.agimatec.utility.validation.Validation;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.lang.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:58:11 <br/>
 *
 */
@XStreamAlias("beanInfos")
public class XMLMetaBeanInfos {
    @XStreamAsAttribute
    private String id;
    @XStreamImplicit
    private List<XMLMetaValidator> validators;
    @XStreamImplicit
    private List<XMLMetaBean> beans;
    @XStreamOmitField
    private Map<String, XMLMetaBean> beanLookup;
    @XStreamOmitField
    private Map<String, XMLMetaValidator> validationLookup;

    /**
     * dient zur Kennung, kann aber leer sein, wenn es nicht aus einer Datenbank stammt.
     * Koennte z.B. auch den Dateinamen enthalten - frei verwendbar.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<XMLMetaValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<XMLMetaValidator> validators) {
        this.validators = validators;
    }

    public List<XMLMetaBean> getBeans() {
        return beans;
    }

    public void setBeans(List<XMLMetaBean> beans) {
        this.beans = beans;
    }

    public XMLMetaBean getBean(String id) {
        if (beans == null) return null;
        if (beanLookup == null) initBeanLookup();
        return beanLookup.get(id);
    }

    private void initBeanLookup() {
        beanLookup = new FastHashMap();
        for (XMLMetaBean bean : beans) {
            beanLookup.put(bean.getId(), bean);
        }
        ((FastHashMap) beanLookup).setFast(true);
    }

    private void initValidationLookup() throws Exception {
        validationLookup = new FastHashMap();
        for (XMLMetaValidator xv : validators) {
            if (xv.getJava() != null) {
                Validation validation =
                        (Validation) ClassUtils.getClass(xv.getJava()).newInstance();
                xv.setValidation(validation);
                validationLookup.put(xv.getId(), xv);
            }
        }
        ((FastHashMap) validationLookup).setFast(true);
    }

    public void addBean(XMLMetaBean bean) {
        if (beans == null) beans = new ArrayList();
        beans.add(bean);
    }

    public XMLMetaValidator getValidator(String id) throws Exception {
        if (validators == null) return null;
        if (validationLookup == null) initValidationLookup();
        return validationLookup.get(id);
    }

    public void addValidator(XMLMetaValidator validator) {
        if (validators == null) validators = new ArrayList();
        validators.add(validator);
    }
}
