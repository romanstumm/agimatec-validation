package com.agimatec.validation.jsr303.extensions;

import com.agimatec.validation.model.MetaBean;

import java.util.List;

/**
 * Description: <br/>
 * User: roman <br/>
 * Date: 01.02.2010 <br/>
 * Time: 14:43:28 <br/>
 * Copyright: Agimatec GmbH
 */
public interface ProcedureDescriptor {
    MetaBean getMetaBean();

    void setCascaded(boolean b);

    List<ParameterDescriptor> getParameterDescriptors();
}
