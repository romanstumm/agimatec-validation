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
package com.agimatec.validation;

import com.agimatec.validation.model.*;
import com.agimatec.validation.util.AccessStrategy;
import com.agimatec.validation.util.PropertyAccess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: Top-Level API-class to validate objects or object-trees.
 * You can invoke, extend or utilize this class if you need other ways to integrate
 * validation in your application.
 * <p/>
 * This class supports cyclic object graphs by keeping track of
 * validated instances in the validation context.<br/>
 * User: roman.stumm <br/>
 * Date: 06.07.2007 <br/>
 * Time: 12:28:46 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class BeanValidator {
    /**
     * convenience API. validate a root object with all related objects
     * with its default metaBean definition.
     *
     * @return results - validation results found
     */
    public ValidationResults validate(Object bean) {
        MetaBean metaBean =
              MetaBeanManagerFactory.getFinder().findForClass(bean.getClass());
        return validate(bean, metaBean);
    }

    /**
     * convenience API. validate a root object with all related objects
     * according to the metaBean.
     *
     * @param bean - a single bean or a collection of beans (that share the same metaBean!)
     * @return results - validation results found
     */
    public ValidationResults validate(Object bean, MetaBean metaBean) {
        ValidationContext context = createContext();
        context.setBean(bean, metaBean);
        validateContext(context);
        return (ValidationResults) context.getListener();
    }

    /**
     * validate the method parameters based on @Validate annotations.
     * Requirements:
     * 1. Method must be annotated with @Valiadate
     * (otherwise this method returns and no current validation context is created)
     * 2. Parameter, that are to be validated must also be annotated with @Validate
     *
     * @param method     -  a method
     * @param parameters - the parameters suitable to the method
     * @return a validation result
     * @see Validate
     */
    public ValidationResults validateCall(Method method, Object[] parameters) {
        if (parameters.length > 0) {
            // shortcut (for performance!)
            if (method.getAnnotation(Validate.class) == null) return null;
            ValidationContext context = createContext();
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < parameters.length; i++) {
                for (Annotation anno : annotations[i]) {
                    if (anno instanceof Validate) {
                        if (determineMetaBean((Validate) anno, parameters[i], context)) {
                            validateContext(context);
                        }
                    }
                }
            }
            return (ValidationResults) context.getListener();
        }
        return null;
    }

    /** @return true when validation should happen, false to skip it */
    protected boolean determineMetaBean(Validate validate, Object parameter,
                                        ValidationContext context) {
        if (validate.value().length() == 0) {
            if (parameter == null) return false;
            Class beanClass;
            if (parameter instanceof Collection) {   // do not validate empty collection
                Collection coll = ((Collection) parameter);
                if (coll.isEmpty()) return false;
                beanClass = coll.iterator().next().getClass(); // get first object
            } else if (parameter.getClass().isArray()) {
                beanClass = parameter.getClass().getComponentType();
            } else {
                beanClass = parameter.getClass();
            }
            context.setBean(parameter,
                  MetaBeanManagerFactory.getFinder().findForClass(beanClass));
        } else {
            context.setBean(parameter,
                  MetaBeanManagerFactory.getFinder().findForId(validate.value()));
        }
        return true;
    }

    /**
     * factory method -
     * overwrite in subclasses
     */
    protected ValidationResults createResults() {
        return new ValidationResults();
    }

    /**
     * factory method -
     * overwrite in subclasses
     */
    protected ValidationContext createContext() {
        return new BeanValidationContext(createResults());
    }

    /**
     * convenience API. validate a single property.
     *
     * @param bean         - the root object
     * @param metaProperty - metadata for the property
     * @return validation results
     */
    public ValidationResults validateProperty(Object bean, MetaProperty metaProperty) {
        ValidationContext context = createContext();
        context.setBean(bean);
        context.setMetaProperty(metaProperty);
        validateProperty(context);
        return (ValidationResults) context.getListener();
    }

    /**
     * validate a single property only. performs all validations
     * for this property.
     */
    public void validateProperty(ValidationContext context) {
        for (Validation validation : context.getMetaProperty().getValidations()) {
            validation.validate(context);
        }
    }

    /**
     * validate a complex 'bean' with related beans according to
     * validation rules in 'metaBean'
     *
     * @param context - the context is initialized with:
     *                <br>&nbsp;&nbsp;bean - the root object start validation at
     *                or a collection of root objects
     *                <br>&nbsp;&nbsp;metaBean - the meta information for the root object(s)
     * @return a new instance of validation results
     */
    public void validateContext(ValidationContext context) {
        if (context.getBean() != null) {
            if (context.getBean() instanceof Map) {
                validateMapInContext(context);
            } else if (context.getBean() instanceof Iterable) {
                validateIteratableInContext(context);
            } else if (context.getBean() instanceof Object[]) {
                validateArrayInContext(context);
            } else { // to One
                validateBeanInContext(context);
            }
        }
    }

    private void validateBeanInContext(ValidationContext context) {
        if (getDynamicMetaBean(context) != null) {
            context.setMetaBean(
                  getDynamicMetaBean(context).resolveMetaBean(context.getBean()));
        }
        validateBeanNet(context);
    }

    private void validateArrayInContext(ValidationContext context) {
        int index = 0;
        DynamicMetaBean dyn = getDynamicMetaBean(context);
        for (Object each : ((Object[]) context.getBean())) {
            context.setCurrentIndex(index++);
            if (each == null) continue; // or throw IllegalArgumentException? (=> spec)
            if (dyn != null) {
                context.setBean(each, dyn.resolveMetaBean(each));
            } else {
                context.setBean(each);
            }
            validateBeanNet(context);
        }
    }

    private DynamicMetaBean getDynamicMetaBean(ValidationContext context) {
        return context.getMetaBean() instanceof DynamicMetaBean ?
              (DynamicMetaBean) context.getMetaBean() : null;
    }

    /** Any object implementing java.lang.Iterable is supported */
    private void validateIteratableInContext(ValidationContext context) {
        Iterator it = ((Iterable) context.getBean()).iterator();
        int index = 0;
        // jsr303 spec: Each object provided by the iterator is validated.
        final DynamicMetaBean dyn = getDynamicMetaBean(context);
        while (it.hasNext()) { // to Many
            Object each = it.next();
            context.setCurrentIndex(index++);
            if (each == null)
                continue; // enhancement: throw IllegalArgumentException? (=> spec)
            if (dyn != null) {
                context.setBean(each, dyn.resolveMetaBean(each));
            } else {
                context.setBean(each);
            }
            validateBeanNet(context);
        }
    }

    private void validateMapInContext(ValidationContext context) {
        // jsr303 spec: For Map, the value of each Map.Entry is validated (key is not validated).
        Iterator<Map.Entry> it = ((Map) context.getBean()).entrySet().iterator();
        final DynamicMetaBean dyn = getDynamicMetaBean(context);
        while (it.hasNext()) { // to Many
            Map.Entry entry = it.next();
            context.setCurrentKey(entry.getKey());
            if (entry.getValue() == null)
                continue; // enhancement: throw IllegalArgumentException? (=> spec)
            if (dyn != null) {
                context.setBean(entry.getValue(), dyn.resolveMetaBean(entry.getValue()));
            } else {
                context.setBean(entry.getValue());
            }
            validateBeanNet(context);
        }
    }

    /** internal validate a bean (=not a collection of beans) and its related beans */
    protected void validateBeanNet(ValidationContext context) {
        if (context.collectValidated()) {
            validateBean(context);
            for (MetaProperty prop : context.getMetaBean().getProperties()) {
                validateRelatedBean(context, prop);
            }
        }
    }

    private void validateRelatedBean(ValidationContext context, MetaProperty prop) {
        AccessStrategy[] access = prop.getFeature(Features.Property.REF_CASCADE);
        if (access == null && prop.getMetaBean() != null) { // single property access strategy
            // save old values from context
            final Object bean = context.getBean();
            final MetaBean mbean = context.getMetaBean();
            // modify context state for relationship-target bean
            context.moveDown(prop, new PropertyAccess(bean.getClass(), prop.getName()));
            validateContext(context);
            // restore old values in context
            context.moveUp(bean, mbean);
        } else if (access != null) { // different accesses to relation
            // save old values from context
            final Object bean = context.getBean();
            final MetaBean mbean = context.getMetaBean();
            for (AccessStrategy each : access) {
                // modify context state for relationship-target bean
                context.moveDown(prop, each);
                validateContext(context);
                // restore old values in context
                context.moveUp(bean, mbean);
            }
        }
    }

    /** validate a single bean only. no related beans will be validated */
    public void validateBean(ValidationContext context) {
        /**
         * execute all property level validations
         */
        for (MetaProperty prop : context.getMetaBean().getProperties()) {
            context.setMetaProperty(prop);
            validateProperty(context);
        }
        /**
         * execute all bean level validations
         */
        context.setMetaProperty(null);
        for (Validation validation : context.getMetaBean().getValidations()) {
            validation.validate(context);
        }
    }
}
