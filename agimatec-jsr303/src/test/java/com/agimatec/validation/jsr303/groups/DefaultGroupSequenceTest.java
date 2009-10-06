package com.agimatec.validation.jsr303.groups;

import junit.framework.TestCase;

import javax.validation.GroupDefinitionException;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hardy Ferentschik
 * @author Roman Stumm
 */
public class DefaultGroupSequenceTest extends TestCase {
    public void testAssertDefaultGroupSequenceIsExpandableWithDefaultAtEndOfSequence() {
        // create a dummy sequence
        Group a = new Group(GroupA.class);
        Group b = new Group(GroupB.class);
        Group c = new Group(GroupC.class);
        Group defaultGroup = new Group(Default.class);
        List<Group> sequence = new ArrayList<Group>();
        sequence.add(a);
        sequence.add(b);
        sequence.add(c);
        sequence.add(defaultGroup);

        Groups chain = new Groups();
        chain.insertSequence(sequence);

        // create test default sequence
        List<Group> defaultSequence = new ArrayList<Group>();
        defaultSequence.add(Group.DEFAULT);
        defaultSequence.add(new Group(GroupA.class));
        try {
            chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
            fail();
        } catch (GroupDefinitionException e) {
            // success
        }

        defaultSequence.clear();
        defaultSequence.add(new Group(GroupA.class));
        defaultSequence.add(new Group(Default.class));
        try {
            chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
            fail();
        } catch (GroupDefinitionException e) {
            // success
        }

        defaultSequence.clear();
        defaultSequence.add(Group.DEFAULT);
        defaultSequence.add(new Group(GroupC.class));
        try {
            chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
            fail();
        } catch (GroupDefinitionException e) {
            // success
        }

        defaultSequence.clear();
        defaultSequence.add(new Group(GroupC.class));
        defaultSequence.add(Group.DEFAULT);
        chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
    }


    public void testAssertDefaulGroupSequenceIsExpandableWithDefaultAtBeginningOfSequence() {
        // create a dummy sequence
        Group a = new Group(GroupA.class);
        Group b = new Group(GroupB.class);
        Group c = new Group(GroupC.class);
        Group defaultGroup = new Group(Default.class);
        List<Group> sequence = new ArrayList<Group>();
        sequence.add(defaultGroup);
        sequence.add(a);
        sequence.add(b);
        sequence.add(c);

        Groups chain = new Groups();
        chain.insertSequence(sequence);

        // create test default sequence
        List<Group> defaultSequence = new ArrayList<Group>();
        defaultSequence.add(Group.DEFAULT);
        defaultSequence.add(new Group(GroupA.class));
        chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);


        defaultSequence.clear();
        defaultSequence.add(new Group(GroupA.class));
        defaultSequence.add(Group.DEFAULT);
        try {
            chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
            fail();
        } catch (GroupDefinitionException e) {
            // success
        }

        defaultSequence.clear();
        defaultSequence.add(Group.DEFAULT);
        defaultSequence.add(new Group(GroupC.class));
        try {
            chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
            fail();
        } catch (GroupDefinitionException e) {
            // success
        }

        defaultSequence.clear();
        defaultSequence.add(new Group(GroupC.class));
        defaultSequence.add(Group.DEFAULT);
        try {
            chain.assertDefaultGroupSequenceIsExpandable(defaultSequence);
            fail();
        } catch (GroupDefinitionException e) {
            // success
        }
    }
}

interface TestSequence {
}

interface GroupA {
}

interface GroupB {
}

interface GroupC {
}
