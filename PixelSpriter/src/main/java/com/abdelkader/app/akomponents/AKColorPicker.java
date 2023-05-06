package com.abdelkader.app.akomponents;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.security.InvalidParameterException;

public class AKColorPicker extends VBox {
    final private static int                         DEFAULT_SIZE      = 200;
    final private static double                      sliderHeightRatio = 0.15;
    final private static Color                       defaultColor      = Color.WHITE;
    final private        SimpleObjectProperty<Color> currentColor;
    final private        SimpleIntegerProperty       size;
    private              GraphicsContext             colorPaletteGc;
    private              Slider                      hueSlider;
    private              Canvas                      colorPaletteOverlayCanvas;

    public AKColorPicker() {
        this(DEFAULT_SIZE);
    }


    public AKColorPicker(int size) {
        this(size, defaultColor);
    }

    public AKColorPicker(Color color) {
        this(DEFAULT_SIZE, color);
    }

    public AKColorPicker(int size, Color color) {
        this.size = new SimpleIntegerProperty();
        setSize(size);
        currentColor = new SimpleObjectProperty<>(color);
        setCache(true);

        init_ui();
    }

    // Getters
    public int getSize() {
        return size.intValue();
    }

    // Setters
    public void setSize(int size) {
        if (size <= 0) throw new InvalidParameterException("Color picker size must be greater than 0.");

        this.size.setValue(size);
    }

    public IntegerProperty sizeProperty() {
        return size;
    }

    public ObjectProperty<Color> currentColorProperty() {
        return currentColor;
    }

    private void init_ui() {
        // Color Palette
        Canvas colorPaletteCanvas = new Canvas(size.intValue(), size.intValue());
        colorPaletteGc = colorPaletteCanvas.getGraphicsContext2D();

        colorPaletteOverlayCanvas = new Canvas(size.intValue(), size.intValue());
        colorPaletteOverlayCanvas.setMouseTransparent(true);

        colorPaletteCanvas.setOnMouseDragged(this::handleChooseFromPalette);
        colorPaletteCanvas.setOnMouseClicked(this::handleChooseFromPalette);

        StackPane colorPalette = new StackPane(colorPaletteCanvas, colorPaletteOverlayCanvas);

        // Slider
        StackPane hueSlider = new StackPane();

        this.hueSlider = new Slider(0, 359, 1);
        hueSlider.setMaxWidth(colorPaletteCanvas.getWidth());
        this.hueSlider.valueProperty().addListener((observableValue, number, t1) -> {
            currentColor.setValue(Color.hsb(t1.intValue(),
                    currentColor.getValue().getSaturation(),
                    currentColor.getValue().getBrightness()
            ));
            drawPalette(t1.intValue());
        });

        Canvas hueSliderOverlay_canvas = new Canvas(size.intValue(), size.intValue() * sliderHeightRatio);
        GraphicsContext hueSliderOverlay_gc = hueSliderOverlay_canvas.getGraphicsContext2D();

        for (int i = 0; i < size.intValue(); i++) {
            hueSliderOverlay_gc.setFill(Color.hsb(hueShift(i, size.intValue()), 1, 1));

            hueSliderOverlay_gc.fillRect(i, 0, 1, size.intValue() * sliderHeightRatio);
        }

        EventHandler<? super MouseEvent> mouseSliderHandle = mouseEvent -> this.hueSlider.setValue(
                mouseEvent.getX() * this.hueSlider.getMax() / this.hueSlider.getWidth());

        hueSliderOverlay_canvas.setOnMouseClicked(mouseSliderHandle);
        hueSliderOverlay_canvas.setOnMouseDragged(mouseSliderHandle);

        hueSlider.getChildren().addAll(this.hueSlider, hueSliderOverlay_canvas);

        // Draw color palette
        drawPalette(this.hueSlider.valueProperty().intValue());

        // Circle for chosen color
        Circle colorPicked_circle = new Circle();
        colorPicked_circle.setRadius(10);
        colorPicked_circle.setStroke(Color.BLACK);


        // Add all children
        this.setSpacing(2);
        this.getChildren().addAll(colorPalette, hueSlider);

    }

    private void drawPalette(int hue) {
        for (int row = 0; row < getSize(); row++) {
            for (int col = 0; col < getSize(); col++) {
                colorPaletteGc.setFill(Color.hsb(hue, ((float) row) / getSize(), 1 - ((float) col) / getSize()));
                colorPaletteGc.fillRect(row, col, 1, 1);
            }
        }
    }


    private int hueShift(int val, int max) {
        return val * 360 / max;
    }

    private void handleChooseFromPalette(MouseEvent event) {
        double saturation = Math.max(Math.min(event.getX(), getSize()), 0) / getSize();

        double brightness = 1 - (Math.max(Math.min(event.getY(), getSize()), 0) / getSize());

        currentColor.set(Color.hsb(hueSlider.valueProperty().doubleValue(), saturation, brightness));

        colorPaletteOverlayCanvas.getGraphicsContext2D().setStroke(brightness > 0.5 ? Color.BLACK : Color.WHITE);

        colorPaletteOverlayCanvas.getGraphicsContext2D().clearRect(0, 0, getSize(), getSize());

        colorPaletteOverlayCanvas.getGraphicsContext2D().strokeOval(Math.max(Math.min(event.getX(), getSize() - 5), -5),
                Math.max(Math.min(event.getY(), getSize() - 5), -5),
                getSize() * 0.05,
                getSize() * 0.05
        );
    }


}
