package com.agimatec.validation.jsr303.groups;

import com.agimatec.validation.jsr303.example.Address;
import com.agimatec.validation.jsr303.example.First;
import com.agimatec.validation.jsr303.example.Last;
import com.agimatec.validation.jsr303.example.Second;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * GroupListComputer Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>04/09/2009</pre>
 */
public class GroupsComputerTest extends TestCase {
    GroupsComputer groupsComputer;

    public GroupsComputerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        groupsComputer = new GroupsComputer();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(GroupsComputerTest.class);
    }

    public void testComputeGroupsNotAnInterface() {
        Set<Class<?>> groups = new HashSet<Class<?>>();
        groups.add(String.class);
        try {
            groupsComputer.computeGroups(groups);
            fail();
        } catch (ValidationException ex) {

        }
    }

    public void testGroupChainForNull() {
        try {
            groupsComputer.computeGroups((Class<?>[]) null);
            fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testGroupChainForEmptySet() {
        try {
            groupsComputer.computeGroups(new HashSet<Class<?>>());
            fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testCyclicGroupSequences() {
        try {
            Set<Class<?>> groups = new HashSet<Class<?>>();
            groups.add(CyclicGroupSequence1.class);
            groupsComputer.computeGroups(groups);
            fail();
        } catch (ValidationException ex) {

        }
    }

    public void testCyclicGroupSequence() {
        try {
            Set<Class<?>> groups = new HashSet<Class<?>>();
            groups.add(CyclicGroupSequence.class);
            groupsComputer.computeGroups(groups);
            fail();
        } catch (ValidationException ex) {

        }
    }

    public void testGroupDuplicates() {
        Set<Class<?>> groups = new HashSet<Class<?>>();
        groups.add(First.class);
        groups.add(Second.class);
        groups.add(Last.class);
        Groups chain = groupsComputer.computeGroups(groups);
        assertEquals(3, chain.groups.size());

        groups.clear();
        groups.add(First.class);
        groups.add(First.class);
        chain = groupsComputer.computeGroups(groups);
        assertEquals(1, chain.groups.size());

        groups.clear();
        groups.add(First.class);
        groups.add(Last.class);
        groups.add(First.class);
        chain = groupsComputer.computeGroups(groups);
        assertEquals(2, chain.groups.size());
    }


    public void testSequenceResolution() {
        Set<Class<?>> groups = new HashSet<Class<?>>();
        groups.add(Address.Complete.class);
        Groups chain = groupsComputer.computeGroups(groups);
        Iterator<List<Group>> sequences = chain.getSequences().iterator();
        List<Group> sequence = sequences.next();

        assertEquals(Address.class, sequence.get(0).getGroup());
        assertEquals(Address.HighLevelCoherence.class, sequence.get(1).getGroup());
    }
}
