package com.ukma.nechyporchuk.database;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sqlite.SQLiteException;

import static org.junit.jupiter.api.Assertions.*;

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
    private Item existingItem = new Item(
            0,
            "ExistingNote",
            "ExistingDescription",
            100,
            100.001,
            "ExistingProducer",
            0
    );
    private static Database database;

    @BeforeAll
    static void createDatabase() {
        database = new Database("TestDatabase1234567890");
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
        database.createItem(
                existingItem.getName(),
                existingItem.getDescription(),
                existingItem.getAmount(),
                existingItem.getCost(),
                existingItem.getProducer(),
                existingItem.getGroupID()
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
        database.deleteDatabase();
    }


    @Test
    void createGroup() {
    }

    @Test
    void createItem() {
    }

    @Test
    void readAllGroups() {
    }

    @Test
    void readGroup() {
    }

    @Test
    void testReadGroup() {
    }

    @Test
    void readGroupId() {
    }

    @Test
    void readGroupDescription() {
    }

    @Test
    void readGroupName() {
    }

    @Test
    void testReadGroupDescription() {
    }

    @Test
    void readItem() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateGroupName(String newName) {
        int groupId = database.readGroupId(initialGroup.getName());

        database.updateGroupName(groupId, newName);
        assertEquals(newName, database.readGroup(groupId).getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateGroupDescription(String newDescription) {
        int groupId = database.readGroupId(initialGroup.getName());

        database.updateGroupDescription(groupId, newDescription);
        assertEquals(newDescription, database.readGroup(groupId).getDescription());
    }

    @ParameterizedTest
    @ValueSource(strings = {"initialNote", "", "New name", ")($U@Y!$@!$DD"})
    void updateItemName(String newName) {
        int itemId = database.readItemId(initialItem.getName());

        database.updateItemName(itemId, newName);
        assertEquals(newName, database.readItem(itemId).getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExistingNote"})
    void updateItemNameWithExisting(String newName) {
        int itemId = database.readItemId(initialItem.getName());

        assertThrows(RuntimeException.class, () -> database.updateItemName(itemId, newName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateItemDescription(String newDescription) {
        int itemId = database.readItemId(initialItem.getName());

        database.updateItemDescription(itemId, newDescription);
        assertEquals(newDescription, database.readItem(itemId).getDescription());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 1000, -100, 0})
    void updateItemAmount(int newAmount) {
        int itemId = database.readItemId(initialItem.getName());

        database.updateItemAmount(itemId, newAmount);
        assertEquals(newAmount, database.readItem(itemId).getAmount());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 100.0123, 1000.999999, -100, 0.000000001})
    void updateItemCost(double newCost) {
        int itemId = database.readItemId(initialItem.getName());

        database.updateItemCost(itemId, newCost);
        assertEquals(newCost, database.readItem(itemId).getCost());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateItemProducer(String newProducer) {
        int itemId = database.readItemId(initialItem.getName());

        database.updateItemProducer(itemId, newProducer);
        assertEquals(newProducer, database.readItem(itemId).getProducer());
    }

    @Test
    void updateItemGroup() {
        int itemId = database.readItemId(initialItem.getName());

        database.updateItemGroup(itemId, initialGroup.getName());
        assertEquals(database.readGroupId(initialGroup.getName()), database.readItem(itemId).getGroupID());
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

    @Test
    void testReadItem() {
    }

    @Test
    void readItemId() {
    }

    @Test
    void readItemName() {
    }
}