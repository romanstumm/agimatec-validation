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
     * TODO RSt - implement spec ValidationException
     * A given class must not be described more than once amongst all the XML mapping descriptors.
     * A given field or getter must not be described more than once on a given class description.
     * A given constraint definition must not be overridden more than once amongst
     * all the XML mapping descriptors.
     * ==> If any of these rule is violated in a given validation deployment,
     * a ValidationException is raised during the creation of the ValidatorFactory.
     *
     * If the name of the class does refer to a class not present in in the classpath,
     * a ValidationException is raised.
     */

    /**
     * If default-package is set, all unqualified class names (including annotations)
     * are considered part of the package described by default-package.
     */

    /**
     * By default, all constraint declarations expressed via annotation are
     * ignored for classes described in XML.
     *
     * You can force Bean Validation to consider both annotations and XML constraint
     * declarations by using ignore-annotation="false" on bean.
     */

    /**
     * add the information from validation.xml to the metaBean.
     *
     * @throws Exception
     */
    public void buildMetaBean(MetaBean metaBean) throws Exception {
        if (config.isIgnoreXmlConfiguration()) return;

        // TODO RSt - add information from validation.xml to the metaBean

    }
}
