package com.ukma.stockmanager.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.stockmanager.core.entities.Group;
import com.ukma.stockmanager.core.entities.Item;
import com.ukma.stockmanager.core.entities.Message;
import com.ukma.stockmanager.core.utils.CommandAnalyser;
import com.ukma.stockmanager.core.utils.Constants;
import com.ukma.stockmanager.database.GroupDAO;
import com.ukma.stockmanager.database.ItemDAO;

import java.util.List;
import java.util.Map;

/**
 * Processor that can create response based on different types of commands.
 * Types are determined in CommandAnalyser
 */
public class Processor {
    private static final ItemDAO ITEM_DAO = new ItemDAO(Constants.DB_NAME);
    private static final GroupDAO GROUP_DAO = new GroupDAO(Constants.DB_NAME);
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
                    Item item = ITEM_DAO.readItem((int) map.get("id"));

                    response = OBJECT_MAPPER.writeValueAsBytes(item);
                    break;

                case CommandAnalyser.ITEM_GET_BY_GROUP:
                    boolean asc = map.get("asc") == null || (boolean) map.get("asc");
                    List<Item> itemsByGroup = ITEM_DAO.listItemsByGroupInOrder(
                            (int) map.get("id"),
                            asc
                    );

                    response = OBJECT_MAPPER.writeValueAsBytes(itemsByGroup);
                    break;

                case CommandAnalyser.ITEM_GET_ALL:
                    response = OBJECT_MAPPER.writeValueAsBytes(ITEM_DAO.readAllItems());
                    break;

                case CommandAnalyser.GROUP_GET:
                    Group group = GROUP_DAO.readGroup((int) map.get("id"));

                    response = OBJECT_MAPPER.writeValueAsBytes(group);
                    break;

                case CommandAnalyser.GROUP_GET_ALL:
                    response = OBJECT_MAPPER.writeValueAsBytes(GROUP_DAO.readAllGroups());
                    break;

                case CommandAnalyser.ITEM_REMOVE:
                    ITEM_DAO.deleteItem((int) map.get("id"));
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "OK_ITEM_REMOVE"));
                    break;

                case CommandAnalyser.GROUP_REMOVE:
                    GROUP_DAO.deleteGroup((int) map.get("id"));
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "OK_GROUP_REMOVE"));
                    break;

                case CommandAnalyser.ITEM_CREATE:
                    boolean itemCreated = ITEM_DAO.createItem(
                            (String) map.get("name"),
                            (String) map.get("description"),
                            (int) map.get("amount"),
                            (double) map.get("cost"),
                            (String) map.get("producer"),
                            (int) map.get("groupId")
                    );
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", itemCreated));
                    break;

                case CommandAnalyser.GROUP_CREATE:
                    boolean groupCreated = GROUP_DAO.createGroup((String) map.get("name"), (String) map.get("description"));
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", groupCreated));
                    break;

                case CommandAnalyser.ITEM_SET_NAME:
                    boolean itemNameChanged = ITEM_DAO.updateItemName((int) map.get("id"), (String) map.get("name"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", itemNameChanged));
                    break;

                case CommandAnalyser.ITEM_SET_DESCRIPTION:
                    ITEM_DAO.updateItemDescription((int) map.get("id"), (String) map.get("description"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_DESCRIPTION"));
                    break;

                case CommandAnalyser.ITEM_ADD_AMOUNT:
                    ITEM_DAO.addItemAmount((int) map.get("id"), (int) map.get("amount"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_ADD_AMOUNT"));
                    break;

                case CommandAnalyser.ITEM_SET_COST:
                    ITEM_DAO.updateItemCost((int) map.get("id"), (double) map.get("cost"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_COST"));
                    break;

                case CommandAnalyser.ITEM_SET_PRODUCER:
                    ITEM_DAO.updateItemProducer((int) map.get("id"), (String) map.get("producer"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_PRODUCER"));
                    break;

                case CommandAnalyser.ITEM_SET_GROUP:
                    ITEM_DAO.updateItemGroup((int) map.get("id"), (int) map.get("groupId"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_GROUP"));
                    break;

                case CommandAnalyser.GROUP_SET_NAME:
                    boolean groupNameChanged = GROUP_DAO.updateGroupName((int) map.get("id"), (String) map.get("name"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", groupNameChanged));
                    break;

                case CommandAnalyser.GROUP_SET_DESCRIPTION:
                    GROUP_DAO.updateGroupDescription((int) map.get("id"), (String) map.get("description"));

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
