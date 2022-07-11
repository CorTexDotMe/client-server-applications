package com.ukma.nechyporchuk.database;

import com.ukma.nechyporchuk.core.entities.Group;
import com.ukma.nechyporchuk.core.entities.Item;
import com.ukma.nechyporchuk.core.entities.User;
import org.sqlite.SQLiteException;

import java.io.File;
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

            String createGroupsQuery = "create table if not exists 'groups' (" +
                                       "'id' integer primary key AUTOINCREMENT, " +
                                       "'name' text unique, " +
                                       "'description' text" +
                                       ");";
            PreparedStatement groupsTableStatement = con.prepareStatement(createGroupsQuery);

            String createItemsQuery = "create table if not exists 'items' (" +
                                      "'id' integer primary key AUTOINCREMENT, " +
                                      "'name' text unique, " +
                                      "'description' text, " +
                                      "'amount' integer, " +
                                      "'cost' double, " +
                                      "'producer' text, " +
                                      "'groupId' integer" +
                                      ");";
            PreparedStatement itemsTableStatement = con.prepareStatement(createItemsQuery);

            String createUsersQuery = "create table if not exists 'users' (" +
                                      "'id' integer primary key AUTOINCREMENT, " +
                                      "'login' text unique, " +
                                      "'password' text" +
                                      ");";
            PreparedStatement usersTableStatement = con.prepareStatement(createUsersQuery);

            int resultGroups = groupsTableStatement.executeUpdate();
            int resultItems = itemsTableStatement.executeUpdate();
            int resultUsers = usersTableStatement.executeUpdate();
        } catch (ClassNotFoundException e) {
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public void createUser(String login, String password) {
        try {
            PreparedStatement statement = con.prepareStatement("insert into users(login, password) values (?,?)");
            statement.setString(1, login);
            statement.setString(2, password);

            int result = statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public User readUser(String login) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from users where login=(?)");
            statement.setString(1, login);

            ResultSet results = statement.executeQuery();

            User resultUser = new User(
                    results.getString("login"),
                    results.getString("password")
            );

            results.close();
            statement.close();
            return resultUser;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public void deleteUser(String login) {
        try {
            PreparedStatement statement = con.prepareStatement("delete from users where login=(?)");
            statement.setString(1, login);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    public boolean containsUser(String login, String password) {
        User user = readUser(login);
        return user != null && user.getPassword().equals(password);
    }

    public boolean containsLogin(String login) {
        return readUser(login) != null;
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

    public boolean createItem(String name, String description, int amount, double cost, String producer, int groupId) {
        try {
            PreparedStatement statement = con.prepareStatement(
                    "insert into items(name, description, amount, cost, producer, groupId) values (?,?,?,?,?,?);"
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
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return false;
        }
    }

    public boolean createItem(Item item) {
        return createItem(
                item.getName(),
                item.getDescription(),
                item.getAmount(),
                item.getCost(),
                item.getProducer(),
                item.getGroupId()
        );
    }

    public boolean createItem(String name, String description, int amount, double cost, String producer, String group) {
        return createItem(name, description, amount, cost, producer, readGroupId(group));
    }

    public List<Group> readAllGroups() {
        String query = "select * from groups";
        return getGroups(query);
    }

    public Group readGroup(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from groups where name=(?)");
            statement.setString(1, name);

            return readGroup(statement);
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

            return readGroup(statement);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public int readGroupId(String name) {
        Group group = readGroup(name);
        return group == null ? -1 : group.getId();
    }

    public String readGroupDescription(String name) {
        Group group = readGroup(name);
        return group == null ? "" : group.getDescription();
    }

    public String readGroupName(int id) {
        Group group = readGroup(id);
        return group == null ? "" : group.getName();
    }

    public String readGroupDescription(int id) {
        Group group = readGroup(id);
        return group == null ? "" : group.getDescription();
    }

    public List<Item> readAllItems() {
        try {
            Statement statement = con.createStatement();
            ResultSet results = statement.executeQuery("select * from items");

            return getItems(results);
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

            return readItem(statement);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public Item readItem(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from items where id=(?)");
            statement.setInt(1, id);

            return readItem(statement);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return null;
        }
    }

    public int readItemId(String name) {
        return readItem(name).getId();
    }

    public String readItemName(int id) {
        return readItem(id).getName();
    }

    public void updateGroupName(int id, String name) {
        updateString(
                "update groups set name=(?) where id=(?)",
                id,
                name
        );
    }

    public void updateGroupDescription(int id, String description) {
        updateString(
                "update groups set description=(?) where id=(?)",
                id,
                description
        );
    }

    public void updateItemName(int id, String name) {
        updateString(
                "update items set name=(?) where id=(?)",
                id,
                name
        );
    }

    public void updateItemDescription(int id, String description) {
        updateString(
                "update items set description=(?) where id=(?)",
                id,
                description
        );
    }

    public void updateItemAmount(int id, int amount) {
        try {
            PreparedStatement statement = con.prepareStatement("update items set amount=(?) where id=(?)");
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
            PreparedStatement statement = con.prepareStatement("update items set cost=(?) where id=(?)");
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
        updateString(
                "update items set producer=(?) where id=(?)",
                id,
                producer
        );
    }

    public void updateItemGroup(int id, String group) {
        try {
            PreparedStatement statement = con.prepareStatement("update items set groupId=(?) where id=(?)");

            int groupId = readGroupId(group);
            if (groupId == -1)
                throw new IllegalArgumentException("No such group with name: " + group);

            statement.setInt(1, groupId);
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

    public boolean deleteItem(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("delete from items where id=(?)");
            statement.setInt(1, id);

            statement.executeUpdate();

            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return false;
        }
    }

    public List<Item> listItemsByGroup(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("select * from items where groupId=(?)");
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public List<Item> listItemsByGroupInOrder(int id, boolean ascending) {
        try {
            String query = "select * from items where groupId=(?) ORDER BY name ";
            if (ascending)
                query += "asc";
            else
                query += "desc";

            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public List<Item> listItemsByNameInOrder(boolean ascending) {
        String query = "select * from items ORDER BY name ";
        if (ascending)
            return listWithOrder(query + "asc");
        else
            return listWithOrder(query + "desc");
    }

    public void deleteDatabase() {
        try {
            File database = new File(databasePath + databaseName);
            con.close();
            database.delete();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
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
                            results.getInt("groupId")
                    )
            );
        }
        results.close();
        return resultList;
    }

    private LinkedList<Group> getGroups(String query) {
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery(query);

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

    private Group readGroup(PreparedStatement statement) throws SQLException {
        ResultSet results = statement.executeQuery();

        Group resultGroup = new Group(
                results.getInt("id"),
                results.getString("name"),
                results.getString("description")
        );

        results.close();
        statement.close();
        return resultGroup;
    }

    private Item readItem(PreparedStatement statement) throws SQLException {
        ResultSet results = statement.executeQuery();

        Item resultItem = new Item(
                results.getInt("id"),
                results.getString("name"),
                results.getString("description"),
                results.getInt("amount"),
                results.getDouble("cost"),
                results.getString("producer"),
                results.getInt("groupId")
        );


        results.close();
        statement.close();
        return resultItem;
    }

    private void updateString(String query, int id, String string) {
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, string);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
        } catch (SQLiteException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    private List<Item> listWithOrder(String query) {
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            LinkedList<Item> resultList = getItems(results);
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public static void main(String[] args) {
//        Database sqlTest = new Database("ShopDB");
        Database sqlTest = new Database("Shop database");
//        sqlTest.insertTestData("SuperMAKAKA");
//        sqlTest.insertTestData("NewMAKAKA");
//        sqlTest.createGroup("Крупи");

//        sqlTest.createGroup("Not food", "You cannnot eat it!");
//        sqlTest.createGroup("Food", "You can eat it!");
//        sqlTest.createItem("Grechkaa", "healthy i guess", 0, 40.0, "Kyiv-something", sqlTest.readGroup("Food").getId());

//        sqlTest.createUser("1WtpmDDne6U4VWecsdJS2g==", "X03MO1qnZdYdgyfeuILPmQ==");

//        sqlTest.createItem("Ice cream", "obviously tasty", 100, 2.25, "Morzho", sqlTest.readGroup("Food").getId());


        for (Group group : sqlTest.readAllGroups())
            System.out.println(group);

        System.out.println();

        for (Item item : sqlTest.readAllItems())
            System.out.println(item);
    }


}
