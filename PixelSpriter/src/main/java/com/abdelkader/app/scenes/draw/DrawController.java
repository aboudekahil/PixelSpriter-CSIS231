package com.abdelkader.app.scenes.draw;

import com.abdelkader.app.akomponents.AKColorPicker;
import com.abdelkader.app.akomponents.AKPixelArtCanvas;
import com.abdelkader.app.scenes.SceneSwitcher;
import com.abdelkader.meta.annotations.Component;
import com.abdelkader.meta.interfaces.isController;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

public class DrawController implements isController {
    @Component
    private Rectangle currentColorRect;
    @Component
    private AKColorPicker colorPicker;
    @Component
    private AKPixelArtCanvas canvas;
    @Component
    private CheckMenuItem toggleGridMenuItem;
    @Component
    private MenuItem clearCanvasMenuItem;
    @Component
    private MenuItem newCanvasMenuItem;

    private Color currentColor = Color.WHITE;
    private GraphicsContext graphicsContext;

    @Override
    public void init() {
        colorPicker.currentColorProperty().addListener((observable, oldValue, newValue) -> {
            currentColorRect.setFill(newValue);
            currentColor = newValue;
        });

        graphicsContext = canvas.getGraphicsContext2D();

        canvas.getToolTipCanvas().setOnMouseMoved(this::handleMouseMove);
        canvas.getToolTipCanvas().setOnMouseDragged(this::handleMouseMove);
        canvas.getToolTipCanvas().setOnMouseClicked(this::handleMouseClick);
        canvas.getToolTipCanvas().setOnMouseExited(mouseEvent -> canvas.getToolTipGraphicsContext()
                .clearRect(0, 0, canvas.getWidth(), canvas.getHeight()));


        canvas.setOnMouseDragged(this::handleMouseClick);
        canvas.setOnMouseClicked(this::handleMouseClick);

        toggleGridMenuItem.setOnAction(this::toggleGrid);

        clearCanvasMenuItem.setOnAction(event -> {
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });

        newCanvasMenuItem.setOnAction(event -> {
            newCanvas();
        });

        graphicsContext.setGlobalAlpha(1);
        graphicsContext.setGlobalBlendMode(null);
        graphicsContext.setLineWidth(2);
    }

    public static Optional<int[]> getCanvasSize() {
        int[] size = new int[2];

        Dialog<Optional<int[]>> dialog = new Dialog<>();
        dialog.setTitle("Canvas Size Dialog");

        GridPane dialogPane = new GridPane();
        dialogPane.setPadding(new Insets(10, 10, 10, 10));
        dialogPane.setHgap(10);
        dialogPane.setVgap(10);

        Label widthLabel = new Label("Canvas Width (pixels):");
        TextField widthTextField = new TextField();
        widthTextField.requestFocus();

        Label heightLabel = new Label("Canvas Height (pixels):");
        TextField heightTextField = new TextField();

        // Add the labels and text fields to the dialog pane
        dialogPane.add(widthLabel, 0, 0);
        dialogPane.add(widthTextField, 1, 0);
        dialogPane.add(heightLabel, 0, 1);
        dialogPane.add(heightTextField, 1, 1);

        // Set the dialog pane as the root node of the dialog
        dialog.getDialogPane().setContent(dialogPane);


        // Add buttons for OK and Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {

                // Get the user's input from the text fields and parse to integers
                size[0] = Integer.parseInt(widthTextField.getText());
                size[1] = Integer.parseInt(heightTextField.getText());

                if (size[0] > 0 && size[1] > 0) return Optional.of(size);
            }
            return Optional.empty();
        });

        // Show the dialog and wait for the user to input values and submit the dialog
        dialog.showAndWait();

        return dialog.getResult();
    }

    public static void newCanvas() {
        Optional<int[]> size = getCanvasSize();

        if (size.isPresent()) {
            SceneSwitcher.goTo(DrawView.class,
                    new Object[]{size.get()[0], size.get()[1]},
                    new Class[]{int.class, int.class});
            return;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Canvas Size");
        alert.setHeaderText("Invalid canvas size");

        alert.showAndWait();
    }

    private void handleMouseMove(MouseEvent mouseEvent) {
        Point2D coords = canvas.getSquare(mouseEvent.getX(), mouseEvent.getY());
        GraphicsContext gc = canvas.getToolTipGraphicsContext();

        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(currentColor);
        gc.fillRect(coords.getX(), coords.getY(), canvas.getGridRatioWidth(), canvas.getGridRatioWidth());


        gc.setFill(currentColor.getBrightness() > 0.5 ? Color.BLACK : Color.WHITE);
        gc.fillRect(mouseEvent.getX() - 2, mouseEvent.getY() - 2, 4, 4);
    }

    private void handleMouseClick(MouseEvent mouseEvent) {
        Point2D coords = canvas.getSquare(mouseEvent.getX(), mouseEvent.getY());

        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            graphicsContext.setFill(currentColor);
            graphicsContext.fillRect((int) coords.getX(), (int) coords.getY(), canvas.getGridRatioHeight(), canvas.getGridRatioHeight());
        } else {
            graphicsContext.clearRect((int) coords.getX() + 1, (int) coords.getY() + 1, canvas.getGridRatioHeight() - 1, canvas.getGridRatioHeight() - 1);
        }
    }

    private void toggleGrid(ActionEvent event) {
        CheckMenuItem toggleButton = (CheckMenuItem) event.getTarget();
        canvas.getGridCanvas().setVisible(!canvas.getGridCanvas().isVisible());

        if (canvas.getGridCanvas().isVisible()) {
            toggleButton.setText("Hide Grid");
        } else {
            toggleButton.setText("Show Grid");
        }
    }
}
