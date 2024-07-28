package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.Group;
import com.ukma.stockmanager.core.entities.Item;
import org.junit.jupiter.api.*;

class DaoTest {

    protected final Group initialGroup = new Group(
            null,
            "InitGroup",
            "InitDescription"
    );

    protected final Item initialItem = new Item(
            null,
            "InitNote",
            "InitDescription",
            100,
            100.001,
            "InitProducer",
            1
    );
    protected final Item existingItem = new Item(
            null,
            "ExistingNote",
            "ExistingDescription",
            100,
            100.001,
            "ExistingProducer",
            1
    );
    protected static GroupDAO GROUP_DAO;
    protected static ItemDAO ITEM_DAO;
    protected static String TEST_DATABASE_NAME = "TestDatabase1234567890";

    @BeforeAll
    static void createDatabase() {
        GROUP_DAO = new GroupDAO(TEST_DATABASE_NAME);
        ITEM_DAO = new ItemDAO(TEST_DATABASE_NAME);
    }

    @BeforeEach
    void createGroupAndItem() {
        GROUP_DAO.create(initialGroup);
        initialGroup.setId(GROUP_DAO.readGroupId(initialGroup.getName()));
        initialItem.setGroupId(initialGroup.getId());
        existingItem.setGroupId(initialGroup.getId());

        ITEM_DAO.createItem(initialItem);
        ITEM_DAO.createItem(existingItem);
    }

    @AfterEach
    void deleteGroupAndItem() {
        for (Group group : GROUP_DAO.readAll())
            GROUP_DAO.deleteGroup(group.getId());
        for (Item item : ITEM_DAO.readAllItems())
            ITEM_DAO.deleteItem(item.getId());
    }

    @AfterAll
    static void deleteDatabase() {
        GROUP_DAO.deleteDatabase();
    }
}