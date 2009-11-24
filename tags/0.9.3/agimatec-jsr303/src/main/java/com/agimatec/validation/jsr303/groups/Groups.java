/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
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