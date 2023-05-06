package com.abdelkader.app.akomponents;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class AKPixelArtCanvas extends StackPane {

    private final static int DEFAULT_GRID_WIDTH = 16;
    private final static int DEFAULT_GRID_HEIGHT = 16;
    private final static int DEFAULT_CANVAS_HEIGHT = 200;
    private final static int DEFAULT_CANVAS_WIDTH = 200;
    private final IntegerProperty gridWidthProperty;
    private final IntegerProperty gridHeightProperty;
    private final Canvas drawingCanvas;
    private final Canvas gridCanvas;
    private final Canvas toolTipCanvas;

    private final int gridRatioHeight;
    private final int gridRatioWidth;


    public AKPixelArtCanvas() {
        this(DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT, DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
    }

    public AKPixelArtCanvas(int gridWidth, int gridHeight){
        this(gridWidth, gridHeight, DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);
    }

    public AKPixelArtCanvas(int gridWidth, int gridHeight, int canvasWidth, int canvasHeight) {
        super();

        setMinSize(canvasWidth, canvasHeight);

        gridWidthProperty = new SimpleIntegerProperty();
        gridHeightProperty = new SimpleIntegerProperty();

        drawingCanvas = new Canvas(canvasWidth, canvasHeight);
        gridCanvas = new Canvas();
        toolTipCanvas = new Canvas();

        setStyle("-fx-border-color: #000;-fx-border-width: 2;");

        toolTipCanvas.widthProperty().bind(drawingCanvas.widthProperty());
        toolTipCanvas.heightProperty().bind(drawingCanvas.heightProperty());

        gridCanvas.widthProperty().bind(drawingCanvas.widthProperty());
        gridCanvas.heightProperty().bind(drawingCanvas.heightProperty());
        gridCanvas.setMouseTransparent(true);
        gridCanvas.setVisible(false);

        toolTipCanvas.setCursor(Cursor.NONE);

        setGridWidth(gridWidth);
        setGridHeight(gridHeight);


        gridRatioHeight = (int) Math.floor(drawingCanvas.getHeight() / gridWidthProperty.intValue());
        gridRatioWidth = (int) Math.floor(drawingCanvas.getWidth() / gridWidthProperty.intValue());

        drawingCanvas.setHeight(gridRatioHeight * gridHeight);
        drawingCanvas.setWidth(gridRatioHeight * gridWidth);

        setMaxWidth(drawingCanvas.getWidth());
        setMaxHeight(drawingCanvas.getHeight());

        drawGrid();

        getChildren().addAll(drawingCanvas, toolTipCanvas, gridCanvas);
    }

    public Point2D getSquare(double x, double y){
        Point2D result = new Point2D(x - drawingCanvas.getLayoutX(),
                                     y - drawingCanvas.getLayoutY());

        result = new Point2D(Math.floor(result.getX() / gridRatioHeight),
                             Math.floor(result.getY() / gridRatioHeight));

        result = result.multiply(gridRatioHeight);

        return result;
    }

    public int getGridHeight() {
        return gridHeightProperty.intValue();
    }

    public int getGridRatioHeight(){ return gridRatioHeight;}
    public int getGridRatioWidth() {return gridRatioWidth;}

    public void setGridHeight(int gridHeight) {
        if (gridHeight < 1)
            throw new IllegalArgumentException("Grid height can't be less than 1.");

        gridHeightProperty.set(gridHeight);
    }

    public int getGridWidth() {
        return gridWidthProperty.intValue();
    }

    public void setGridWidth(int gridWidth) {
        if (gridWidth < 1)
            throw new IllegalArgumentException("Grid width can't be less than 1.");

        gridWidthProperty.setValue(gridWidth);
    }

    public IntegerProperty getGridHeightProperty() {
        return gridHeightProperty;
    }

    public IntegerProperty getGridWidthProperty() {
        return gridWidthProperty;
    }

    public GraphicsContext getGraphicsContext2D() {
        return drawingCanvas.getGraphicsContext2D();
    }

    public GraphicsContext getToolTipGraphicsContext(){return toolTipCanvas.getGraphicsContext2D();}

    private void drawGrid() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();

        gc.setStroke(Color.LIGHTGRAY);
        for (int i = gridRatioHeight; i < gridCanvas.getWidth(); i += gridRatioHeight) {
            gc.strokeLine(i, 0, i, gridCanvas.getHeight());
        }

        for (int i = gridRatioWidth; i < gridCanvas.getHeight(); i += gridRatioWidth) {
            gc.strokeLine(0, i, gridCanvas.getWidth(), i);
        }
    }

    public Canvas getGridCanvas() {
        return gridCanvas;
    }
}