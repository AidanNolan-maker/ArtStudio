package com.artstudio.repository;

import com.artstudio.database.Database;
import com.artstudio.model.Artwork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArtworkRepository {
    public long save(
            String title,
            byte[] imageData,
            int width,
            int height) throws SQLException {
        String sql = """
                INSERT INTO artworks (
                    title, image_data, width, height,
                    created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        LocalDateTime now = LocalDateTime.now();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setBytes(2, imageData);
            statement.setInt(3, width);
            statement.setInt(4, height);
            statement.setString(5, now.toString());
            statement.setString(6, now.toString());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new SQLException("Could not retrieve the artwork ID.");
        }
    }

    public List<Artwork> findAll() throws SQLException {
        String sql = """
                SELECT id, title, image_data, width, height,
                    created_at, updated_at
                FROM artworks
                ORDER BY updated_at DESC, id DESC                   
                """;

        List<Artwork> artworks = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                artworks.add(mapArtwork(result));
            }
        }

        return artworks;
    }

    public Optional<Artwork> findById(long id) throws SQLException {
        String sql = """
                SELECT id, title, image_data, width, height,
                    created_at, updated_at
                FROM artworks
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapArtwork(result));
                }
            }
        }

        return Optional.empty();
    }

    private Artwork mapArtwork(ResultSet result) throws SQLException {
        return new Artwork(
                result.getLong("id"),
                result.getString("title"),
                result.getBytes("image_data"),
                result.getInt("width"),
                result.getInt("height"),
                LocalDateTime.parse(result.getString("created_at")),
                LocalDateTime.parse(result.getString("updated_at"))
        );
    }
}
