package com.ukma.nechyporchuk.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.nechyporchuk.core.entities.Group;
import com.ukma.nechyporchuk.core.entities.Item;
import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.core.utils.CommandAnalyser;
import com.ukma.nechyporchuk.core.utils.Constants;
import com.ukma.nechyporchuk.database.Database;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Processor that can create response based on different types of commands.
 * Types are determined in CommandAnalyser
 */
public class Processor {

    private final static BlockingQueue<Pair> amountToAdd = new LinkedBlockingQueue<>();
    private static final Database database = new Database("Shop database");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static int bUserId = 1;
    private static byte bSrc = 1;
    private static boolean notStartedAddingThread = true;

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

                case CommandAnalyser.GROUP_GET:
                    Group group = database.readGroup((int) map.get("id"));

                    response = OBJECT_MAPPER.writeValueAsBytes(group);
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
                    boolean itemCreated = database.createItem(
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
                    boolean groupCreated = database.createGroup(
                            (String) map.get("name"),
                            (String) map.get("description")
                    );
                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", groupCreated));
                    break;

                case CommandAnalyser.ITEM_SET_NAME:
                    boolean itemNameChanged = database.updateItemName((int) map.get("id"), (String) map.get("name"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", itemNameChanged));
                    break;

                case CommandAnalyser.ITEM_SET_DESCRIPTION:
                    database.updateItemDescription((int) map.get("id"), (String) map.get("description"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_DESCRIPTION"));
                    break;

                case CommandAnalyser.ITEM_ADD_AMOUNT:
                    amountToAdd.put(new Pair(((int) map.get("id")), ((int) map.get("amount"))));
                    if (notStartedAddingThread) {
                        notStartedAddingThread = false;
                        startAddingThread();
                    }

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_ADD_AMOUNT"));
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
                    database.updateItemGroup((int) map.get("id"), (int) map.get("groupId"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", "ITEM_SET_GROUP"));
                    break;

                case CommandAnalyser.GROUP_SET_NAME:
                    boolean groupNameChanged = database.updateGroupName((int) map.get("id"), (String) map.get("name"));

                    response = OBJECT_MAPPER.writeValueAsBytes(Map.of("response", groupNameChanged));
                    break;

                case CommandAnalyser.GROUP_SET_DESCRIPTION:
                    database.updateGroupDescription((int) map.get("id"), (String) map.get("description"));

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

    private void startAddingThread() {
        new Thread(() -> {
            while (true) {
                Pair amountAndItemId = poll();
                Item item = database.readItem(amountAndItemId.id);
                int result = item.getAmount() + amountAndItemId.amount;

//                if (result < 0)
//                    result = 0;
                database.updateItemAmount(item.getId(), result);
            }
        }).start();
    }

    private Pair poll() {
        try {
            Pair result = null;
            while (result == null)
                result = amountToAdd.poll(Constants.POLL_TIMEOUT, TimeUnit.MILLISECONDS);
            return result;
        } catch (InterruptedException e) {
            return poll();
        }
    }

    private static class Pair {
        private int id;
        private int amount;

        public Pair(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
