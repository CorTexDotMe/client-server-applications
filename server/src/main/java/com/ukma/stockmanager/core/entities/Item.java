package com.ukma.stockmanager.core.entities;

import java.util.Objects;

public class Item {
    private Integer id;
    private String name;
    private String description;
    private int amount;
    private double cost;
    private String producer;
    private int groupId;

    public Item() {
        this.id = 0;
        this.name = "";
        this.description = "";
        this.amount = 0;
        this.cost = 0;
        this.producer = "";
        this.groupId = 0;
    }

    public Item(Integer id, String name, String description, int amount, double cost, String producer, int groupId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.cost = cost;
        this.producer = producer;
        this.groupId = groupId;
    }

    public Integer getId() {
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

    public int getGroupId() {
        return groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public String toString() {
        return id + ". " + name + '\n' +
               "\tDescription: " + description + '\n' +
               "\tAmount: " + amount + '\n' +
               "\tCost: " + cost + '\n' +
               "\tProducer: " + producer + '\n' +
               "\tGroupID: " + groupId;
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
        return Objects.hash(name, description, amount, cost, producer, groupId);
    }
}
