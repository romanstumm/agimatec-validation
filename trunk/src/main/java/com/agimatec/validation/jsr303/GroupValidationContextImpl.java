package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.ValidationListener;

import javax.validation.ConstraintDescriptor;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import java.util.*;

/**
 * Description: instance per validation process, not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 16:32:35 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class GroupValidationContextImpl extends BeanValidationContext
        implements GroupValidationContext {

    private final MessageInterpolator messageResolver;
    private final LinkedList propertyStack = new LinkedList();
    private Class<?>[] requestedGroups;
    private List<Class<?>> sequencedGroups;
    private Class<?> currentGroup;

    /**
     * contains the validation constraints that have already been processed during
     * this validation routine (as part of a previous group match)
     */
    private IdentityHashMap<Object, IdentityHashMap<ConstraintValidator, Object>> validatedConstraints =
            new IdentityHashMap();
    private ConstraintDescriptor currentConstraint;


    public GroupValidationContextImpl(ValidationListener listener,
                                      MessageInterpolator aMessageResolver) {
        super(listener);
        this.messageResolver = aMessageResolver;
    }

    @Override
    public void setCurrentIndex(int index) {
        super.setCurrentIndex(index);   // call super!
        Object last = propertyStack.getLast();
        if (last instanceof Integer) {
            propertyStack.removeLast();
        }
        propertyStack.addLast(index);
    }

    @Override
    public void moveDown(MetaProperty prop) {
        propertyStack.addLast(prop.getName());
        super.moveDown(prop);   // call super!
    }

    @Override
    public void moveUp(Object bean, MetaBean metaBean) {
        if (propertyStack.removeLast() instanceof Integer) {
            propertyStack.removeLast();
        }
        super.moveUp(bean, metaBean); // call super!
    }

    /** @return true when the constraint for this object was not already validated in this context */
    public boolean collectValidated(Object bean, ConstraintValidator constraint) {
        IdentityHashMap<ConstraintValidator, Object> beanConstraints = validatedConstraints.get(bean);
        if (beanConstraints == null) {
            beanConstraints = new IdentityHashMap();
            validatedConstraints.put(bean, beanConstraints);
        }
        return beanConstraints.put(constraint, Boolean.TRUE) == null;
    }

    public boolean isValidated(Object bean, ConstraintValidator constraint) {
        IdentityHashMap<ConstraintValidator, Object> beanConstraints = validatedConstraints.get(bean);
        return beanConstraints != null && beanConstraints.containsKey(constraint);
    }

    public void resetValidatedConstraints() {
        validatedConstraints.clear();
    }

    /**
     * if an associated object is validated,
     * add the association field or JavaBeans property name and a dot ('.') as a prefix
     * to the previous rules.
     * uses prop[index] in property path for elements in to-many-relationships.
     *
     * @return the path in dot notation
     */
    public String getPropertyPath() {
        StringBuilder sb = new StringBuilder();
        boolean dot = false;
        for (Object prop : propertyStack) {
            if (prop instanceof String) {
                if (dot) sb.append('.');
                sb.append(prop);
                dot = true;
            } else if (prop instanceof Integer) {
                sb.append('[');
                sb.append(prop);
                sb.append(']');
                dot = true;
            }
        }
        if (getMetaProperty() != null) {
            if (dot) sb.append('.');
            sb.append(getMetaProperty().getName());
        }
        return sb.toString();
    }

    public void setRequestedGroups(Class<?>[] requestedGroups) {
        this.requestedGroups = requestedGroups;
    }

    public Class<?>[] getRequestedGroups() {
        return requestedGroups;
    }

    public void setSequencedGroups(List<Class<?>> sequencedGroups) {
        this.sequencedGroups = sequencedGroups;
    }

    public List<Class<?>> getSequencedGroups() {
        if (sequencedGroups != null) {
            return sequencedGroups;
        }

        Class<?>[] groups = getRequestedGroups();
        if (groups == null || groups.length == 0) {
            groups = DEFAULT_GROUPS;
        }
        Map<String, Class<?>[]> groupSeqMap = getMetaBean().getFeature(Jsr303Features.Bean.GROUP_SEQ);
        List<Class<?>> sequencedGroups = new ArrayList();
        for (Class<?> eachGroup : groups) {
            if (groupSeqMap != null) {
                Class<?>[] theSeq = groupSeqMap.get(eachGroup);
                if (theSeq != null) {
                    for (Class<?> eachSeq : theSeq) {
                        addSequences(groupSeqMap, eachSeq, sequencedGroups);
                    }
                    continue;
                }
            }
            if (!sequencedGroups.contains(eachGroup)) sequencedGroups.add(eachGroup);
        }
        setSequencedGroups(sequencedGroups);
        return sequencedGroups;
    }

    private void addSequences(Map<String, Class<?>[]> groupSeqMap, Class<?> eachSeq,
                              List<Class<?>> sequence) {
        if (!sequence.contains(eachSeq)) {
            Class<?>[] nextSeq = groupSeqMap.get(eachSeq);
            if (nextSeq != null) {
                for (Class<?> eachNextSeq : nextSeq) {
                    /**
                     * Group sequences are recursively resolved: the user must make sure 
                     * no circular graph is defined by the group sequence definitions.
                     */
                    addSequences(groupSeqMap, eachNextSeq, sequence);  // recursion!
                }
            } else {
                sequence.add(eachSeq);
            }
        }
    }

    public Class<?> getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Class<?> currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void setCurrentConstraint(ConstraintDescriptor constraint) {
        currentConstraint = constraint;
    }

    public ConstraintDescriptor getCurrentConstraint() {
        return currentConstraint;
    }

    public MessageInterpolator getMessageResolver() {
        return messageResolver;
    }
}
