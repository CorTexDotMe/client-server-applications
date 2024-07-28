package com.ukma.stockmanager.database;

import com.ukma.stockmanager.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends DAO{

    public UserDAO(String databaseName) {
        super(databaseName);
    }

    public void createUser(String login, String password) {
        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO users(login, password) VALUES (?,?)");
            statement.setString(1, login);
            statement.setString(2, password);

            int result = statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL INSERT statement");
            e.printStackTrace();
        }
    }

    public User readUser(String login) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE login=(?)");
            statement.setString(1, login);

            ResultSet results = statement.executeQuery();

            User resultUser = new User(
                    results.getInt("id"),
                    results.getString("login"),
                    results.getString("password")
            );

            results.close();
            statement.close();
            return resultUser;
        } catch (SQLException e) {
            System.out.println("Incorrect SQL SELECT statement");
            e.printStackTrace();
            return null;
        }
    }

    public void deleteUser(String login) {
        try {
            PreparedStatement statement = con.prepareStatement("DELETE FROM users WHERE login=(?)");
            statement.setString(1, login);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            System.out.println("Incorrect SQL DELETE statement");
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

}
