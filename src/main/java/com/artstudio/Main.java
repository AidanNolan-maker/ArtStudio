package com.artstudio;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(1000, 650);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);

        Slider brushSize = new Slider(1, 40, 5);
        brushSize.setShowTickLabels(true);
        brushSize.setShowTickMarks(true);
        brushSize.setMajorTickUnit(10);
        brushSize.setPrefWidth(150);

        Label sizeLabel = new Label("Brush: 5px");

        brushSize.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        sizeLabel.setText(
                                "Brush: " + newValue.intValue() + " px"
                        )
        );

        Button clearButton = new Button("Clear Canvas");
        clearButton.setOnAction(event -> {
            graphics.setFill(Color.WHITE);
            graphics.fillRect(
                    0, 0,  canvas.getWidth(), canvas.getHeight()
            );
        });

        HBox toolbar = new HBox(
                12,
                new Label("Color:"),
                colorPicker,
                sizeLabel,
                brushSize,
                clearButton
        );

        toolbar.setPadding(new Insets(12));

        canvas.setOnMousePressed(event -> {
            graphics.setFill(colorPicker.getValue());
            graphics.fillOval(
                    event.getX() - brushSize.getValue() / 2,
                    event.getY() - brushSize.getValue() / 2,
                    brushSize.getValue(),
                    brushSize.getValue()
            );
        });

        canvas.setOnMouseDragged(event -> {
            graphics.setFill(colorPicker.getValue());
            graphics.fillOval(
                    event.getX() - brushSize.getValue() / 2,
                    event.getY() - brushSize.getValue() / 2,
                    brushSize.getValue(),
                    brushSize.getValue()
            );
        });

        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setStyle("-fx-background-color: #e5e7eb;");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvasContainer);

        Scene scene = new Scene(root, 1100, 750);

        stage.setTitle("ArtStudio");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
