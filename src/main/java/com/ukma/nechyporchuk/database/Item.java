package com.ukma.nechyporchuk.database;

import java.util.Objects;

public class Item {
    private int id;
    private String name;
    private String description;
    private int amount;
    private double cost;
    private String producer;
    private int groupID;

    public Item(int id, String name, String description, int amount, double cost, String producer, int groupID) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.cost = cost;
        this.producer = producer;
        this.groupID = groupID;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public double getCost() {
        return cost;
    }

    public String getProducer() {
        return producer;
    }

    public int getGroupID() {
        return groupID;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return id + ". " + name + '\n' +
               "\tDescription: " + description + '\n' +
               "\tAmount: " + amount + '\n' +
               "\tCost: " + cost + '\n' +
               "\tProducer: " + producer + '\n' +
               "\tGroupID: " + groupID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return amount == item.amount && Double.compare(item.cost, cost) == 0 && name.equals(item.name) && description.equals(item.description) && producer.equals(item.producer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, amount, cost, producer, groupID);
    }
}
