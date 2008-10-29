package com.agimatec.validation.jsr303.impl;

import com.agimatec.validation.jsr303.AgimatecValidatorFactory;
import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;

import javax.validation.ElementDescriptor;
import javax.validation.InvalidConstraint;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * API class -
 * Description:
 * instance is able to validate instances of T classes (and the associated objects if any).
 * concurrent, multithreaded access implementation is safe.
 * It is recommended to cache BeanValidator<T> instances.
 * <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 13:36:33 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ClassValidator<T> implements Validator<T> {
    protected final MetaBean metaBean;
    protected final AgimatecValidatorFactory factory;
    private ElementDescriptorImpl elementDescriptor;

    /**
     * create an instance with the default provider for a bean class
     *
     * @deprecated use official bootstrap API
     */
    public ClassValidator(Class<T> aClass) {
        factory = AgimatecValidatorFactory.getDefault();
        metaBean = factory.getMetaBeanManager().findForClass(aClass);
    }

    /**
     * create an instance with the default provider for a bean id
     *
     * @deprecated use official bootstrap API
     */
    public ClassValidator(String metaBeanId) {
        factory = AgimatecValidatorFactory.getDefault();
        metaBean = factory.getMetaBeanManager().findForId(metaBeanId);
    }

    public ClassValidator(AgimatecValidatorFactory factory, MetaBean metaBean) {
        this.factory = factory;
        this.metaBean = metaBean;
    }

    /** validate all constraints on object */
    public Set<InvalidConstraint<T>> validate(T object, String... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        GroupValidationContext context = createContext(object, groups);
        ConstraintValidationListener result = (ConstraintValidationListener) context.getListener();
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            factory.getBeanValidator().validateContext(context);
            /**
             * if one of the group process in the sequence leads to one or more validation failure,
             * the groups following in the sequence must not be processed
             */
            if (!result.isEmpty()) break;
        }
        return result.getInvalidConstraints();
    }

    /**
     * validate all constraints on <code>propertyName</code> property of object
     *
     * @param propertyName - the attribute name
     *                     TODO RSt - are nested property with dot-notation supported, private fields etc.?
     */
    public Set<InvalidConstraint<T>> validateProperty(T object, String propertyName,
                                                      String... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        GroupValidationContext context = createContext(object, groups);
        ConstraintValidationListener result = (ConstraintValidationListener) context.getListener();
        context.setMetaProperty(metaBean.getProperty(propertyName));
        if (context.getMetaProperty() == null) throw new IllegalArgumentException(
                "Unknown property " + object.getClass().getName() + "." + propertyName);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            factory.getBeanValidator().validateProperty(context);
            /**
             * if one of the group process in the sequence leads to one or more validation failure,
             * the groups following in the sequence must not be processed
             */
            if (!result.isEmpty()) break;
        }
        return result.getInvalidConstraints();
    }

    /**
     * validate all constraints on <code>propertyName</code> property
     * if the property value is <code>value</code>
     */
    public Set<InvalidConstraint<T>> validateValue(String propertyName, Object value,
                                                   String... groups) {
        GroupValidationContext context = createContext(null, groups);
        ConstraintValidationListener result = (ConstraintValidationListener) context.getListener();
        context.setMetaProperty(metaBean.getProperty(propertyName));
        context.setFixedValue(value);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            factory.getBeanValidator().validateProperty(context);
            /**
             * if one of the group process in the sequence leads to one or more validation failure,
             * the groups following in the sequence must not be processed
             */
            if (!result.isEmpty()) break;
        }
        return result.getInvalidConstraints();
    }

    protected GroupValidationContext createContext(T object,
                                                   String[] groups) {
        ConstraintValidationListener<T> listener = new ConstraintValidationListener<T>(object);
        GroupBeanValidationContext context = new GroupBeanValidationContext(listener,
                factory.getMessageResolver());
        if (groups == null || groups.length == 0)
            groups = GroupBeanValidationContext.DEFAULT_GROUPS;
        context.setRequestedGroups(groups);
        context.setBean(object, metaBean);
        return context;
    }

    /**
     * @return true if at least one constraint declaration is present for the given bean
     *         or if one property is marked for validation cascade
     */
    public boolean hasConstraints() {
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
            if (mprop.getMetaBean() != null &&
                    mprop.getFeature(Features.Property.REF_CASCADE, true)) return true;
        }
        return false;
    }

    public ElementDescriptor getConstraintsForBean() {
        if (elementDescriptor == null) {
            ElementDescriptorImpl edesc = new ElementDescriptorImpl();
//            edesc.setElementType(ElementType.TYPE);
            edesc.setReturnType(metaBean.getBeanClass());
            edesc.setCascaded(false);
            /**
             * if the constraint is a class level constraint, then the empty string is used
             */
            edesc.setPropertyPath("");
            createConstraintDescriptors(edesc, metaBean.getValidations());
            elementDescriptor = edesc;
        }
        return elementDescriptor;
    }

    private void createConstraintDescriptors(ElementDescriptorImpl edesc,
                                             com.agimatec.validation.Validation[] validations) {
        edesc.setConstraintDescriptors(new ArrayList(validations.length));
        for (com.agimatec.validation.Validation validation : validations) {
            if (validation instanceof ConstraintValidation) {
                ConstraintValidation cval = (ConstraintValidation) validation;
                edesc.getConstraintDescriptors().add(cval.getConstraintDescriptor());
            }
        }
    }

    public ElementDescriptor getConstraintsForProperty(String propertyName) {
        MetaProperty prop = metaBean.getProperty(propertyName);
        if (prop == null) return null;
        ElementDescriptorImpl edesc = prop.getFeature(Jsr303Features.Property.ElementDescriptor);
        if (edesc == null) {
            edesc = new ElementDescriptorImpl();
            edesc.setReturnType(prop.getFeature(Features.Property.REF_BEAN_TYPE, prop.getType()));
            edesc.setCascaded(prop.getFeature(Features.Property.REF_CASCADE, false));
            edesc.setPropertyPath(propertyName);
            createConstraintDescriptors(edesc, prop.getValidations());
            // hack try to find if elementType is FIELD
//            edesc.setElementType(ElementType.FIELD);
            /*for (ConstraintDescriptor each : edesc.getConstraintDescriptors()) {
                if (!((ConstraintDescriptorImpl) each).isFieldAccess()) {
                    edesc.setElementType(ElementType.METHOD);
                    break;
                }
            }*/
            prop.putFeature(Jsr303Features.Property.ElementDescriptor, edesc);
        }
        return edesc;
    }

    /** @return return the property names having at least a constraint defined */
    public String[] getValidatedProperties() {
        Set<String> validatedProperties = new HashSet();
        for (MetaProperty prop : metaBean.getProperties()) {
            if (prop.getValidations().length > 0 || (prop.getMetaBean() != null &&
                    prop.getFeature(Features.Property.REF_CASCADE, true))) {
                validatedProperties.add(prop.getName());
            }
        }
        return validatedProperties.toArray(new String[validatedProperties.size()]);
    }
}
