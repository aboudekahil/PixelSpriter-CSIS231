package com.abdelkader.app.scenes.draw;

import com.abdelkader.app.akomponents.AKColorPicker;
import com.abdelkader.app.akomponents.AKPixelArtCanvas;
import com.abdelkader.meta.annotations.View;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@View(controller = DrawController.class)
public class DrawView extends Scene {

    private final AKColorPicker colorPicker;
    private final Rectangle currentColorRect;
    private final AKPixelArtCanvas canvas;

    public DrawView(int[] size) {
        this(size[0], size[1]);
    }

    public DrawView(int gridWidth, int gridHeight) {
        super(new BorderPane());

        BorderPane root = (BorderPane) super.getRoot();
        root.setBackground(Background.fill(Color.valueOf("#778b96")));

        colorPicker = new AKColorPicker();
        canvas = new AKPixelArtCanvas(gridWidth, gridHeight, 400, 400);

        currentColorRect = new Rectangle(40, 40, Color.WHITE);

        VBox rightPanel = new VBox(colorPicker, currentColorRect);


        MenuBar menuBar = new MenuBar();
        menuBar.setBackground(Background.fill(Color.hsb(203, .12, .35)));

        Menu fileMenu = new Menu("File");

        MenuItem newCanvasMenuItem = new MenuItem("New");
        MenuItem saveMenuItem = new MenuItem("Save to disk");
        MenuItem saveAsMenuItem = new MenuItem("Save as to disk");
        MenuItem openMenuItem = new MenuItem("Open from disk");
        MenuItem saveToDatabaseItem = new MenuItem("Save to database");
        MenuItem openFromDatabaseMenuItem = new MenuItem("Open from database");

        fileMenu.getItems().addAll(
                newCanvasMenuItem,
                saveMenuItem,
                saveAsMenuItem,
                saveToDatabaseItem,
                openMenuItem,
                openFromDatabaseMenuItem
        );

        Menu canvasMenu = new Menu("Canvas");
        MenuItem clearCanvasMenuItem = new MenuItem("Clear canvas");
        CheckMenuItem toggleGridMenuItem = new CheckMenuItem("Show Grid");

        canvasMenu.getItems().addAll(clearCanvasMenuItem, toggleGridMenuItem);

        Menu editMenu = new Menu("Edit");
        MenuItem undoMenuItem = new MenuItem("Undo");
        MenuItem redoMenuItem = new MenuItem("Redo");

        editMenu.getItems().addAll(undoMenuItem, redoMenuItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, canvasMenu);

        BorderPane canvasContainer = new BorderPane();
        canvasContainer.setCenter(canvas);

        root.setRight(rightPanel);
        root.setCenter(canvasContainer);
        root.setTop(menuBar);
    }

    public AKColorPicker getColorPicker() {
        return colorPicker;
    }

    public AKPixelArtCanvas getCanvas() {
        return canvas;
    }

    public Rectangle getCurrentColorRect() {
        return currentColorRect;
    }
}
