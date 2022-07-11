package com.ukma.nechyporchuk.processor;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.nechyporchuk.core.entities.Item;
import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.core.utils.CommandAnalyser;
import com.ukma.nechyporchuk.database.Database;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Processor that can create response based on different types of commands.
 * Types are determined in CommandAnalyser
 */
public class Processor {

    private static final Database database = new Database("Shop database");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static int bUserId = 1;
    private static byte bSrc = 1;

    public Message process(Message message) {
        byte[] response;
        try {
            Map<String, Object> map = OBJECT_MAPPER.readValue(message.getMessage(), new TypeReference<>() {
            });

            switch (message.getCType()) {
                case CommandAnalyser.ITEM_GET:
                    Item item = database.readItem((int) map.get("id"));

                    response = OBJECT_MAPPER.writeValueAsBytes(item);
                    break;

                case CommandAnalyser.ITEM_GET_BY_GROUP:
                    boolean asc = map.get("asc") == null || (boolean) map.get("asc");
                    List<Item> itemsByGroup = database.listItemsByGroupInOrder(
                            (int) map.get("id"),
                            asc
                    );

                    response = OBJECT_MAPPER.writeValueAsBytes(itemsByGroup);
                    break;

                case CommandAnalyser.ITEM_GET_ALL:
                    response = OBJECT_MAPPER.writeValueAsBytes(database.readAllItems());
                    break;

                case CommandAnalyser.GROUP_GET_ALL:
                    response = OBJECT_MAPPER.writeValueAsBytes(database.readAllGroups());
                    break;

                case CommandAnalyser.ITEM_REMOVE:
                    database.deleteItem((int) map.get("id"));
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "OK_ITEM_REMOVE"));
                    break;

                case CommandAnalyser.GROUP_REMOVE:
                    database.deleteGroup((int) map.get("id"));
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "OK_GROUP_REMOVE"));
                    break;

                case CommandAnalyser.ITEM_CREATE:
                    database.createItem(
                            (String) map.get("name"),
                            (String) map.get("description"),
                            (int) map.get("amount"),
                            (double) map.get("cost"),
                            (String) map.get("producer"),
                            (int) map.get("group")
                    );
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "OK_CREATE_ITEM"));
                    break;

                case CommandAnalyser.GROUP_CREATE:
                    database.createGroup(
                            (String) map.get("name"),
                            (String) map.get("description")
                    );
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "OK_CREATE_GROUP"));
                    break;

                case CommandAnalyser.ITEM_SET_NAME:
                    database.updateItemName((int) map.get("id"), (String) map.get("name"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_NAME"));
                    break;

                case CommandAnalyser.ITEM_SET_DESCRIPTION:
                    database.updateItemDescription((int) map.get("id"), (String) map.get("description"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_DESCRIPTION"));
                    break;

                case CommandAnalyser.ITEM_SET_AMOUNT:
                    database.updateItemAmount((int) map.get("id"), (int) map.get("amount"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_AMOUNT"));
                    break;

                case CommandAnalyser.ITEM_SET_COST:
                    database.updateItemCost((int) map.get("id"), (double) map.get("cost"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_COST"));
                    break;

                case CommandAnalyser.ITEM_SET_PRODUCER:
                    database.updateItemProducer((int) map.get("id"), (String) map.get("producer"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_PRODUCER"));
                    break;

                case CommandAnalyser.ITEM_SET_GROUP:
                    database.updateItemGroup((int) map.get("id"), (String) map.get("group"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_GROUP"));
                    break;

                case CommandAnalyser.GROUP_SET_NAME:
                    database.updateGroupName((int) map.get("id"), (String) map.get("name"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "GROUP_SET_NAME"));
                    break;

                case CommandAnalyser.GROUP_SET_DESCRIPTION:
                    database.updateGroupName((int) map.get("id"), (String) map.get("description"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "GROUP_SET_DESCRIPTION"));
                    break;

                case CommandAnalyser.INITIAL_PACKET:
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of(
                            "bSrc", bSrc,
                            "bUserId", bUserId
                    ));
                    bSrc = (byte) (bSrc + 1);
                    bUserId++;
                    break;

                default:
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "Wrong command"));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = new byte[0];
        }
        return new Message(message.getCType(), message.getBUserId(), response);
    }
}
