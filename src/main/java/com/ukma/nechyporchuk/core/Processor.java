package com.ukma.nechyporchuk.core;

import com.ukma.nechyporchuk.core.CommandAnalyser;
import com.ukma.nechyporchuk.core.Message;


public class Processor {
    public Message process(Message message) {
        byte[] response;

        switch (message.getCType()) {
            case CommandAnalyser.ITEM_GET:
                response = "OK_GET".getBytes();
                break;
            case CommandAnalyser.ITEM_REMOVE:
                response = "OK_REMOVE".getBytes();
                break;
            case CommandAnalyser.ITEM_ADD:
                response = "OK_ADD".getBytes();
                break;
            case CommandAnalyser.ITEM_CREATE:
                response = "OK_CREATE_ITEM".getBytes();
                break;
            case CommandAnalyser.GROUP_CREATE:
                response = "OK_CREATE_GROUP".getBytes();
                break;
            case CommandAnalyser.ITEM_SET_PRICE:
                response = "OK_SET_PRICE".getBytes();
                break;
            default:
                response = "Wrong command".getBytes();
        }

        return new Message(message.getCType(), message.getBUserId(), response);
    }
}
