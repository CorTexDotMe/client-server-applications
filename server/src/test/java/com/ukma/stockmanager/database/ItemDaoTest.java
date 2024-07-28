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

import static org.junit.jupiter.api.Assertions.*;

public class ItemDaoTest extends DaoTest {

    @Test
    void createItem() {
        Item newItem = new Item(
                null,
                "Unique name",
                "description",
                10,
                10.0,
                "Hello",
                initialGroup.getId()
        );
        Item item = ITEM_DAO.readItem(newItem.getName());
        assertNull(item);

        ITEM_DAO.createItem(newItem);
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
        assertEquals(inDatabase.getId(), initialItem.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"initialNote", "", "New name", ")($U@Y!$@!$DD"})
    void updateItemName(String newName) {
        ITEM_DAO.updateItemName(initialItem.getId(), newName);
        assertEquals(newName, ITEM_DAO.readItem(initialItem.getId()).getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ExistingNote"})
    void updateItemNameWithExisting(String newName) {
        assertFalse(ITEM_DAO.updateItemName(initialItem.getId(), newName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateItemDescription(String newDescription) {
        ITEM_DAO.updateItemDescription(initialItem.getId(), newDescription);
        assertEquals(newDescription, ITEM_DAO.readItem(initialItem.getId()).getDescription());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 1000, -100, 0})
    void updateItemAmount(int newAmount) {
        ITEM_DAO.updateItemAmount(initialItem.getId(), newAmount);
        assertEquals(newAmount, ITEM_DAO.readItem(initialItem.getId()).getAmount());
    }

    @Test
    void updateItemAmountConcurrently() throws InterruptedException {
        int numThreads = 10;
        int initialAmount = initialItem.getAmount();
        int amountToAdd = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        var a = new ArrayList<String>();
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(() -> {
                ITEM_DAO.addItemAmount(initialItem.getId(), amountToAdd);
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(a);

        // Verify the final amount
        int expectedFinalAmount = initialAmount + (numThreads * amountToAdd);
        assertEquals(expectedFinalAmount, ITEM_DAO.readItem(initialItem.getId()).getAmount());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 100.0123, 1000.999999, -100, 0.000000001})
    void updateItemCost(double newCost) {
        ITEM_DAO.updateItemCost(initialItem.getId(), newCost);
        assertEquals(newCost, ITEM_DAO.readItem(initialItem.getId()).getCost());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "New name", ")($U@Y!$@!$DD"})
    void updateItemProducer(String newProducer) {
        ITEM_DAO.updateItemProducer(initialItem.getId(), newProducer);
        assertEquals(newProducer, ITEM_DAO.readItem(initialItem.getId()).getProducer());
    }

    @Test
    void updateItemGroup() {
        Group group = new Group(null, "GroupToUpdateItemGroupId", "");
        GROUP_DAO.createGroup(group);
        int groupId = GROUP_DAO.readGroupId(group.getName());

        assertNotEquals(groupId, ITEM_DAO.readItem(initialItem.getId()).getGroupId());
        ITEM_DAO.updateItemGroup(initialItem.getId(), groupId);
        assertEquals(groupId, ITEM_DAO.readItem(initialItem.getId()).getGroupId());
    }

    @Test
    void deleteItem() {
        Item item = ITEM_DAO.readItem(initialItem.getName());
        assertEquals(item, initialItem);

        ITEM_DAO.deleteItem(initialItem.getId());

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
        GROUP_DAO.createGroup(group);
        Item item = new Item(null, "test", "", 0, 0, "", GROUP_DAO.readGroupId(group.getName()));
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
