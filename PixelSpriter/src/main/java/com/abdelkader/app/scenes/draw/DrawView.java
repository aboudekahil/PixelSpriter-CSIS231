package com.abdelkader.app.scenes.draw;

import com.abdelkader.app.akomponents.AKColorPicker;
import com.abdelkader.app.akomponents.AKPixelArtCanvas;
import com.abdelkader.meta.annotations.View;
import com.abdelkader.utils.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.awt.image.BufferedImage;

@View(controller = DrawController.class)
public class DrawView extends Scene {

    private final AKColorPicker colorPicker;
    private final Rectangle currentColorRect;
    private final AKPixelArtCanvas canvas;
    private final CheckMenuItem toggleGridMenuItem;
    private final MenuItem clearCanvasMenuItem;
    private final MenuItem newCanvasMenuItem;
    private final MenuItem saveMenuItem;
    private final MenuItem saveAsMenuItem;
    private final MenuItem openMenuItem;
    private final MenuItem saveToDatabaseItem;
    private final MenuItem undoMenuItem;
    private final MenuItem redoMenuItem;
    private final MenuItem openFromDatabaseMenuItem;

    public DrawView(Image image) {
        this((int) image.getWidth(), (int) image.getHeight());
        int width = (int) canvas.getGraphicsContext2D().getCanvas().getWidth();
        int height = (int) canvas.getGraphicsContext2D().getCanvas().getHeight();
        BufferedImage scaledUp = ImageUtils.scaleImage(image, width, height);
        image = SwingFXUtils.toFXImage(scaledUp, null);
        canvas.getGraphicsContext2D().drawImage(image, -1, -1, width, height);
    }

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

        newCanvasMenuItem = new MenuItem("New");
        saveMenuItem = new MenuItem("Save to disk");
        saveAsMenuItem = new MenuItem("Save as to disk");
        openMenuItem = new MenuItem("Open from disk");
        saveToDatabaseItem = new MenuItem("Save to database");
        openFromDatabaseMenuItem = new MenuItem("Open from database");

        fileMenu.getItems().addAll(
                newCanvasMenuItem,
                saveMenuItem,
                saveAsMenuItem,
                saveToDatabaseItem,
                openMenuItem,
                openFromDatabaseMenuItem
        );

        Menu canvasMenu = new Menu("Canvas");
        clearCanvasMenuItem = new MenuItem("Clear canvas");
        toggleGridMenuItem = new CheckMenuItem("Show Grid");

        canvasMenu.getItems().addAll(clearCanvasMenuItem, toggleGridMenuItem);

        Menu editMenu = new Menu("Edit");
        undoMenuItem = new MenuItem("Undo");
        redoMenuItem = new MenuItem("Redo");

        editMenu.getItems().addAll(undoMenuItem, redoMenuItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, canvasMenu);

        BorderPane canvasContainer = new BorderPane();
        canvasContainer.setCenter(canvas);

        root.setRight(rightPanel);
        root.setCenter(canvasContainer);
        root.setTop(menuBar);
    }
}
