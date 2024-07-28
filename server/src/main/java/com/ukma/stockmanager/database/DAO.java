package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.Group;
import com.ukma.stockmanager.core.entities.Item;
import com.ukma.stockmanager.core.entities.User;
import com.ukma.stockmanager.core.utils.Constants;
import org.sqlite.SQLiteException;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DAO {
    protected Connection con;
    private static final HashMap<String, Connection> connectionContainer = new HashMap<>();
    private final String databaseName;

    public DAO(String databaseName) {
        this.databaseName = databaseName;
        initialization();
    }

    private void initialization() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (!connectionContainer.containsKey(databaseName)) {
                connectionContainer.put(
                        databaseName,
                        DriverManager.getConnection("jdbc:sqlite:" + Constants.DB_PATH + databaseName)
                );
            }
            con = connectionContainer.get(databaseName);

            String createGroupsQuery = "CREATE TABLE IF NOT EXISTS 'groups' (" +
                    "'id' INTEGER PRIMARY KEY, " +
                    "'name' TEXT UNIQUE, " +
                    "'description' TEXT" +
                    ");";
            PreparedStatement groupsTableStatement = con.prepareStatement(createGroupsQuery);

            String createItemsQuery = "CREATE TABLE IF NOT EXISTS 'items' (" +
                    "'id' INTEGER PRIMARY KEY, " +
                    "'name' TEXT UNIQUE, " +
                    "'description' TEXT, " +
                    "'amount' INTEGER, " +
                    "'cost' DOUBLE, " +
                    "'producer' TEXT, " +
                    "'group_id' INTEGER" +
                    ");";
            PreparedStatement itemsTableStatement = con.prepareStatement(createItemsQuery);

            String createUsersQuery = "CREATE TABLE IF NOT EXISTS 'users' (" +
                    "'id' INTEGER PRIMARY KEY, " +
                    "'login' TEXT UNIQUE, " +
                    "'password' TEXT" +
                    ");";
            PreparedStatement usersTableStatement = con.prepareStatement(createUsersQuery);

            int resultGroups = groupsTableStatement.executeUpdate();
            int resultItems = itemsTableStatement.executeUpdate();
            int resultUsers = usersTableStatement.executeUpdate();
            groupsTableStatement.close();
            itemsTableStatement.close();
            usersTableStatement.close();
        } catch (ClassNotFoundException e) {
            System.out.println("No JDBC drivers");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Incorrect SQL CREATE TABLE statement");
            e.printStackTrace();
        }
    }

    public void deleteDatabase() {
        try {
            File database = new File(Constants.DB_PATH + databaseName);
            con.close();
            connectionContainer.remove(databaseName);
            database.delete();
        } catch (SQLException e) {
            System.out.println("Error while closing database connection");
            e.printStackTrace();
        }
    }

    protected boolean executeUpdateQuery(String query, int id, String updatedValue) {
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, updatedValue);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
            return true;
        } catch (SQLiteException e) {
            return false;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL UPDATE statement");
            e.printStackTrace();
            return false;
        }
    }
}
