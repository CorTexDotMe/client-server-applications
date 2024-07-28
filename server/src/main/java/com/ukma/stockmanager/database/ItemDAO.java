package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ItemDAO extends DAO {

    public ItemDAO(String databaseName) {
        super(databaseName);
    }

    public boolean createItem(String name, String description, int amount, double cost, String producer, int groupId) {
        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO items(name, description, amount, cost, producer, group_id) VALUES (?,?,?,?,?,?);"
            );
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, amount);
            statement.setDouble(4, cost);
            statement.setString(5, producer);
            statement.setInt(6, groupId);

            int result = statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL INSERT statement");
            e.printStackTrace();
            return false;
        }
    }

    public boolean createItem(Item item) {
        return createItem(item.getName(), item.getDescription(), item.getAmount(),
                item.getCost(), item.getProducer(), item.getGroupId());
    }

    public List<Item> readAllItems() {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM items");
            ResultSet results = statement.executeQuery();

            List<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public Item readItem(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM items WHERE name=(?)");
            statement.setString(1, name);

            return readItem(statement);
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return null;
        }
    }

    public Item readItem(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM items WHERE id=(?)");
            statement.setInt(1, id);

            return readItem(statement);
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return null;
        }
    }

    public Integer readItemId(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM items WHERE name=(?)");
            statement.setString(1, name);

            Integer itemId = null;
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                itemId = result.getInt("id");
            }

            result.close();
            statement.close();
            return itemId;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return 0;
        }
    }

    public boolean updateItemName(int id, String name) {
        return executeUpdateQuery(
                "UPDATE items SET name=(?) WHERE id=(?)",
                id,
                name
        );
    }

    public void updateItemDescription(int id, String description) {
        executeUpdateQuery(
                "UPDATE items SET description=(?) WHERE id=(?)",
                id,
                description
        );
    }

    public void updateItemAmount(int id, int amount) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE items SET amount=(?) WHERE id=(?)");
            statement.setInt(1, amount);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL UPDATE statement");
            e.printStackTrace();
        }
    }

    public void addItemAmount(int id, int amount) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE items SET amount=amount+(?) WHERE id=(?)");
            statement.setInt(1, amount);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL UPDATE statement");
            e.printStackTrace();
        }
    }

    public void updateItemCost(int id, double cost) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE items SET cost=(?) WHERE id=(?)");
            statement.setDouble(1, cost);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL UPDATE statement");
            e.printStackTrace();
        }
    }

    public void updateItemProducer(int id, String producer) {
        executeUpdateQuery(
                "UPDATE items SET producer=(?) WHERE id=(?)",
                id,
                producer
        );
    }

    public void updateItemGroup(int id, int groupId) {
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE items SET group_id=(?) WHERE id=(?)");

            statement.setInt(1, groupId);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL UPDATE statement");
            e.printStackTrace();
        }
    }

    public boolean deleteItem(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE id=(?)");
            statement.setInt(1, id);

            statement.executeUpdate();

            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL DELETE statement");
            e.printStackTrace();
            return false;
        }
    }


    public List<Item> listItemsByGroup(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM items WHERE group_id=(?)");
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public List<Item> listItemsByGroupInOrder(int id, boolean ascending) {
        try {
            String query = "SELECT * FROM items WHERE group_id=(?) ORDER BY name ";
            if (ascending)
                query += "ASC";
            else
                query += "DESC";

            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public List<Item> listItemsByNameInOrder(boolean ascending) {
        String query = "SELECT * FROM items ORDER BY name ";
        if (ascending)
            return listWithOrder(query + "ASC");
        else
            return listWithOrder(query + "DESC");
    }

    private LinkedList<Item> getItems(ResultSet results) throws SQLException {
        LinkedList<Item> resultList = new LinkedList<>();
        while (results.next()) {
            resultList.add(
                    new Item(
                            results.getInt("id"),
                            results.getString("name"),
                            results.getString("description"),
                            results.getInt("amount"),
                            results.getDouble("cost"),
                            results.getString("producer"),
                            results.getInt("group_id")
                    )
            );
        }
        results.close();
        return resultList;
    }

    private Item readItem(PreparedStatement statement) throws SQLException {
        ResultSet results = statement.executeQuery();
        Item resultItem = null;

        if (results.next()) {
            resultItem = new Item(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("description"),
                    results.getInt("amount"),
                    results.getDouble("cost"),
                    results.getString("producer"),
                    results.getInt("group_id")
            );
        }


        results.close();
        statement.close();
        return resultItem;
    }

    private List<Item> listWithOrder(String query) {
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }
}
