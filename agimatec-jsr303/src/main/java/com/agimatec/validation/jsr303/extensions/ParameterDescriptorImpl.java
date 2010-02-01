package com.agimatec.validation.jsr303.extensions;

import com.agimatec.validation.jsr303.ElementDescriptorImpl;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.Validation;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 10:49:29 <br/>
 * Copyright: Agimatec GmbH
 */
public class ParameterDescriptorImpl extends ElementDescriptorImpl
      implements ParameterDescriptor {
    private boolean cascaded;
    private int index;

    public ParameterDescriptorImpl(MetaBean metaBean, Validation[] validations) {
        super(metaBean, validations);
    }

    public ParameterDescriptorImpl(Class elementClass, Validation[] validations) {
        super(elementClass, validations);
    }

    public boolean isCascaded() {
        return cascaded;
    }

    public void setCascaded(boolean cascaded) {
        this.cascaded = cascaded;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
