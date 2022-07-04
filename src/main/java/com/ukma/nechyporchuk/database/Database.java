package com.ukma.nechyporchuk.database;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class Database {
    private Connection con;
    private final String databasePath = "src/main/resources/";
    private final String databaseName;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        initialization();
    }

    private void initialization() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + databasePath + databaseName);

            String createGroupQuery = "create table if not exists 'groups' (" +
                                      "'id' integer primary key AUTOINCREMENT, " +
                                      "'name' text, " +
                                      "'description' text" +
                                      ");";
            PreparedStatement groupTableStatement = con.prepareStatement(createGroupQuery);

            String createItemsQuery = "create table if not exists 'items' (" +
                                      "'id' integer primary key AUTOINCREMENT, " +
                                      "'name' text, " +
                                      "'description' text, " +
                                      "'amount' integer, " +
                                      "'cost' double, " +
                                      "'producer' text, " +
                                      "'groupID' integer" +
                                      ");";
            PreparedStatement itemTableStatement = con.prepareStatement(createItemsQuery);

            int resultFirst = groupTableStatement.executeUpdate();
            int resultSecond = itemTableStatement.executeUpdate();
        } catch (ClassNotFoundException e) {
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public void createGroup(String name, String description) {
        try {
            PreparedStatement statement = con.prepareStatement("insert into groups(name, description) values (?,?)");
            statement.setString(1, name);
            statement.setString(2, description);

            int result = statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void createItem(String name, String description, int amount, double cost, String producer, int groupID) {
        try {
            PreparedStatement statement = con.prepareStatement(
                    "insert into items(name, description, amount, cost, groupID, producer) values (?,?,?,?,?,?);"
            );
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, amount);
            statement.setDouble(4, cost);
            statement.setString(5, producer);
            statement.setInt(6, groupID);

            int result = statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public List<Group> readAllGroups() {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("select * from groups");

            LinkedList<Group> resultList = new LinkedList<>();
            while (res.next())
                resultList.add(
                        new Group(
                                res.getInt("id"),
                                res.getString("name"),
                                res.getString("description")
                        )
                );


            res.close();
            st.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public Group readGroup(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from groups where name=(?)");
            statement.setString(1, name);
            ResultSet results = statement.executeQuery();

            LinkedList<Group> resultList = new LinkedList<>();
            while (results.next())
                resultList.add(
                        new Group(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description")
                        )
                );

            results.close();
            statement.close();
            return resultList.element();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public Group readGroup(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from groups where id=(?)");
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            LinkedList<Group> resultList = new LinkedList<>();
            while (results.next())
                resultList.add(
                        new Group(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description")
                        )
                );

            results.close();
            statement.close();
            return resultList.element();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public int readGroupID(String name) {
        return readGroup(name).getId();
    }

    public List<Item> readAllItems() {
        try {
            Statement statement = con.createStatement();
            ResultSet results = statement.executeQuery("select * from items");

            LinkedList<Item> resultList = new LinkedList<>();
            while (results.next())
                resultList.add(
                        new Item(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description"),
                                results.getInt("amount"),
                                results.getDouble("cost"),
                                results.getString("producer"),
                                results.getInt("groupID")
                        )
                );

            results.close();
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public Item readItem(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from items where name=(?)");
            statement.setString(1, name);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = new LinkedList<>();
            while (results.next())
                resultList.add(
                        new Item(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description"),
                                results.getInt("amount"),
                                results.getDouble("cost"),
                                results.getString("producer"),
                                results.getInt("groupID")
                        )
                );

            results.close();
            statement.close();
            return resultList.element();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public int readItemID(String name) {
        return readItem(name).getId();
    }

    public void updateGroupName(int id, String name) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set name=(?) where id=(?)");
            statement.setString(1, name);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateGroupDescription(int id, String description) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set description=(?) where id=(?)");
            statement.setString(1, description);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateItemName(int id, String name) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set name=(?) where id=(?)");
            statement.setString(1, name);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateItemDescription(int id, String description) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set description=(?) where id=(?)");
            statement.setString(1, description);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateItemAmount(int id, int amount) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set amount=(?) where id=(?)");
            statement.setInt(1, amount);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateItemCost(int id, double cost) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set cost=(?) where id=(?)");
            statement.setDouble(1, cost);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateItemProducer(int id, String producer) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set producer=(?) where id=(?)");
            statement.setString(1, producer);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void updateItemGroup(int id, int group) {
        try {
            PreparedStatement statement = con.prepareStatement("update groups set groupID=(?) where id=(?)");
            statement.setInt(1, group);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void deleteGroup(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("delete from groups where id=(?)");
            statement.setInt(1, id);
            statement.executeUpdate();

            for (Item item : listItemsByGroup(id))
                deleteItem(item.getId());


            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public void deleteItem(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("delete from items where id=(?)");
            statement.setInt(1, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public List<Item> listItemsByGroup(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from items where id=(?)");
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = new LinkedList<>();
            while (results.next())
                resultList.add(
                        new Item(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description"),
                                results.getInt("amount"),
                                results.getDouble("cost"),
                                results.getString("producer"),
                                results.getInt("groupID")
                        )
                );

            results.close();
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public List<Item> listItemsByProducer(String producer) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from items where producer=(?)");
            statement.setString(1, producer);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = new LinkedList<>();
            while (results.next())
                resultList.add(
                        new Item(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description"),
                                results.getInt("amount"),
                                results.getDouble("cost"),
                                results.getString("producer"),
                                results.getInt("groupID")
                        )
                );

            results.close();
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public void deleteDatabase() {
        try {
//            PreparedStatement statement = con.prepareStatement("drop database ShopDB");
//            statement.setString(1, databaseName);

            Statement statement = con.createStatement();
            statement.executeUpdate("DROP DATABASE " + databasePath + databaseName);

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Database sqlTest = new Database("ShopDB");
//        sqlTest.insertTestData("SuperMAKAKA");
//        sqlTest.insertTestData("NewMAKAKA");
//        sqlTest.createGroup("Крупи");

//        sqlTest.createGroup("Food", "You can eat it!");
//        sqlTest.createItem("Grechka", "healthy i guess", 0, 40.0, "Kyiv-something", sqlTest.readGroup("Food").getId());


        for (Group group : sqlTest.readAllGroups())
            System.out.println(group);

        System.out.println();

        for (Item item : sqlTest.readAllItems())
            System.out.println(item);

        sqlTest.deleteDatabase();
        sqlTest.readAllGroups();

    }


}
