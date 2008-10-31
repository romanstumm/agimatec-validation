package com.agimatec.utility.validation.xml;

import static com.agimatec.utility.validation.model.Features.Property.REF_BEAN_ID;
import com.agimatec.utility.validation.model.MetaProperty;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Description: <br/>
 * User: roman.stumm <br/>
 * Date: 05.07.2007 <br/>
 * Time: 14:48:01 <br/>
 * Copyright: Agimatec GmbH 2008
 */
@XStreamAlias("relationship")
public class XMLMetaBeanReference extends XMLMetaElement {
    @XStreamAsAttribute
    private String beanId;

    public XMLMetaBeanReference(String refId) {
        this.beanId = refId;
    }

    public XMLMetaBeanReference() {
    }

    /**
     * id of referenced target bean of the relationship
     * @return
     */
    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    @Override
    public void mergeInto(MetaProperty prop) throws ClassNotFoundException {
        super.mergeInto(prop);   // call super!
        if(getBeanId() != null) {
            prop.putFeature(REF_BEAN_ID, getBeanId());
        }
    }
}
