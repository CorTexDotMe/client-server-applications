package com.ukma.nechyporchuk.database;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseTest {

    private Group initialGroup = new Group(
            0,
            "InitNote",
            "InitDescription"
    );

    private Item initialItem = new Item(
            0,
            "InitNote",
            "InitDescription",
            100,
            100.001,
            "InitProducer",
            0
    );

    private static Database database;

    @BeforeAll
    static void createDatabase() {
        database = new Database("TestDB");
    }

    @BeforeEach
    void createGroupAndItem() {
        database.createGroup(initialGroup.getName(), initialGroup.getDescription());
        database.createItem(
                initialItem.getName(),
                initialItem.getDescription(),
                initialItem.getAmount(),
                initialItem.getCost(),
                initialItem.getProducer(),
                initialItem.getGroupID()
        );
    }

    @AfterEach
    void deleteGroupAndItem() {
        for (Group group : database.readAllGroups())
            database.deleteGroup(group.getId());
        for (Item item : database.readAllItems())
            database.deleteItem(item.getId());
    }

    @AfterAll
    static void deleteDatabase() {

    }


    @Test
    void createGroup() {
    }

    @Test
    void createItem() {
    }

    @Test
    void readAllItems() {
    }

    @Test
    void updateGroupName() {
        String newName = "New name";
        int groupID = database.readGroupID(initialGroup.getName());

        database.updateGroupName(groupID, newName);
        assertEquals(newName, database.readGroup(groupID).getName());
    }

    @Test
    void updateGroupDescription() {
    }

    @Test
    void updateItemName() {
    }

    @Test
    void updateItemDescription() {
    }

    @Test
    void updateItemAmount() {
    }

    @Test
    void updateItemCost() {
    }

    @Test
    void updateItemProducer() {
    }

    @Test
    void updateItemGroup() {
    }

    @Test
    void deleteGroup() {
    }

    @Test
    void deleteItem() {
    }

    @Test
    void listItemsByGroup() {
    }

    @Test
    void listItemsByProducer() {
    }
}