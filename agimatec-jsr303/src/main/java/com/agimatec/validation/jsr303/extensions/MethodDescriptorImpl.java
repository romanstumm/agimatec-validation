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
