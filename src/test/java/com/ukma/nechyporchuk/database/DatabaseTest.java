package com.ukma.nechyporchuk.database;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sqlite.SQLiteException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private final Group initialGroup = new Group(
            0,
            "InitGroup",
            "InitDescription"
    );

    private final Item initialItem = new Item(
            0,
            "InitNote",
            "InitDescription",
            100,
            100.001,
            "InitProducer",
            1
    );
    private final Item existingItem = new Item(
            0,
            "ExistingNote",
            "ExistingDescription",
            100,
            100.001,
            "ExistingProducer",
            1
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
                database.readGroupId(initialGroup.getName())
        );
        database.createItem(
                existingItem.getName(),
                existingItem.getDescription(),
                existingItem.getAmount(),
                existingItem.getCost(),
                existingItem.getProducer(),
                database.readGroupId(initialGroup.getName())
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
        Group newGroup = new Group(0, "Unique name", "description");
        Group group = database.readGroup(newGroup.getName());
        assertNull(group);

        database.createGroup(newGroup.getName(), newGroup.getDescription());
        group = database.readGroup(newGroup.getName());
        assertEquals(group, newGroup);
    }

    @Test
    void createItem() {
        Item newItem = new Item(
                0,
                "Unique name",
                "description",
                10,
                10.0,
                "Hello",
                database.readGroupId(initialGroup.getName()));
        Item item = database.readItem(newItem.getName());
        assertNull(item);

        database.createItem(
                newItem.getName(),
                newItem.getDescription(),
                newItem.getAmount(),
                newItem.getCost(),
                newItem.getProducer(),
                newItem.getGroupID()
        );
        item = database.readItem(newItem.getName());
        assertEquals(item, newItem);
    }

    @Test
    void readAllGroups() {
        LinkedList<Group> realAllGroups = new LinkedList<>();
        Group first = new Group(0, "1", "1");
        Group second = new Group(0, "2", "2");
        Group third = new Group(0, "3", "3");
        Group forth = new Group(0, "4", "4");
        realAllGroups.add(initialGroup);
        realAllGroups.add(first);
        realAllGroups.add(second);
        realAllGroups.add(third);
        realAllGroups.add(forth);
        database.createGroup(first.getName(), first.getDescription());
        database.createGroup(second.getName(), second.getDescription());
        database.createGroup(third.getName(), third.getDescription());
        database.createGroup(forth.getName(), forth.getDescription());

        assertEquals(realAllGroups, database.readAllGroups());
    }

    @Test
    void readGroupByName() {
        assertEquals(initialGroup, database.readGroup(initialGroup.getName()));
    }

    @Test
    void readGroupById() {
        Group inDatabase = database.readAllGroups().get(0);
        int id = inDatabase.getId();
        assertEquals(initialGroup, database.readGroup(id));
    }

    @Test
    void readGroupId() {
        Group inDatabase = database.readAllGroups().get(0);
        assertEquals(inDatabase.getId(), database.readGroupId(initialGroup.getName()));
    }

    @Test
    void readGroupDescription() {
        assertEquals(initialGroup.getDescription(), database.readGroupDescription(initialGroup.getName()));

        Group inDatabase = database.readAllGroups().get(0);
        assertEquals(initialGroup.getDescription(), database.readGroupDescription(inDatabase.getId()));
    }

    @Test
    void readGroupName() {
        Group inDatabase = database.readAllGroups().get(0);
        assertEquals(initialGroup.getName(), database.readGroupName(inDatabase.getId()));

    }

    @Test
    void readItem() {
        assertEquals(initialItem, database.readItem(initialItem.getName()));

        Item inDatabase = database.readAllItems().get(0);
        assertEquals(initialItem, database.readItem(inDatabase.getId()));
    }

    @Test
    void readItemId() {
        Item inDatabase = database.readAllItems().get(0);
        assertEquals(inDatabase.getId(), database.readItemId(initialItem.getName()));
    }

    @Test
    void readItemName() {
        Item inDatabase = database.readAllItems().get(0);
        assertEquals(initialItem.getName(), database.readItemName(inDatabase.getId()));
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
        Group group = database.readGroup(initialGroup.getName());
        assertEquals(group, initialGroup);

        database.deleteGroup(database.readGroupId(initialGroup.getName()));

        group = database.readGroup(initialGroup.getName());
        assertNull(group);
    }

    @Test
    void deleteItem() {
        Item item = database.readItem(initialItem.getName());
        assertEquals(item, initialItem);

        database.deleteItem(database.readItemId(initialItem.getName()));

        item = database.readItem(initialItem.getName());
        assertNull(item);
    }

    @Test
    void listItemsByGroup() {
        int groupId = database.readGroupId(initialGroup.getName());

        database.createItem("", "", 0, 0, "", -1);
        List<Item> itemList = database.readAllItems().stream().filter(
                item -> item.getGroupID() == groupId
        ).toList();

        assertEquals(itemList, database.listItemsByGroup(groupId));
    }

    @Test
    void orderByName() {
        List<Item> ascList = database.listItemsByNameInOrder(true);
        assertEquals(existingItem, ascList.get(0));
        assertEquals(initialItem, ascList.get(1));

        List<Item> descList = database.listItemsByNameInOrder(false);
        assertEquals(initialItem, descList.get(0));
        assertEquals(existingItem, descList.get(1));
    }

    @Test
    void orderByGroup() {
        database.createGroup("", "");
        Item item = new Item(0, "test", "", 0, 0, "", database.readGroupId(""));
        database.createItem(item.getName(), item.getDescription(), item.getAmount(), item.getCost(), item.getProducer(), item.getGroupID());

        int groupId = database.readGroupId(initialGroup.getName());
        database.createItem("", "", 0, 0, "", -1);
        List<Item> itemList = new ArrayList<>(database.readAllItems().stream().filter(
                element -> element.getGroupID() == groupId
        ).toList());

        itemList.sort(Comparator.comparing(Item::getName));
        assertEquals(itemList, database.listItemsByGroupInOrder(groupId, true));

        itemList.sort((first, second) -> second.getName().compareTo(first.getName()));
        assertEquals(itemList, database.listItemsByGroupInOrder(groupId, false));
    }
}