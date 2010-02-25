/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agimatec.validation.jsr303.xml;

import com.agimatec.validation.MetaBeanFactory;
import com.agimatec.validation.model.MetaBean;

import javax.validation.spi.ConfigurationState;

/**
 * Description: TODO RSt - not yet implemented: jsr303-xml support<br/>
 * User: roman <br/>
 * Date: 07.10.2009 <br/>
 * Time: 14:17:56 <br/>
 * Copyright: Agimatec GmbH
 */
public class ValidationMappingMetaBeanFactory implements MetaBeanFactory {
    private ConfigurationState config;

    public ValidationMappingMetaBeanFactory(ConfigurationState configurationState) {
        this.config = configurationState;
    }

    /**
     * add the information from validation.xml to the metaBean.
     * @param metaBean
     * @throws Exception
     */
    public void buildMetaBean(MetaBean metaBean) throws Exception {
        if(config.isIgnoreXmlConfiguration()) return;
        
        // TODO RSt - add information from validation.xml to the metaBean

    }
}