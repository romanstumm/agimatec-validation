package com.agimatec.validation;

import com.agimatec.validation.model.MetaBean;

/**
 * Description: interface for abstraction how to initialize a MetaBean
 * with information from somewhere<br/>
 * User: roman <br/>
 * Date: 07.10.2009 <br/>
 * Time: 11:38:03 <br/>
 * Copyright: Agimatec GmbH
 */
public interface MetaBeanFactory {
     void buildMetaBean(MetaBean metaBean) throws Exception;
}
