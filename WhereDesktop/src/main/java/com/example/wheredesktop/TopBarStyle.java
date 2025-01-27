package com.example.wheredesktop;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TopBarStyle {

    private double xOffset = 0; // For window dragging
    private double yOffset = 0;

    private boolean isFullscreen = false;

    private final int RESIZE_MARGIN = 8; // Margin around edges for resizing
    private boolean isRightResize = false;
    private boolean isBottomResize = false;
    private boolean isTopResize = false;
    private boolean isLeftResize = false;

    public HBox createTopBar(Stage stage){
        //TOP BAR STYLING
        HBox topBar = new HBox();
        topBar.setPrefHeight(30); // Height of the top bar
        topBar.setStyle("-fx-background-color: black;"); // Black background
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Title Label
        Label barTitle = new Label("Where_Admin_APP");
        barTitle.setTextFill(Color.LIMEGREEN); // Green text
        barTitle.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Minimize Button
        Button minimizeButton = new Button("_");
        minimizeButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: transparent;
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """);
        minimizeButton.setOnMouseEntered(e -> minimizeButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: rgba(50, 205, 50, 0.2);
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """));
        minimizeButton.setOnMouseExited(e -> minimizeButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: transparent;
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """));
        minimizeButton.setOnAction(e -> stage.setIconified(true));


        // Fullscreen Button
        Button fullscreenButton = new Button("⬜");
        fullscreenButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: transparent;
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """);
        fullscreenButton.setOnMouseEntered(e -> fullscreenButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: rgba(50, 205, 50, 0.2);
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """));
        fullscreenButton.setOnMouseExited(e -> fullscreenButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: transparent;
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """));
        fullscreenButton.setOnAction(e -> {
            isFullscreen = !isFullscreen;
            stage.setFullScreen(isFullscreen);
        });

        // Close Button
        Button closeButton = new Button("X");
        closeButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: transparent;
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
            """);
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: rgba(255, 0, 0, 0.2);
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """));

        closeButton.setOnMouseExited(e -> closeButton.setStyle("""
            -fx-text-fill: limegreen;
            -fx-background-color: transparent;
            -fx-font-size: 16px;
            -fx-border-width: 0;
            -fx-padding: 5;
        """));

        closeButton.setOnAction(e -> {
            Timeline fadeOut = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(stage.opacityProperty(), 1)),
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(stage.opacityProperty(), 0))
            );
            fadeOut.setOnFinished(event -> stage.close());
            fadeOut.play();
            stage.close();
        });



        topBar.getChildren().addAll(barTitle, spacer, minimizeButton, fullscreenButton, closeButton);

        // Add drag functionality to the top bar
        topBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        topBar.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        return topBar;
    }

    /**
     * Aggiunge un ascoltatore di ridimensionamento alla finestra.
     * Modifica il cursore e le dimensioni della finestra in base alla posizione del mouse.
     *
     * @param root Il nodo radice della scena.
     * @param stage Il palco principale dell'applicazione.
     */
    public void addResizeListener(Region root, Stage stage) {
        root.setOnMouseMoved(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            double width = stage.getWidth();
            double height = stage.getHeight();

            isRightResize = mouseX >= width - RESIZE_MARGIN && mouseX <= width;
            isBottomResize = mouseY >= height - RESIZE_MARGIN && mouseY <= height;
            isLeftResize = mouseX >= 0 && mouseX <= RESIZE_MARGIN;
            isTopResize = mouseY >= 0 && mouseY <= RESIZE_MARGIN;

            if (isRightResize && isBottomResize) {
                root.setCursor(Cursor.SE_RESIZE);
            } else if (isLeftResize && isBottomResize) {
                root.setCursor(Cursor.SW_RESIZE);
            } else if (isRightResize && isTopResize) {
                root.setCursor(Cursor.NE_RESIZE);
            } else if (isLeftResize && isTopResize) {
                root.setCursor(Cursor.NW_RESIZE);
            } else if (isRightResize) {
                root.setCursor(Cursor.E_RESIZE);
            } else if (isBottomResize) {
                root.setCursor(Cursor.S_RESIZE);
            } else if (isLeftResize) {
                root.setCursor(Cursor.W_RESIZE);
            } else if (isTopResize) {
                root.setCursor(Cursor.N_RESIZE);
            } else {
                root.setCursor(Cursor.DEFAULT);
            }
        });

        root.setOnMouseDragged(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            if (isRightResize) {
                stage.setWidth(mouseX);
            }
            if (isBottomResize) {
                stage.setHeight(mouseY);
            }
            if (isLeftResize) {
                double newWidth = stage.getWidth() - mouseX;
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                    stage.setX(event.getScreenX());
                }
            }
            if (isTopResize) {
                double newHeight = stage.getHeight() - mouseY;
                if (newHeight > stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                    stage.setY(event.getScreenY());
                }
            }
        });
    }
}
