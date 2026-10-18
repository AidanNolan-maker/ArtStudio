package com.artstudio.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String URL = "jdbc:sqlite:artstudio.db";

    private Database(){
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        try (var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_key = ON");
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }

        return connection;
    }
}
