package com.agimatec.utility.validation.jsr303;

import com.agimatec.utility.validation.ValidationContext;
import com.agimatec.utility.validation.model.MetaBean;
import com.agimatec.utility.validation.model.MetaProperty;

import javax.validation.Constraint;
import javax.validation.MessageResolver;
import java.util.*;

/**
 * Description: instance per validation process, not thread-safe<br/>
 * User: roman.stumm <br/>
 * Date: 01.04.2008 <br/>
 * Time: 16:32:35 <br/>
 * Copyright: Agimatec GmbH 2008
 */
class GroupValidationContext extends ValidationContext {
    public static final String DEFAULT_GROUP = "default";
    public static final String[] DEFAULT_GROUPS = {DEFAULT_GROUP};
    public static final List<String> DEFAULT_SEQUENCE;

    static {
        DEFAULT_SEQUENCE = new ArrayList(1);
        DEFAULT_SEQUENCE.add(DEFAULT_GROUP);
    }

    private final MessageResolver messageResolver;
    private final LinkedList<String> propertyStack = new LinkedList();
    private String[] requestedGroups;
    private List<String> sequencedGroups;
    private String currentGroup;

    /**
     * contains the validation constraints that have already been processed during
     * this validation routine (as part of a previous group match)
     */
    private IdentityHashMap<Object, IdentityHashMap<Constraint, Object>> validatedConstraints =
            new IdentityHashMap();


    public GroupValidationContext(MessageResolver aMessageResolver) {
         this.messageResolver = aMessageResolver;
    }

    @Override
    protected void moveDown(MetaProperty prop) {
        propertyStack.addLast(prop.getName());
        super.moveDown(prop);   // call super!
    }

    @Override
    protected void moveUp(Object bean, MetaBean metaBean) {
        propertyStack.removeLast();
        super.moveUp(bean, metaBean); // call super!
    }

    /** @return true when the constraint for this object was not already validated in this context */
    public boolean collectValidated(Object bean, Constraint constraint) {
        IdentityHashMap<Constraint, Object> beanConstraints = validatedConstraints.get(bean);
        if(beanConstraints == null) {
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
     * to the previous rules
     *
     * @return the path in dot notation
     */
    public String getPropertyPath() {
        StringBuilder sb = new StringBuilder();
        for (String prop : propertyStack) {
            sb.append(prop);
            sb.append('.');
        }
        if (getMetaProperty() != null) {
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

    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
}
