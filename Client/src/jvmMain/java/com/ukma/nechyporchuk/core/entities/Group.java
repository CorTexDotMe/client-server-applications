package com.ukma.nechyporchuk.core.entities;

import com.arkivanov.essenty.parcelable.Parcelable;

import java.io.Serializable;
import java.util.Objects;

public class Group implements Parcelable {
    private int id;
    private String name;
    private String description;

    public Group() {
        this.id = 0;
        this.name = "";
        this.description = "";
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return id + ". " + name + '\n' +
               "    Description: " + description;
    }

    public String additionalInfo() {
        return "Description: " + description;
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
