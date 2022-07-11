package com.ukma.nechyporchuk.entities;

import java.util.Objects;

public class Group{
    private int id;
    private String name;
    private String description;

    public Group(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return id + ". " + name + '\n' +
               "\tDescription: " + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return name.equals(group.name) && description.equals(group.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
