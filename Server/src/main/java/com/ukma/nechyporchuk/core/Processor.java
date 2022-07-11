package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.database.Database;
import com.ukma.nechyporchuk.utils.CommandAnalyser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Processor that can create response based on different types of commands.
 * Types are determined in CommandAnalyser
 */
public class Processor {

    private static final Database database = new Database("Shop database");

    public Message process(Message message) {
        byte[] response;
        JSONObject json = new JSONObject(new String(message.getMessage(), StandardCharsets.UTF_8));

        switch (message.getCType()) {
            case CommandAnalyser.ITEM_GET:
                JSONObject item = new JSONObject(
                        database.readItem(json.getString("name"))
                );

                response = item.toString().getBytes();
                break;
            case CommandAnalyser.ITEM_GET_BY_GROUP:
                boolean asc = json.opt("asc") == null || json.optBoolean("asc");
                JSONArray allItemsByGroup = new JSONArray(database.listItemsByGroupInOrder(
                        json.getInt("id"),
                        asc
                ));

                response = allItemsByGroup.toString().getBytes();
                break;
            case CommandAnalyser.ITEM_GET_ALL:
                JSONArray allItems = new JSONArray(database.readAllItems());

                response = allItems.toString().getBytes();
                break;
            case CommandAnalyser.GROUP_GET_ALL:
                JSONArray allGroups = new JSONArray(database.readAllGroups());

                response = allGroups.toString().getBytes();
                break;

            case CommandAnalyser.ITEM_REMOVE:
                database.deleteItem(json.getInt("id"));

                response = "OK_ITEM_REMOVE".getBytes();
                break;
            case CommandAnalyser.GROUP_REMOVE:
                database.deleteGroup(json.getInt("id"));

                response = "OK_GROUP_REMOVE".getBytes();
                break;
            case CommandAnalyser.ITEM_CREATE:
                database.createItem(
                        json.getString("name"),
                        json.getString("description"),
                        json.getInt("amount"),
                        json.getDouble("cost"),
                        json.getString("producer"),
                        json.getString("group")
                );

                response = "OK_CREATE_ITEM".getBytes();
                break;
            case CommandAnalyser.GROUP_CREATE:
                database.createGroup(
                        json.getString("name"),
                        json.getString("description")
                );

                response = "OK_CREATE_GROUP".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_NAME:
                database.updateItemName(json.getInt("id"), json.getString("name"));

                response = "ITEM_SET_NAME".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_DESCRIPTION:
                database.updateItemDescription(json.getInt("id"), json.getString("description"));

                response = "ITEM_SET_DESCRIPTION".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_AMOUNT:
                database.updateItemAmount(json.getInt("id"), json.getInt("amount"));

                response = "ITEM_SET_AMOUNT".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_COST:
                database.updateItemCost(json.getInt("id"), json.getDouble("cost"));

                response = "ITEM_SET_COST".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_PRODUCER:
                database.updateItemProducer(json.getInt("id"), json.getString("producer"));

                response = "ITEM_SET_PRODUCER".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_GROUP:
                database.updateItemGroup(json.getInt("id"), json.getString("group"));

                response = "ITEM_SET_GROUP".getBytes();
                break;
            case CommandAnalyser.GROUP_SET_NAME:
                database.updateGroupName(json.getInt("id"), json.getString("name"));

                response = "GROUP_SET_NAME".getBytes();
                break;
            case CommandAnalyser.GROUP_SET_DESCRIPTION:
                database.updateGroupName(json.getInt("id"), json.getString("description"));

                response = "GROUP_SET_DESCRIPTION".getBytes();
                break;
            default:
                response = "Wrong command".getBytes();
        }

        return new Message(message.getCType(), message.getBUserId(), response);
    }
}
