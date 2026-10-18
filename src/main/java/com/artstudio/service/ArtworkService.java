package com.artstudio.service;

import com.artstudio.model.Artwork;
import com.artstudio.repository.ArtworkRepository;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArtworkService {
    private final ArtworkRepository repository;

    public long saveArtwork(
            String title,
            WritableImage snapshot) throws SQLException, IOException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Artwork title cannot be empty."
            );
        }

        if (snapshot == null
                || snapshot.getWidth() <= 0
                || snapshot.getHeight() <= 0) {
            throw new IllegalArgumentException(
                    "A valid canvas snapshot is required."
            );
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        boolean written = ImageIO.write(
                SwingFXUtils.fromFXImage(snapshot, null),
                "png",
                output
        );

        if (!written) {
            throw new  IOException("Could not encode the artwork as PNG.");
        }

        return repository.save(
                title.trim(),
                output.toByteArray(),
                (int) snapshot.getWidth(),
                (int) snapshot.getHeight()
        );
    }

    public List<Artwork> getAllArtworks() throws SQLException {
        return repository.findAll();
    }

    public Optional<Artwork> getArtwork(long id) throws SQLException {
        return repository.findById(id);
    }

    public Image decodeImage(Artwork artwork) throws IOException {
        try (ByteArrayInputStream input =
                new ByteArrayInputStream(artwork.getImageData())) {
            Image image = new Image(input);

            if (image.isError() || image.getWidth() <= 0
                || image.getHeight() <= 0) {
                throw new IOException("The saved artwork image is invalid.");
            }

            return image;
        }
    }
}
