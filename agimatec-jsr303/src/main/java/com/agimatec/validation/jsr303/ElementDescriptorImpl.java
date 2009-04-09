package com.agimatec.validation.jsr303;

import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.Validation;

import javax.validation.ConstraintDescriptor;
import javax.validation.ElementDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: MetaData class<br/>
 * User: roman.stumm <br/>
 * Date: 02.04.2008 <br/>
 * Time: 12:23:45 <br/>
 * Copyright: Agimatec GmbH 2008
 */
abstract class ElementDescriptorImpl implements ElementDescriptor {
    protected MetaBean metaBean;
    protected Class type;
    private Set<ConstraintDescriptor<?>> constraintDescriptors;

    public ElementDescriptorImpl(MetaBean metaBean, Validation[] validations) {
        this.metaBean = metaBean;
        this.type = metaBean.getBeanClass();
        createConstraintDescriptors(validations);
    }

    public ElementDescriptorImpl() {
    }

    public Class getType() {
        return type;
    }

    public Set<ConstraintDescriptor<?>> getConstraintDescriptors() {
        return constraintDescriptors;
    }

    /** return true if at least one constraint declaration is present on the element. */
    public boolean hasConstraints() {
        if (metaBean.getValidations().length > 0) return true;
        for (MetaProperty mprop : metaBean.getProperties()) {
            if (mprop.getValidations().length > 0) return true;
        }
        return false;
    }

    protected void createConstraintDescriptors(Validation[] validations) {
        final Set<ConstraintDescriptor<?>> cds = new HashSet(validations.length);
        for (Validation validation : validations) {
            if (validation instanceof ConstraintValidation) {
                ConstraintValidation cval = (ConstraintValidation) validation;
                cds.add(cval);
            }
        }
        setConstraintDescriptors(cds);
    }

    public void setConstraintDescriptors(
          Set<ConstraintDescriptor<?>> constraintDescriptors) {
        this.constraintDescriptors = constraintDescriptors;
    }

    public void setType(Class returnType) {
        this.type = returnType;
    }

}
