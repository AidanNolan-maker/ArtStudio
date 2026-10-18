package com.artstudio.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS artworks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    image_data BLOB NOT NULL,
                    width INTEGER NOT NULL CHECK (width > 0),
                    height INTEGER NOT NULL CHECK (height > 0),
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                )
                """;

        try (Connection connection = Database.getConnection();
            Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
