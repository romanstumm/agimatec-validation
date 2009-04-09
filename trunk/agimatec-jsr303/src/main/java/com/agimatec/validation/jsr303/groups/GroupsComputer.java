package com.agimatec.validation.jsr303.groups;


import javax.validation.GroupSequence;
import javax.validation.ValidationException;
import javax.validation.groups.Default;
import java.util.*;

/**
 * Description: compute group order, based on the hibernate validator algorithm
 * to guarantee compatibility with interpretation of spec by reference implementation <br/>
 * User: roman <br/>
 * Date: 09.04.2009 <br/>
 * Time: 09:15:50 <br/>
 * Copyright: Agimatec GmbH
 */
public class GroupsComputer {
    /** The default group array used in case any of the validate methods is called without a group. */
    public static final Class<?>[] DEFAULT_GROUP_ARRAY = new Class<?>[]{Default.class};


    private final Map<Class<?>, List<Group>> resolvedSequences =
          new HashMap<Class<?>, List<Group>>();

    public Groups computeGroups(Class<?>[] groups) {
        if (groups == null) {
            throw new IllegalArgumentException("null passed as group");
        }

        // if no groups is specified use the default
        if (groups.length == 0) {
            groups = DEFAULT_GROUP_ARRAY;
        }

        return computeGroups(Arrays.asList(groups));
    }


    public Groups computeGroups(Collection<Class<?>> groups) {
        if (groups == null || groups.size() == 0) {
            throw new IllegalArgumentException(
                  "At least one group has to be specified.");
        }

        for (Class<?> clazz : groups) {
            if (!clazz.isInterface()) {
                throw new ValidationException(
                      "A group has to be an interface. " + clazz.getName() + " is not.");
            }
        }

        Groups chain = new Groups();
        for (Class<?> clazz : groups) {
            GroupSequence anno = clazz.getAnnotation(GroupSequence.class);
            if (anno == null) {
                Group group = new Group(clazz);
                chain.insertGroup(group);
                insertInheritedGroups(clazz, chain);
            } else {
                insertSequence(clazz, anno, chain);
            }
        }

        return chain;
    }

    private void insertInheritedGroups(Class<?> clazz, Groups chain) {
        for (Class<?> extendedInterface : clazz.getInterfaces()) {
            Group group = new Group(extendedInterface);
            chain.insertGroup(group);
            insertInheritedGroups(extendedInterface, chain);
        }
    }

    private void insertSequence(Class<?> clazz, GroupSequence anno, Groups chain) {
        List<Group> sequence;
        if (resolvedSequences.containsKey(clazz)) {
            sequence = resolvedSequences.get(clazz);
        } else {
            sequence = resolveSequence(clazz, anno, new HashSet<Class<?>>());
        }
        chain.insertSequence(sequence);
    }

    private List<Group> resolveSequence(Class<?> group, GroupSequence sequenceAnnotation,
                                        Set<Class<?>> processedSequences) {
        if (processedSequences.contains(group)) {
            throw new ValidationException("Cyclic dependency in groups definition");
        } else {
            processedSequences.add(group);
        }
        List<Group> resolvedGroupSequence = new LinkedList<Group>();
        Class<?>[] sequenceArray = sequenceAnnotation.value();
        for (Class<?> clazz : sequenceArray) {
            GroupSequence anno = clazz.getAnnotation(GroupSequence.class);
            if (anno == null) {
                resolvedGroupSequence.add(new Group(clazz)); // group part of sequence
            } else {
                List<Group> tmpSequence =
                      resolveSequence(clazz, anno, processedSequences);  // recursion!
                resolvedGroupSequence.addAll(tmpSequence);
            }
        }
        resolvedSequences.put(group, resolvedGroupSequence);
        return resolvedGroupSequence;
    }
}
