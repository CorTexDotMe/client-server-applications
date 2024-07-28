package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.Group;
import com.ukma.stockmanager.core.entities.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemDaoTest extends DaoTest {

    @Test
    void createItem() {
        Item newItem = new Item(
                0,
                "Unique name",
                "description",
                10,
                10.0,
                "Hello",
                GROUP_DAO.readGroupId(initialGroup.getName()));
        Item item = ITEM_DAO.readItem(newItem.getName());
        assertNull(item);

        ITEM_DAO.createItem(
                newItem.getName(),
                newItem.getDescription(),
                newItem.getAmount(),
                newItem.getCost(),
                newItem.getProducer(),
                newItem.getGroupId()
        );
        item = ITEM_DAO.readItem(newItem.getName());
        assertEquals(item, newItem);
    }



    @Test
    void readItem() {
        assertEquals(initialItem, ITEM_DAO.readItem(initialItem.getName()));

        Item inDatabase = ITEM_DAO.readAllItems().get(0);
        assertEquals(initialItem, ITEM_DAO.readItem(inDatabase.getId()));
    }

    @Test
    void readItemId() {
        Item inDatabase = ITEM_DAO.readAllItems().get(0);
        assertEquals(inDatabase.getId(), ITEM_DAO.readItemId(initialItem.getName()));
    }

    @Test
    void readItemName() {
        Item inDatabase = ITEM_DAO.readAllItems().get(0);
        assertEquals(initialItem.getName(), ITEM_DAO.readItemName(inDatabase.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"initialNote", "", "New name", ")($U@Y!$@!$DD"})
    void updateItemName(String newName) {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        ITEM_DAO.updateItemName(itemId, newName);
        assertEquals(newName, ITEM_DAO.readItem(itemId).getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExistingNote"})
    void updateItemNameWithExisting(String newName) {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        assertFalse(ITEM_DAO.updateItemName(itemId, newName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateItemDescription(String newDescription) {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        ITEM_DAO.updateItemDescription(itemId, newDescription);
        assertEquals(newDescription, ITEM_DAO.readItem(itemId).getDescription());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 1000, -100, 0})
    void updateItemAmount(int newAmount) {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        ITEM_DAO.updateItemAmount(itemId, newAmount);
        assertEquals(newAmount, ITEM_DAO.readItem(itemId).getAmount());
    }

    @Test
    void updateItemAmountConcurrently() throws InterruptedException {
        int numThreads = 10;
        int itemId = ITEM_DAO.readItemId(initialItem.getName());
        int initialAmount = initialItem.getAmount();
        int amountToAdd = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        var a = new ArrayList<String>();
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                ITEM_DAO.addItemAmount(itemId, amountToAdd);
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(a);

        // Verify the final amount
        int expectedFinalAmount = initialAmount + (numThreads * amountToAdd);
        assertEquals(expectedFinalAmount, ITEM_DAO.readItem(itemId).getAmount());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 100.0123, 1000.999999, -100, 0.000000001})
    void updateItemCost(double newCost) {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        ITEM_DAO.updateItemCost(itemId, newCost);
        assertEquals(newCost, ITEM_DAO.readItem(itemId).getCost());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateItemProducer(String newProducer) {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        ITEM_DAO.updateItemProducer(itemId, newProducer);
        assertEquals(newProducer, ITEM_DAO.readItem(itemId).getProducer());
    }

    @Test
    void updateItemGroup() {
        int itemId = ITEM_DAO.readItemId(initialItem.getName());

        ITEM_DAO.updateItemGroup(itemId, initialGroup.getName());
        assertEquals(GROUP_DAO.readGroupId(initialGroup.getName()), ITEM_DAO.readItem(itemId).getGroupId());
    }

    @Test
    void deleteItem() {
        Item item = ITEM_DAO.readItem(initialItem.getName());
        assertEquals(item, initialItem);

        ITEM_DAO.deleteItem(ITEM_DAO.readItemId(initialItem.getName()));

        item = ITEM_DAO.readItem(initialItem.getName());
        assertNull(item);
    }

    @Test
    void listItemsByGroup() {
        int groupId = GROUP_DAO.readGroupId(initialGroup.getName());

        ITEM_DAO.createItem("", "", 0, 0, "", -1);
        List<Item> itemList = ITEM_DAO.readAllItems().stream().filter(
                item -> item.getGroupId() == groupId
        ).toList();

        assertEquals(itemList, ITEM_DAO.listItemsByGroup(groupId));
    }

    @Test
    void orderByName() {
        List<Item> ascList = ITEM_DAO.listItemsByNameInOrder(true);
        assertEquals(existingItem, ascList.get(0));
        assertEquals(initialItem, ascList.get(1));

        List<Item> descList = ITEM_DAO.listItemsByNameInOrder(false);
        assertEquals(initialItem, descList.get(0));
        assertEquals(existingItem, descList.get(1));
    }

    @Test
    void orderByGroup() {
        Group group = new Group(null, "", "");
        GROUP_DAO.create(group);
        Item item = new Item(0, "test", "", 0, 0, "", GROUP_DAO.readGroupId(group.getName()));
        ITEM_DAO.createItem(item.getName(), item.getDescription(), item.getAmount(), item.getCost(), item.getProducer(), item.getGroupId());

        int groupId = GROUP_DAO.readGroupId(initialGroup.getName());
        ITEM_DAO.createItem("", "", 0, 0, "", -1);
        List<Item> itemList = new ArrayList<>(ITEM_DAO.readAllItems().stream().filter(
                element -> element.getGroupId() == groupId
        ).toList());

        itemList.sort(Comparator.comparing(Item::getName));
        assertEquals(itemList, ITEM_DAO.listItemsByGroupInOrder(groupId, true));

        itemList.sort((first, second) -> second.getName().compareTo(first.getName()));
        assertEquals(itemList, ITEM_DAO.listItemsByGroupInOrder(groupId, false));
    }
}
