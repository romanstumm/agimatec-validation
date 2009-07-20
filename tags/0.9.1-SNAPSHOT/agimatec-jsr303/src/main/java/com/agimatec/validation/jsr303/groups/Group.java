package com.agimatec.validation.jsr303.groups;

/** wrap an interface that represents a single group. */
public class Group {
    private Class<?> group;

    public Group(Class<?> group) {
        this.group = group;
    }

    public Class<?> getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "Group{" + "group=" + group + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group1 = (Group) o;

        return !(group != null ? !group.equals(group1.group) : group1.group != null);
    }

    @Override
    public int hashCode() {
        return (group != null ? group.hashCode() : 0);
    }
}
