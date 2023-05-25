package com.abdelkader.app.scenes.draw;

import com.abdelkader.app.PixelSpriter;
import com.abdelkader.app.akomponents.AKArtWidget;
import com.abdelkader.app.akomponents.AKColorPicker;
import com.abdelkader.app.akomponents.AKPixelArtCanvas;
import com.abdelkader.app.scenes.SceneSwitcher;
import com.abdelkader.backend.RequestsHandler;
import com.abdelkader.backend.UserHandler;
import com.abdelkader.backend.modals.ImageDTO;
import com.abdelkader.meta.annotations.Component;
import com.abdelkader.meta.interfaces.isController;
import com.abdelkader.utils.FixedSizeStack;
import com.abdelkader.utils.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class DrawController implements isController {
    private final SnapshotParameters spa = new SnapshotParameters();
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
    @Component
    private MenuItem saveMenuItem;
    @Component
    private MenuItem saveAsMenuItem;
    @Component
    private MenuItem openMenuItem;
    @Component
    private MenuItem saveToDatabaseItem;
    @Component
    private MenuItem undoMenuItem;
    @Component
    private MenuItem redoMenuItem;
    @Component
    private MenuItem openFromDatabaseMenuItem;


    private Color currentColor = Color.WHITE;
    private GraphicsContext graphicsContext;
    private File imageFile = null;
    private boolean imageFileDB = false;
    private String title = null;
    private String description = null;
    private final FixedSizeStack<WritableImage> undoStack    = new FixedSizeStack<>(5);
    private final FixedSizeStack<WritableImage> redoStack    = new FixedSizeStack<>(5);

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

    @Override
    public void init() {
        spa.setFill(Color.TRANSPARENT);

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

        clearCanvasMenuItem.setOnAction(
                event -> {
                    undoStack.push(graphicsContext.getCanvas().snapshot(spa, null));
                    graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    redoStack.clear();
                }
        );

        newCanvasMenuItem.setOnAction(event -> newCanvas());

        saveMenuItem.setOnAction(this::saveToDisk);
        saveAsMenuItem.setOnAction(event -> {
            imageFile = null;
            saveToDisk(event);
        });

        openMenuItem.setOnAction((event) -> openFromDisk());

        saveToDatabaseItem.setOnAction(event -> {
            try {
                saveToDb(event);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Something went wrong");
                alert.setHeaderText("??");

                alert.showAndWait();
            }
        });

        undoMenuItem.setOnAction(event -> undo());
        redoMenuItem.setOnAction(event -> redo());

        openFromDatabaseMenuItem.setOnAction(event -> {
            try {
                openFromDb();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        graphicsContext.setGlobalAlpha(1);
        graphicsContext.setGlobalBlendMode(null);
        graphicsContext.setLineWidth(2);
    }

    private void saveToDb(ActionEvent actionEvent) throws IOException {
        int width = canvas.getGridWidth();
        int height = canvas.getGridHeight();


        if (!imageFileDB) {
            if (title == null || description == null) {
                getTitleAndDesc();
                if (title == null || description == null) return;
            }
        }

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());

        canvas.getGraphicsContext2D().getCanvas().snapshot(spa, writableImage);

        BufferedImage scaledDown = ImageUtils.scaleImage(writableImage, width, height);


        if (RequestsHandler.getInstance().sendImage(title, description, scaledDown, width, height)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Image upload was successful");
            alert.setHeaderText(":)");

            alert.showAndWait();
            imageFileDB = true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong with image upload");
            alert.setHeaderText("??");

            alert.showAndWait();
        }
    }

    private void openFromDisk() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");

        // set the file extension filters to only show image files
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files",
                "*.png",
                "*.jpg",
                "*.gif",
                "*.bmp"
        ));

        // show the file dialog and wait for user to choose an image file
        File file = fileChooser.showOpenDialog(PixelSpriter.getStage());

        if (file == null) return;

        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            SceneSwitcher.goTo(DrawView.class, new Object[]{image}, new Class[]{image.getClass()});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToDisk(ActionEvent actionEvent) {
        int width = canvas.getGridWidth();
        int height = canvas.getGridHeight();

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());

        canvas.getGraphicsContext2D().getCanvas().snapshot(spa, writableImage);

        BufferedImage scaledDown = ImageUtils.scaleImage(writableImage, width, height);

        if (imageFile != null) {
            try {
                ImageIO.write(scaledDown, "png", imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG files", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showSaveDialog(PixelSpriter.getStage());

        if (file != null) {
            // Save the scaled-down image to the chosen file
            imageFile = file;
            try {
                ImageIO.write(scaledDown, "png", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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

        undoStack.push(graphicsContext.getCanvas().snapshot(spa, null));
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            graphicsContext.setFill(currentColor);
            graphicsContext.fillRect((int) coords.getX(), (int) coords.getY(), canvas.getGridRatioHeight(), canvas.getGridRatioHeight());
        } else {
            graphicsContext.clearRect((int) coords.getX() + 1, (int) coords.getY() + 1, canvas.getGridRatioHeight() - 1, canvas.getGridRatioHeight() - 1);
        }
        redoStack.clear();
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

    private void getTitleAndDesc() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Canvas Title and Description Dialog");

        GridPane dialogPane = new GridPane();
        dialogPane.setPadding(new Insets(10, 10, 10, 10));
        dialogPane.setHgap(10);
        dialogPane.setVgap(10);

        Label titleLabel = new Label("Canvas Title (128 chr):");
        TextField titleTextField = new TextField();
        titleTextField.requestFocus();

        Label descriptionLabel = new Label("Canvas Description (256 chr):");
        TextField descriptionTextField = new TextField();

        // Add the labels and text fields to the dialog pane
        dialogPane.add(titleLabel, 0, 0);
        dialogPane.add(titleTextField, 1, 0);
        dialogPane.add(descriptionLabel, 0, 1);
        dialogPane.add(descriptionTextField, 1, 1);


        dialog.getDialogPane().setContent(dialogPane);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    if (titleTextField.getText().length() > 128 || descriptionTextField.getText().length() > 256)
                        throw new IllegalStateException();

                    title = titleTextField.getText();
                    description = descriptionTextField.getText();

                } catch (IllegalStateException e) {
                    // If the user input is not a valid integer, display an error message
                    Dialog<String> errorDialog = new Dialog<>();
                    errorDialog.setTitle("Invalid Input");
                    errorDialog.setContentText(
                            "Title length and descripiton length must be less than 128 and 256 characters " +
                                    "respectively!");
                    errorDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    errorDialog.showAndWait();
                }
            }
            return buttonType;
        });

        // Show the dialog and wait for the user to input values and submit the dialog
        dialog.showAndWait();
    }

    public void openFromDb() throws IOException {
        Stage dialog = new Stage();
        BorderPane root = new BorderPane();
        FlowPane flexbox = new FlowPane(10, 10);

        List<ImageDTO> imagesDB = RequestsHandler.getInstance()
                .findImagesFromId(UserHandler.getCurrUser().id());

        for (var imageRow : imagesDB) {
            FileInputStream in = new FileInputStream(imageRow.imagePath());
            var image = new Image(in);
            in.close();

            int width = (int) image.getWidth();
            int height = (int) image.getHeight();

            int id = imageRow.id();

            int factor = (int) Math.ceil(150d / width);

            int newWidth = factor * width;
            int newHeight = factor * height;

            BufferedImage scaledUp = ImageUtils.scaleImage(image, newWidth, newHeight);

            AKArtWidget<ImageDTO> widget = new AKArtWidget<ImageDTO>(
                    SwingFXUtils.toFXImage(scaledUp, null),
                    imageRow.title(),
                    imageRow.description(),
                    imageRow
            );

            widget.setOnMouseClicked(mouseEvent -> {
                if (!(mouseEvent.getButton() == MouseButton.SECONDARY)) {
                    openFromImage(widget.getValue());
                    dialog.close();
                }
            });

            widget.setDeleteMenuItemAction(event -> {
                try {
                    RequestsHandler.getInstance().deleteImageFromId(id);
                } catch (NoSuchElementException | UnknownError | IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(e.getMessage());

                    alert.showAndWait();
                }
                flexbox.getChildren().remove(widget);
            });

            widget.setRenameMenuItemAction(event -> renameImageFromId(widget, id));

            widget.setShareMenuItemAction(event -> showShareImage(id));

            flexbox.getChildren().add(widget);
        }
        root.setCenter(flexbox);
        Label openLabel = new Label("Or Enter Image id of a friend:");
        TextField idInput = new TextField();
        Button submitBtn = new Button("Submit");
        submitBtn.setDefaultButton(true);
        submitBtn.setOnAction(event -> {
            if (openImageFromId(Integer.parseInt(idInput.getText()))) {
                dialog.close();
                return;
            }
            Dialog<String> errorDialog = new Dialog<>();
            errorDialog.setTitle("Invalid id");
            errorDialog.setContentText("The id you entered is not valid!");
            errorDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            errorDialog.showAndWait();
        });
        HBox or = new HBox(openLabel, idInput, submitBtn);

        root.setBottom(or);

        dialog.setScene(new Scene(root));
        dialog.setTitle("Open from database");
        dialog.show();
    }

    private boolean openImageFromId(int i) {
        SceneSwitcher.goTo(DrawView.class);
        return true;
    }

    private void renameImageFromId(AKArtWidget<ImageDTO> widget, int id) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Canvas Title and Description Dialog");

            GridPane dialogPane = new GridPane();
            dialogPane.setPadding(new Insets(10, 10, 10, 10));
            dialogPane.setHgap(10);
            dialogPane.setVgap(10);

            Label titleLabel = new Label("Canvas Title (128 chr):");
            TextField titleTextField = new TextField();
            titleTextField.requestFocus();
            titleTextField.setText(widget.getTitle());

            Label descriptionLabel = new Label("Canvas Description (256 chr):");
            TextField descriptionTextField = new TextField();
            descriptionTextField.setText(widget.getDescription());

            // Add the labels and text fields to the dialog pane
            dialogPane.add(titleLabel, 0, 0);
            dialogPane.add(titleTextField, 1, 0);
            dialogPane.add(descriptionLabel, 0, 1);
            dialogPane.add(descriptionTextField, 1, 1);


            dialog.getDialogPane().setContent(dialogPane);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType != ButtonType.OK) return buttonType;
                if (titleTextField.getText().length() > 128 || descriptionTextField.getText().length() > 256) {
                    // If the user input is not a valid integer, display an error message
                    Dialog<String> errorDialog = new Dialog<>();
                    errorDialog.setTitle("Invalid Input");
                    errorDialog.setContentText(
                            "Title length and descripiton length must be less than 128 and 256 characters " +
                                    "respectively!");
                    errorDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    errorDialog.showAndWait();
                    return buttonType;
                }

                String newTitle = titleTextField.getText();
                String newDescription = descriptionTextField.getText();

                RequestsHandler.getInstance().renameImage(id, newTitle, newDescription);

                widget.setTitle(newTitle);
                widget.setDescription(newDescription);
                dialog.close();

                return buttonType;
            });

            // Show the dialog and wait for the user to input values and submit the dialog
            dialog.showAndWait();
    }

    private void showShareImage(int id) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Share Image");

        BorderPane dialogPane = new BorderPane();
        dialogPane.setPadding(new Insets(10, 10, 10, 10));

        Label idLabel = new Label("Image id: ");
        Hyperlink idLink = new Hyperlink(String.valueOf(id));

        idLink.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(idLink.getText());
            clipboard.setContent(content);
        });

        HBox shareText = new HBox(idLabel, idLink);


        // Add the labels and text fields to the dialog pane
        dialogPane.setCenter(shareText);


        dialog.getDialogPane().setContent(dialogPane);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                dialog.close();
            }
            return buttonType;
        });

        // Show the dialog and wait for the user to input values and submit the dialog
        dialog.showAndWait();
    }

    private void openFromImage(ImageDTO value) {
        value.imagePath();
    }

    public void undo() {
        Canvas canvas = graphicsContext.getCanvas();
        WritableImage image = undoStack.pop();
        if (image == null) return;
        WritableImage oldImage = graphicsContext.getCanvas().snapshot(spa, null);

        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.drawImage(image, -1, -1, canvas.getWidth(), canvas.getHeight());

        redoStack.push(oldImage);
    }

    public void redo() {
        Canvas canvas = graphicsContext.getCanvas();
        WritableImage image = redoStack.pop();
        if (image == null) return;

        WritableImage oldImage = graphicsContext.getCanvas().snapshot(spa, null);

        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        graphicsContext.drawImage(image, -1, -1, canvas.getWidth(), canvas.getHeight());

        undoStack.push(oldImage);
    }
}
