package com.ukma.nechyporchuk.utils;

/**
 * Because commands are sent in packet as int value, it needs to be analyzed
 * Integer can represent different commands with different bytes.
 * <p>
 * At first, first and second bits represent GROUP and ITEM.
 * If first bit is set, command will be operating with GROUP
 * <p>
 * Then next bits represent different command(GET, REMOVE, ADD, CREATE, etc.)
 *
 * @author Danylo Nechyporchuk
 */
public class CommandAnalyser {
    public static final int
            GROUP = 1,
            ITEM = 2;

    public static final int
            GET = 4,
            GET_ALL = 6,
            GET_BY_GROUP = 7,
            REMOVE = 8,
            CREATE = 32,
            SET_NAME = 40,
            SET_DESCRIPTION = 48,
            SET_AMOUNT = 56,
            SET_COST = 64,
            SET_PRODUCER = 72,
            SET_GROUP = 80;

    public static final int
            ITEM_GET = ITEM ^ GET,
            ITEM_GET_ALL = ITEM ^ GET_ALL,
            ITEM_GET_BY_GROUP = ITEM ^ GET_BY_GROUP,
            ITEM_REMOVE = ITEM ^ REMOVE,
            ITEM_CREATE = ITEM ^ CREATE,
            ITEM_SET_NAME = ITEM ^ SET_NAME,
            ITEM_SET_DESCRIPTION = ITEM ^ SET_DESCRIPTION,
            ITEM_SET_AMOUNT = ITEM ^ SET_AMOUNT,
            ITEM_SET_COST = ITEM ^ SET_COST,
            ITEM_SET_PRODUCER = ITEM ^ SET_PRODUCER,
            ITEM_SET_GROUP = ITEM ^ SET_GROUP;

    public static final int
            GROUP_CREATE = GROUP ^ CREATE,
            GROUP_GET_ALL = GROUP ^ GET_ALL,
            GROUP_REMOVE = GROUP ^ REMOVE,
            GROUP_SET_NAME = GROUP ^ SET_NAME,
            GROUP_SET_DESCRIPTION = GROUP ^ SET_DESCRIPTION;

    static boolean isItemCommand(int cType) {
        return (cType & ITEM) == ITEM;
    }

    static int getCommand(int cType, boolean isItemCommand) {
        return cType ^ (isItemCommand ? ITEM : GROUP);
    }

    static int getCommand(int cType) {
        return cType ^ (isItemCommand(cType) ? ITEM : GROUP);
    }
}
