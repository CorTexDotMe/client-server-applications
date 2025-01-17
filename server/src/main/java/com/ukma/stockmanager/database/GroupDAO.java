package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.Group;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GroupDAO extends DAO {

    public GroupDAO(String databaseName) {
        super(databaseName);
    }

    public boolean createGroup(String name, String description) {
        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO groups(name, description) VALUES (?,?)"
            );
            statement.setString(1, name);
            statement.setString(2, description);

            int result = statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL INSERT statement");
            e.printStackTrace();
            return false;
        }
    }

    public boolean createGroup(Group group) {
        return createGroup(group.getName(), group.getDescription());
    }


    public List<Group> readAllGroups() {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM groups");
            ResultSet results = statement.executeQuery();

            LinkedList<Group> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(
                        new Group(
                                results.getInt("id"),
                                results.getString("name"),
                                results.getString("description")
                        )
                );
            }

            results.close();
            statement.close();
            return resultList;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return Collections.emptyList();
        }

    }

    public Group readGroup(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM groups WHERE name=(?)");
            statement.setString(1, name);

            return readGroup(statement);
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return null;
        }
    }

    public Group readGroup(int id) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM groups WHERE id=(?)");
            statement.setInt(1, id);

            return readGroup(statement);
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return null;
        }
    }

    public int readGroupId(String name) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT id FROM groups WHERE name=(?)");
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();
            int groupId = result.getInt("id");

            result.close();
            statement.close();
            return groupId;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return 0;
        }
    }

    public boolean updateGroupName(int id, String name) {
        return executeUpdateQuery(
                "UPDATE GROUPS SET name=(?) WHERE id=(?)",
                id,
                name
        );
    }

    public void updateGroupDescription(int id, String description) {
        executeUpdateQuery(
                "UPDATE GROUPS SET description=(?) WHERE id=(?)",
                id,
                description
        );
    }

    public void deleteGroup(int id) {
        try {
            PreparedStatement deleteGroupStatement = con.prepareStatement("DELETE FROM groups WHERE id=(?)");
            deleteGroupStatement.setInt(1, id);
            deleteGroupStatement.executeUpdate();
            deleteGroupStatement.close();

            PreparedStatement deletItemsStatement = con.prepareStatement("DELETE FROM items WHERE group_id=(?)");
            deletItemsStatement.setInt(1, id);
            deletItemsStatement.executeUpdate();
            deletItemsStatement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL DELETE statement");
            e.printStackTrace();
        }
    }

    private Group readGroup(PreparedStatement statement) throws SQLException {
        ResultSet results = statement.executeQuery();
        Group resultGroup = null;

        if (results.next()) {
            resultGroup = new Group(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("description")
            );
        }

        results.close();
        statement.close();
        return resultGroup;
    }
}
