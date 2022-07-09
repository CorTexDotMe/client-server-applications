package com.ukma.nechyporchuk.network.implementation.tcp;

import com.ukma.nechyporchuk.utils.CommandAnalyser;
import com.ukma.nechyporchuk.core.entities.Message;
import com.ukma.nechyporchuk.core.entities.Packet;
import com.ukma.nechyporchuk.database.Group;
import com.ukma.nechyporchuk.database.Item;
import com.ukma.nechyporchuk.utils.Constants;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RealTCPTest {

    private static StoreServerTCP server;

    @BeforeAll
    static void setUp() {
        new Thread(() -> {
            server = new StoreServerTCP();
            server.start(Constants.TCP_PORT);
        }).start();
    }

    @AfterAll
    static void tearDown() {
        server.stopServer();
    }

    @Test
    void createAndGetItem() throws IOException {
        int packetId = 0;
        JSONObject json;

        Group group = new Group(0, "Food", "You can eat it");
        json = new JSONObject(group);
        Packet createGroupPacket = new Packet(
                (byte) 0,
                packetId,
                new Message(CommandAnalyser.GROUP_CREATE, 0, json.toString().getBytes())
        );

        Item item = new Item(0, "Ice cream", "tasty", 100, 2.25, "Morzho", 0);
        json = new JSONObject(item);
        json.put("group", group.getName());
        Packet createItemPacket = new Packet(
                (byte) 0,
                packetId,
                new Message(CommandAnalyser.ITEM_CREATE, 0, json.toString().getBytes())
        );


        StoreClientTCP client = new StoreClientTCP();
        client.startConnection("127.0.0.1", Constants.TCP_PORT);

//      Create group
        client.sendMessage(createGroupPacket.getBytes());
        System.out.println(client.receiveMessage()); // group created ok

//      Create Item
        client.sendMessage(createItemPacket.getBytes());
        System.out.println(client.receiveMessage()); // item create ok

//      Get item
        Packet getItemPacket = new Packet(
                (byte) 0,
                packetId,
                new Message(CommandAnalyser.ITEM_GET, 0, json.toString().getBytes())
        );
        client.sendMessage(getItemPacket.getBytes());
        Packet packet = new Packet(client.receiveMessageBytes());

        JSONObject responseItem = new JSONObject(new String(packet.getBMsg().getMessage(), StandardCharsets.UTF_8));

        Item receivedItem = new Item(
                responseItem.getInt("id"),
                responseItem.getString("name"),
                responseItem.getString("description"),
                responseItem.getInt("amount"),
                responseItem.getDouble("cost"),
                responseItem.getString("producer"),
                responseItem.getInt("groupId")
        );
        item.setGroupId(receivedItem.getGroupId());


        client.sendEndMessage();
        assertEquals(item, receivedItem);
    }
}
