package com.artstudio;

import com.artstudio.database.DatabaseInitializer;
import com.artstudio.model.Artwork;
import com.artstudio.repository.ArtworkRepository;
import com.artstudio.service.ArtworkService;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

public class Main extends Application {
    private final ArtworkService artworkService =
            new ArtworkService(new ArtworkRepository());

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseInitializer.initialize();

        Canvas canvas = new Canvas(1000, 650);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        clearCanvas(graphics, canvas);

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);

        Slider brushSize = new Slider(1, 40, 5);
        brushSize.setShowTickLabels(true);
        brushSize.setShowTickMarks(true);
        brushSize.setMajorTickUnit(10);
        brushSize.setPrefWidth(150);

        Label sizeLabel = new Label("Brush: 5 px");

        brushSize.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        sizeLabel.setText(
                                "Brush: " + newValue.intValue() + " px"
                        )
        );

        Button clearButton = new Button("Clear Canvas");
        clearButton.setOnAction(
                event -> clearCanvas(graphics, canvas)
        );

        Button saveButton = new Button("Save Artwork");
        saveButton.setOnAction(
                event -> saveArtwork(stage, canvas)
        );

        Button galleryButton = new Button("My Gallery");
        galleryButton.setOnAction(
                event -> openGallery(stage, graphics, canvas)
        );

        HBox toolbar = new HBox(
                12,
                new Label("Color:"),
                colorPicker,
                sizeLabel,
                brushSize,
                clearButton,
                saveButton,
                galleryButton
        );
        toolbar.setPadding(new Insets(12));

        canvas.setOnMousePressed(event -> {
            graphics.setFill(colorPicker.getValue());
            double size = brushSize.getValue();

            graphics.fillOval(
                    event.getX() - size / 2,
                    event.getY() - size / 2,
                    size,
                    size
            );
        });

        canvas.setOnMouseDragged(event -> {
            graphics.setFill(colorPicker.getValue());
            double size = brushSize.getValue();

            graphics.fillOval(
                    event.getX() - size / 2,
                    event.getY() - size / 2,
                    size,
                    size
            );
        });

        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setStyle("-fx-background-color: #e5e7eb;");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvasContainer);

        Scene scene = new Scene(root, 1250, 750);

        stage.setTitle("ArtStudio");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.show();
    }

    private void clearCanvas(
            GraphicsContext graphics,
            Canvas canvas) {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(
                0, 0,  canvas.getWidth(), canvas.getHeight()
        );
    }

    private void saveArtwork(Stage stage, Canvas canvas) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Artwork");
        dialog.setHeaderText("Save your drawing");
        dialog.setContentText("Artwork title:");

        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        String title = result.get().trim();

        if (title.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Title",
                    "Please enter a title for your artwork."
            );
            return;
        }

        try {
            long id = artworkService.saveArtwork(
                    title,
                    canvas.snapshot(null, null)
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Artwork Saved",
                    "Your artwork was saved successfully.\nArtwork ID: " + id
            );
        } catch (SQLException | IOException | IllegalArgumentException ex) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Save Failed",
                    "Could not save your artwork:\n" + ex.getMessage()
            );
        }
    }

    private void openGallery(
            Stage stage,
            GraphicsContext graphics,
            Canvas canvas) {
        try {
            List<Artwork> artworks = artworkService.getAllArtworks();

            if (artworks.isEmpty()) {
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "My Gallery",
                        "You have not saved any artwork yet."
                );
                return;
            }

            ChoiceDialog<Artwork> dialog =
                    new ChoiceDialog<>(artworks.get(0), artworks);

            dialog.initOwner(stage);
            dialog.setTitle("My Gallery");
            dialog.setHeaderText("Choose an artwork to open");
            dialog.setContentText("Saved artwork:");

            dialog.showAndWait().ifPresent(artwork ->
                    loadArtwork(artwork, graphics, canvas)
            );
        } catch (SQLException ex) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Gallery Error",
                    "Could not load your gallery:\n" + ex.getMessage()
            );
        }
    }

    private void loadArtwork(
            Artwork artwork,
            GraphicsContext graphics,
            Canvas canvas) {
        try {
            Image image = artworkService.decodeImage(artwork);

            clearCanvas(graphics, canvas);
            graphics.drawImage(
                    image,
                    0,
                    0,
                    canvas.getWidth(),
                    canvas.getHeight()
            );
        } catch (IOException ex) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Open Failed",
                    "Could not open this artwork:\n" + ex.getMessage()
            );
        }
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
