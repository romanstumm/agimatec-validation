package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.Validation;
import com.agimatec.utility.validation.model.Features;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;

import javax.validation.ElementDescriptor;
import javax.validation.InvalidConstraint;
import javax.validation.MessageResolver;
import javax.validation.Validator;
import java.lang.annotation.ElementType;
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
 * 
 */
public class ClassValidator<T> implements Validator<T> {
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
    }

    protected ClassValidator(Provider provider, MetaBean metaBean) {
        this.provider = provider;
        this.metaBean = metaBean;
    }

    /** validate all constraints on object */
    public Set<InvalidConstraint<T>> validate(T object, String... groups) {
        ConstraintValidationListener<T> result = new ConstraintValidationListener<T>(object);
        GroupValidationContext context = createContext(object, groups);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            provider.getBeanValidator().validate(context, result);
            /**
             * if one of the group process in the sequence leads to one or more validation failure,
             * the groups following in the sequence must not be processed
             */
            if (!result.isEmpty()) break;
        }
        return result.getInvalidConstraints();
    }

    /** validate all constraints on <code>propertyName</code> property of object */
    public Set<InvalidConstraint<T>> validateProperty(T object, String propertyName,
                                                      String... groups) {
        GroupValidationContext context = createContext(object, groups);
        context.setMetaProperty(metaBean.getProperty(propertyName));
        ConstraintValidationListener<T> result = new ConstraintValidationListener<T>(object);
        List<String> sequence = context.getSequencedGroups();
        for (String currentGroup : sequence) {
            context.resetValidated();
            context.setCurrentGroup(currentGroup);
            provider.getBeanValidator().validateProperty(context, result);
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
     * validate all constraints on <code>propertyName</code> property
     * if the property value is <code>value</code>
     *
     * @throws UnsupportedOperationException - object missing in API?
     */
    public Set<InvalidConstraint<T>> validateValue(String propertyName, Object value,
                                                   String... groups) {
        throw new UnsupportedOperationException("object missing in API?");
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
        if(prop == null) return null;
        ElementDescriptorImpl edesc = prop.getFeature(Jsr303Features.Property.ElementDescriptor);
        if (edesc == null) {
            edesc = new ElementDescriptorImpl();

            edesc.setElementType(prop.getAccess() == MetaProperty.ACCESS.METHOD ?
                    ElementType.METHOD : ElementType.FIELD);
            edesc.setReturnType(prop.getFeature(Features.Property.REF_BEAN_TYPE, prop.getType()));
            edesc.setCascaded(prop.getFeature(Features.Property.REF_CASCADE, false));
            edesc.setPropertyPath(propertyName);
            createConstraintDescriptors(edesc, prop.getValidations());
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
}
