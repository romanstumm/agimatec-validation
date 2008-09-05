package com.agimatec.utility.validation;

import com.agimatec.utility.validation.integration.Validate;
import com.agimatec.utility.validation.model.DynamicMetaBean;
import com.agimatec.utility.validation.model.Features;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

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
        MetaBean metaBean = MetaBeanManagerFactory.getFinder().findForClass(bean.getClass());
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
            context.setBean(parameter, MetaBeanManagerFactory.getFinder().findForClass(beanClass));
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
            DynamicMetaBean dynamic = context.getMetaBean() instanceof DynamicMetaBean ?
                    (DynamicMetaBean) context.getMetaBean() : null;
            if (context.getBean() instanceof Collection) { // to Many
                int index = 0;
                for (Object each : ((Collection) context.getBean())) {
                    if (dynamic != null) {
                        context.setBean(each, dynamic.resolveMetaBean(each));
                    } else {
                        context.setBean(each);
                    }
                    context.setCurrentIndex(index++);
                    validateBeanNet(context);
                }
            } else if (context.getBean() instanceof Object[]) {
                int index = 0;
                for (Object each : ((Object[]) context.getBean())) {
                    if (dynamic != null) {
                        context.setBean(each, dynamic.resolveMetaBean(each));
                    } else {
                        context.setBean(each);
                    }
                    context.setCurrentIndex(index++);
                    validateBeanNet(context);
                }
            } else { // to One
                if (dynamic != null) {
                    context.setMetaBean(dynamic.resolveMetaBean(context.getBean()));
                }
                validateBeanNet(context);
            }
        }
    }

    /** internal  validate a bean (=not a collection of beans) and its related beans */
    protected void validateBeanNet(ValidationContext context) {
        if (context.collectValidated(context.getBean())) {
            validateBean(context);
            final Object bean = context.getBean();
            final MetaBean mbean = context.getMetaBean();
            for (MetaProperty prop : context.getMetaBean().getProperties()) {
                if (prop.getMetaBean() != null ||
                        prop.getFeature(Features.Property.REF_CASCADE, false)) {
                    // modify context state for relationship-target bean
                    context.moveDown(prop);
                    validateContext(context);
                    context.moveUp(bean, mbean); // reset context state
                }
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
