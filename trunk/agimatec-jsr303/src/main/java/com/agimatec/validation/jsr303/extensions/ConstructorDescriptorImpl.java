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
 * Time: 10:57:39 <br/>
 * Copyright: Agimatec GmbH
 */
public class ConstructorDescriptorImpl extends ElementDescriptorImpl
      implements ConstructorDescriptor, ProcedureDescriptor {
    private List<ParameterDescriptor> parameterDescriptors = new ArrayList();
    private boolean cascaded;

    protected ConstructorDescriptorImpl(MetaBean metaBean, Validation[] validations) {
        super(metaBean, validations);
    }

    protected ConstructorDescriptorImpl(Class elementClass, Validation[] validations) {
        super(elementClass, validations);
    }

    public boolean isCascaded() {
        return cascaded;
    }

    public void setCascaded(boolean cascaded) {
        this.cascaded = cascaded;
    }


    public List<ParameterDescriptor> getParameterDescriptors() //index aligned
    {
        return parameterDescriptors;
    }
}
