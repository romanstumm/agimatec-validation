package com.agimatec.validation.jsr303;

import com.agimatec.validation.BeanValidationContext;
import com.agimatec.validation.model.MetaBean;
import com.agimatec.validation.model.MetaProperty;
import com.agimatec.validation.model.ValidationListener;

import javax.validation.Constraint;
import javax.validation.ConstraintDescriptor;
import javax.validation.MessageResolver;
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

    private final MessageResolver messageResolver;
    private final LinkedList propertyStack = new LinkedList();
    private String[] requestedGroups;
    private List<String> sequencedGroups;
    private String currentGroup;

    /**
     * contains the validation constraints that have already been processed during
     * this validation routine (as part of a previous group match)
     */
    private IdentityHashMap<Object, IdentityHashMap<Constraint, Object>> validatedConstraints =
            new IdentityHashMap();
    private ConstraintDescriptor currentConstraint;


    public GroupValidationContextImpl(ValidationListener listener,
                                      MessageResolver aMessageResolver) {
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
    public boolean collectValidated(Object bean, Constraint constraint) {
        IdentityHashMap<Constraint, Object> beanConstraints = validatedConstraints.get(bean);
        if (beanConstraints == null) {
            beanConstraints = new IdentityHashMap();
            validatedConstraints.put(bean, beanConstraints);
        }
        return beanConstraints.put(constraint, Boolean.TRUE) == null;
    }

    public boolean isValidated(Object bean, Constraint constraint) {
        IdentityHashMap<Constraint, Object> beanConstraints = validatedConstraints.get(bean);
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

    public void setRequestedGroups(String[] requestedGroups) {
        this.requestedGroups = requestedGroups;
    }

    public String[] getRequestedGroups() {
        return requestedGroups;
    }

    public void setSequencedGroups(List<String> sequencedGroups) {
        this.sequencedGroups = sequencedGroups;
    }

    public List<String> getSequencedGroups() {
        if (sequencedGroups != null) {
            return sequencedGroups;
        }

        String[] groups = getRequestedGroups();
        if (groups == null || groups.length == 0) {
            groups = DEFAULT_GROUPS;
        }
        Map<String, String[]> groupSeqMap = getMetaBean().getFeature(Jsr303Features.Bean.GROUP_SEQ);
        List<String> sequencedGroups = new ArrayList();
        for (String eachGroup : groups) {
            if (groupSeqMap != null) {
                String[] theSeq = groupSeqMap.get(eachGroup);
                if (theSeq != null) {
                    for (String eachSeq : theSeq) {
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

    private void addSequences(Map<String, String[]> groupSeqMap, String eachSeq,
                              List<String> sequence) {
        if (!sequence.contains(eachSeq)) {
            String[] nextSeq = groupSeqMap.get(eachSeq);
            if (nextSeq != null) {
                for (String eachNextSeq : nextSeq) {
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

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void setCurrentConstraint(ConstraintDescriptor constraint) {
        currentConstraint = constraint;
    }

    public ConstraintDescriptor getCurrentConstraint() {
        return currentConstraint;
    }

    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
}
