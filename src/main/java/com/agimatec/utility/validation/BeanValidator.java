package com.agimatec.utility.validation;

import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;
import com.agimatec.utility.validation.model.Features;

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
     * according to the metaBean.
     *
     * @param bean - a single bean or a collection of beans (that share the same metaBean!)
     */
    public ValidationResults validate(Object bean, MetaBean metaBean) {
        ValidationResults result = new ValidationResults();
        validate(new ValidationContext().setBean(bean, metaBean), result);
        return result;
    }

    /**
     * API - validate a complex 'bean' with related beans according to
     * validation rules in 'metaBean'
     *
     * @param context - the context is initialized with:
     *                <br>&nbsp;&nbsp;bean - the root object start validation at
     *                or a collection of root objects
     *                <br>&nbsp;&nbsp;metaBean - the meta information for the root object(s)
     * @return a new instance of validation results
     */
    public void validate(ValidationContext context, ValidationListener listener) {
        if (context.getBean() != null) {
            if (context.getBean() instanceof Collection) { // to Many
                for (Object each : ((Collection) context.getBean())) {
                    context.setBean(each);
                    validateBeanNet(context, listener);
                }
            } else { // to One
                validateBeanNet(context, listener);
            }
        }
    }

    /** internal  validate a bean (=not a collection of beans) and its related beans */
    protected void validateBeanNet(ValidationContext context, ValidationListener listener) {
        if (context.collectValidated(context.getBean())) {
            validateBean(context, listener);
            final Object bean = context.getBean();
            final MetaBean mbean = context.getMetaBean();
            for (MetaProperty prop : context.getMetaBean().getProperties()) {
                if (prop.getMetaBean() != null &&
                        prop.getFeature(Features.Property.REF_CASCADE, true)) {
                    // modify context state for relationship-target bean
                    context.moveDown(prop);
                    validate(context, listener);
                    context.moveUp(bean, mbean); // reset context state
                }
            }
        }
    }

    /** API - validate a single bean only. no related beans will be validated */
    public void validateBean(ValidationContext context, ValidationListener listener) {
        /**
         * field-level validations
         */
        for (MetaProperty prop : context.getMetaBean().getProperties()) {
            if (prop.getAccess() == MetaProperty.ACCESS.FIELD) {
                context.setMetaProperty(prop);
                validateProperty(context, listener);
            }
        }

        /**
         * property-level validation
         */
        for (MetaProperty prop : context.getMetaBean().getProperties()) {
            if (prop.getAccess() == MetaProperty.ACCESS.METHOD) {
                context.setMetaProperty(prop);
                validateProperty(context, listener);
            }
        }
        /**
         * bean-level validation
         */
        context.setMetaProperty(null);
        for (Validation validation : context.getMetaBean().getValidations()) {
            validation.validate(context, listener);
        }
    }

    /**
     * API - validate a single property only. performs all validations
     * for this property.
     */
    public void validateProperty(ValidationContext context, ValidationListener listener) {
        for (Validation validation : context.getMetaProperty().getValidations()) {
            validation.validate(context, listener);
        }
    }

}
