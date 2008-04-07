package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.BeanValidator;
import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.ValidationListener;
import com.agimatec.utility.validation.model.Features;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;

import javax.validation.*;
import java.lang.annotation.ElementType;
import java.util.*;

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
public class ClassValidator<T> extends BeanValidator implements Validator<T> {
    protected final MetaBean metaBean;
    protected final Provider provider;
    private ElementDescriptorImpl elementDescriptor;
    private MessageResolver messageResolver;

    /**
     * create an instance with the default provider for a bean class
     *
     * @see Provider
     */
    public ClassValidator(Class<T> aClass) {
        provider = Provider.getInstance();
        messageResolver = provider.getDefaultMessageResolver();
        metaBean = provider.getMetaBeanManager().findForClass(aClass);
    }

    /**
     * create an instance with the default provider for a bean id
     *
     * @see Provider
     */
    public ClassValidator(String metaBeanId) {
        provider = Provider.getInstance();
        metaBean = provider.getMetaBeanManager().findForId(metaBeanId);
        this.messageResolver = provider.getDefaultMessageResolver();
    }

    protected ClassValidator(Provider provider, MetaBean metaBean) {
        this.provider = provider;
        this.metaBean = metaBean;
        this.messageResolver = this.provider.getDefaultMessageResolver();
    }

    /** validate all constraints on object */
    public Set<InvalidConstraint<T>> validate(T object, String... groups) {
        ConstraintValidationListener<T> result = new ConstraintValidationListener<T>(object);
        GroupValidationContext context = createContext(object, groups);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            validate(context, result);
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
        GroupValidationContext context = createContext(object, groups);
        context.setMetaProperty(metaBean.getProperty(propertyName));
        if (context.getMetaProperty() == null) throw new IllegalArgumentException(
                "Unknown property " + object.getClass().getName() + "." + propertyName);
        ConstraintValidationListener<T> result = new ConstraintValidationListener<T>(object);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            validateProperty(context, result);
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
        context.setMetaProperty(metaBean.getProperty(propertyName));
        context.setFixedValue(value);
        ConstraintValidationListener<T> result = new ConstraintValidationListener<T>(null);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            validateProperty(context, result);            
            /**
             * if one of the group process in the sequence leads to one or more validation failure,
             * the groups following in the sequence must not be processed
             */
            if (!result.isEmpty()) break;
        }
        return result.getInvalidConstraints();
    }

    protected GroupValidationContext createContext(T object, String[] groups) {
        GroupValidationContext context = new GroupValidationContext(getMessageResolver());
        if (groups == null || groups.length == 0) groups = GroupValidationContext.DEFAULT_GROUPS;
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

    public ElementDescriptor getBeanConstraints() {
        if (elementDescriptor == null) {
            ElementDescriptorImpl edesc = new ElementDescriptorImpl();
            edesc.setElementType(ElementType.TYPE);
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
                                             Validation[] validations) {
        edesc.setConstraintDescriptors(new HashSet(validations.length));
        for (Validation validation : validations) {
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
            edesc.setElementType(ElementType.FIELD);
            for (ConstraintDescriptor each : edesc.getConstraintDescriptors()) {
                if (!((ConstraintDescriptorImpl) each).isFieldAccess()) {
                    edesc.setElementType(ElementType.METHOD);
                    break;
                }
            }
            prop.putFeature(Jsr303Features.Property.ElementDescriptor, edesc);
        }
        return edesc;
    }

    /** @return the names of the bean properties having at least one constraint or being cascaded */
    public Set<String> getValidatedProperties() {
        Set<String> validatedProperties = new HashSet();
        for (MetaProperty prop : metaBean.getProperties()) {
            if (prop.getValidations().length > 0 || (prop.getMetaBean() != null &&
                    prop.getFeature(Features.Property.REF_CASCADE, true))) {
                validatedProperties.add(prop.getName());
            }
        }
        return validatedProperties;
    }


    public MessageResolver getMessageResolver() {
        return messageResolver;
    }

    public void setMessageResolver(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    /**
     * validate a single property only. performs all validations
     * for this property.
     */
    @Override
    public void validateProperty(ValidationContext context, ValidationListener listener) {
        /**
         * execute all field level validations than all method level validations
         */
        for (Validation validation : context.getMetaProperty().getValidations()) {
            if (validation.isFieldAccess()) {
                validation.validate(context, listener);
            }
        }
        for (Validation validation : context.getMetaProperty().getValidations()) {
            if (!validation.isFieldAccess()) {
                validation.validate(context, listener);
            }
        }
    }

    /** validate a single bean only. no related beans will be validated */
    @Override
    public void validateBean(ValidationContext context, ValidationListener listener) {
        /**
         * execute all field level validations than all method level validations
         */
        for (ValidationEntry entry : sortValidations(context.getMetaBean())) {
            if (!entry.metaProperty.equals(context.getMetaProperty())) {
                context.setMetaProperty(entry.metaProperty);
            }
            entry.validation.validate(context, listener);
        }
        /**
         * execute all bean level validations
         */
        context.setMetaProperty(null);
        for (Validation validation : context.getMetaBean().getValidations()) {
            validation.validate(context, listener);
        }
    }

    private ValidationEntry[] sortValidations(MetaBean metaBean) {
        // sorted list (field-validations, method-validations)
        ValidationEntry[] propertyValidations =
                metaBean.getFeature(Jsr303Features.Bean.ValidationSequence);
        if (propertyValidations != null) return propertyValidations;
        List<ValidationEntry> entries = new ArrayList();
        for (MetaProperty prop : metaBean.getProperties()) {
            for (Validation validation : prop.getValidations()) {
                entries.add(new ValidationEntry(prop, validation));
            }
        }
        Collections.sort(entries);
        propertyValidations = entries.toArray(new ValidationEntry[entries.size()]);
        metaBean.putFeature(Jsr303Features.Bean.ValidationSequence, propertyValidations);
        return propertyValidations;
    }

}
