package com.agimatec.validation.jsr303.groups;


import java.util.LinkedList;
import java.util.List;

/** defines the order in to validate groups during validation */
public class Groups {

    /** The list of single groups. */
    protected List<Group> groups = new LinkedList<Group>();

    /** The list of sequences. */
    protected List<List<Group>> sequences = new LinkedList<List<Group>>();

    public List<Group> getGroups() {
        return groups;
    }

    public List<List<Group>> getSequences() {
        return sequences;
    }

    void insertGroup(Group group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
    }

    void insertSequence(List<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }

        if (!sequences.contains(groups)) {
            sequences.add(groups);
        }
    }
}