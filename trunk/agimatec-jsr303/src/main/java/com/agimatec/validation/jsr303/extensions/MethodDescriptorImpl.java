package com.agimatec.validation.jsr303.extensions;

import com.agimatec.validation.jsr303.ElementDescriptorImpl;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 10:38:05 <br/>
 * Copyright: Agimatec GmbH
 */
public class MethodDescriptorImpl extends ElementDescriptorImpl
      implements MethodDescriptor, ProcedureDescriptor {
    private List<ParameterDescriptor> parameterDescriptors = new ArrayList();
    private boolean cascaded;

    protected MethodDescriptorImpl(MetaBean metaBean, Validation[] validations) {
        super(metaBean, validations);
    }

    protected MethodDescriptorImpl(Class elementClass, Validation[] validations) {
        super(elementClass, validations);
    }

    public List<ParameterDescriptor> getParameterDescriptors() //index aligned
    {
        return parameterDescriptors;
    }

    public boolean isCascaded() {
        return cascaded;
    }

    public void setCascaded(boolean cascaded) {
        this.cascaded = cascaded;
    }

}
