package com.ukma.nechyporchuk.core;

/**
 * Because commands are sent in packet as int value, it needs to be analyzed
 * Integer can represent different commands with different bytes.
 *<p>
 * At first, first and second bits represent GROUP and ITEM.
 * If first bit is set, command will be operating with GROUP
 *
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
            REMOVE = 8,
            ADD = 16,
            CREATE = 32,
            SET_PRICE = 64;

    public static final int
            ITEM_GET        = ITEM ^ GET,
            ITEM_REMOVE     = ITEM ^ REMOVE,
            ITEM_ADD        = ITEM ^ ADD,
            ITEM_CREATE     = ITEM ^ CREATE,
            ITEM_SET_PRICE  = ITEM ^ SET_PRICE;

    public static final int
            GROUP_CREATE = GROUP ^ CREATE;

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
