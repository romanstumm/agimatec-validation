package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.Features;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.Validation;

import javax.validation.BeanDescriptor;
import javax.validation.ConstraintViolation;
import javax.validation.PropertyDescriptor;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * API class -
 * Description:
 * instance is able to validate bean instances (and the associated objects).
 * concurrent, multithreaded access implementation is safe.
 * It is recommended to cache the instance.
 * <br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 13:36:33 <br/>
 * Copyright: Agimatec GmbH 2008
 */
public class ClassValidator implements Validator {
    protected final AgimatecValidatorFactory factory;

    public ClassValidator(AgimatecValidatorFactory factory) {
        this.factory = factory;
    }

    /** validate all constraints on object */
    public <T> Set<ConstraintViolation<T>> validate(T object, String... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(object.getClass());
        GroupValidationContext context = createContext(metaBean, object, groups);
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
        return result.getConstaintViolations();
    }

    /**
     * validate all constraints on <code>propertyName</code> property of object
     *
     * @param propertyName - the attribute name, or nested property name (e.g. prop[2].subpropA.subpropB)
     */
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName,
                                                            String... groups) {
        if (object == null) throw new IllegalArgumentException("cannot validate null");
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(object.getClass());
        GroupValidationContext context = createContext(metaBean, object, groups);
        ConstraintValidationListener result = (ConstraintValidationListener) context.getListener();
        NestedMetaProperty nestedProp = getNestedProperty(metaBean, object, propertyName);
        context.setMetaProperty(nestedProp.getMetaProperty());
        if (nestedProp.isNested()) {
            context.setFixedValue(nestedProp.getValue());
        } else {
            context.setMetaProperty(nestedProp.getMetaProperty());
        }
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
        return result.getConstaintViolations();
    }

    /**
     * find the MetaProperty for the given propertyName,
     * which could contain a path, following the path on a given object to resolve
     * types at runtime from the instance
     */
    private NestedMetaProperty getNestedProperty(MetaBean metaBean, Object t, String propertyName) {
        NestedMetaProperty nested = new NestedMetaProperty(propertyName, t);
        nested.setMetaBean(metaBean);
        nested.parse();
        return nested;
    }

    /**
     * validate all constraints on <code>propertyName</code> property
     * if the property value is <code>value</code>
     */
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName,
                                                         Object value, String... groups) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(beanType);
        GroupValidationContext context = createContext(metaBean, null, groups);
        ConstraintValidationListener result = (ConstraintValidationListener) context.getListener();
        context.setMetaProperty(getNestedProperty(metaBean, null, propertyName).getMetaProperty());
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
        return result.getConstaintViolations();
    }

    protected <T> GroupValidationContext createContext(MetaBean metaBean, T object,
                                                       String[] groups) {
        ConstraintValidationListener<T> listener = new ConstraintValidationListener<T>(object);
        GroupValidationContextImpl context = new GroupValidationContextImpl(listener,
                factory.getMessageResolver());
        if (groups == null || groups.length == 0)
            groups = GroupValidationContext.DEFAULT_GROUPS;
        context.setRequestedGroups(groups);
        context.setBean(object, metaBean);
        return context;
    }

    /**
     * @param clazz class type evaluated
     * @return true if at least one constraint declaration is present for the given bean
     *         or if one property is marked for validation cascade
     */
    public boolean hasConstraints(Class<?> clazz) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
            if (mprop.getMetaBean() != null &&
                    mprop.getFeature(Features.Property.REF_CASCADE, true)) return true;
        }
        return false;
    }

    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        ElementDescriptorImpl edesc = metaBean.getFeature(Jsr303Features.Bean.BeanDescriptor);
        if (edesc == null) {
            edesc = new ElementDescriptorImpl();
            edesc.setReturnType(metaBean.getBeanClass());
            createConstraintDescriptors(edesc, metaBean.getValidations());
            metaBean.putFeature(Jsr303Features.Bean.BeanDescriptor, edesc);
        }
        return edesc;
    }

    private void createConstraintDescriptors(ElementDescriptorImpl edesc,
                                             Validation[] validations) {
        edesc.setConstraintDescriptors(new ArrayList(validations.length));
        for (Validation validation : validations) {
            if (validation instanceof ConstraintValidation) {
                ConstraintValidation cval = (ConstraintValidation) validation;
                edesc.getConstraintDescriptors().add(cval);
            }
        }
    }

    public PropertyDescriptor getConstraintsForProperty(Class<?> clazz, String propertyName) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        MetaProperty prop = metaBean.getProperty(propertyName);
        if (prop == null) return null;
        ElementDescriptorImpl edesc = prop.getFeature(Jsr303Features.Property.ElementDescriptor);
        if (edesc == null) {
            edesc = new ElementDescriptorImpl();
            edesc.setReturnType(prop.getFeature(Features.Property.REF_BEAN_TYPE, prop.getType()));
            edesc.setCascaded(prop.getFeature(Features.Property.REF_CASCADE, false));
            edesc.setPropertyPath(propertyName);
            createConstraintDescriptors(edesc, prop.getValidations());
            prop.putFeature(Jsr303Features.Property.ElementDescriptor, edesc);
        }
        return edesc;
    }

    /** @return return the property names having at least a constraint defined */
    public Set<String> getPropertiesWithConstraints(Class<?> clazz) {
        MetaBean metaBean = factory.getMetaBeanManager().findForClass(clazz);
        Set<String> validatedProperties = new HashSet();
        for (MetaProperty prop : metaBean.getProperties()) {
            if (prop.getValidations().length > 0 || (prop.getMetaBean() != null &&
                    prop.getFeature(Features.Property.REF_CASCADE, true))) {
                validatedProperties.add(prop.getName());
            }
        }
        return validatedProperties;
    }

    public AgimatecValidatorFactory getFactory() {
        return factory;
    }

}
