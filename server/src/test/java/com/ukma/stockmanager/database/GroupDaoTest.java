package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GroupDaoTest extends DaoTest {

    @Test
    void createGroup() {
        Group newGroup = new Group(null, "Unique name", "description");
        Group group = GROUP_DAO.readGroup(newGroup.getName());
        assertNull(group);

        GROUP_DAO.create(newGroup);
        group = GROUP_DAO.readGroup(newGroup.getName());
        assertEquals(group, newGroup);
    }

    @Test
    void readAllGroups() {
        LinkedList<Group> realAllGroups = new LinkedList<>();
        Group first = new Group(null, "1", "1");
        Group second = new Group(null, "2", "2");
        Group third = new Group(null, "3", "3");
        Group forth = new Group(null, "4", "4");
        realAllGroups.add(initialGroup);
        realAllGroups.add(first);
        realAllGroups.add(second);
        realAllGroups.add(third);
        realAllGroups.add(forth);
        GROUP_DAO.create(first);
        GROUP_DAO.create(second);
        GROUP_DAO.create(third);
        GROUP_DAO.create(forth);

        assertEquals(realAllGroups, GROUP_DAO.readAll());
    }

    @Test
    void readGroupByName() {
        assertEquals(initialGroup, GROUP_DAO.readGroup(initialGroup.getName()));
    }

    @Test
    void readGroupById() {
        Group inDatabase = GROUP_DAO.readAll().get(0);
        int id = inDatabase.getId();
        assertEquals(initialGroup, GROUP_DAO.readGroup(id));
    }

    @Test
    void readGroupId() {
        Group inDatabase = GROUP_DAO.readAll().get(0);
        assertEquals(inDatabase.getId(), GROUP_DAO.readGroupId(initialGroup.getName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateGroupName(String newName) {
        int groupId = GROUP_DAO.readGroupId(initialGroup.getName());

        GROUP_DAO.updateGroupName(groupId, newName);
        assertEquals(newName, GROUP_DAO.readGroup(groupId).getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateGroupDescription(String newDescription) {
        int groupId = GROUP_DAO.readGroupId(initialGroup.getName());

        GROUP_DAO.updateGroupDescription(groupId, newDescription);
        assertEquals(newDescription, GROUP_DAO.readGroup(groupId).getDescription());
    }

    @Test
    void deleteGroup() {
        Group group = GROUP_DAO.readGroup(initialGroup.getName());
        assertEquals(group, initialGroup);

        GROUP_DAO.deleteGroup(GROUP_DAO.readGroupId(initialGroup.getName()));

        group = GROUP_DAO.readGroup(initialGroup.getName());
        assertNull(group);
    }
}
