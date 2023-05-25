package com.abdelkader.app.akomponents;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class AKArtWidget<T> extends VBox {

    private final MenuItem shareMenuItem;
    private final MenuItem deleteMenuItem;
    private final MenuItem renameMenuItem;
    private final Label    titleLabel;
    private final Label    descriptionLabel;
    private final T value;

    public AKArtWidget(Image image, String title, String description, T value) {
        this.value = value;
        // create an ImageView for the image
        ImageView imageView = new ImageView(image);

        // create a Label for the title
        titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");

        // create a Label for the description
        descriptionLabel = new Label(description);

        // add the components to the VBox
        getChildren().addAll(imageView, titleLabel, descriptionLabel);


        // add styles directly to the widget
        setStyle("-fx-cursor: hand; -fx-background-color: #f0f0f0;");

        // add mouse event handlers to change the background color on hover
        setOnMouseEntered(event -> {
            setStyle("-fx-cursor: hand; -fx-background-color: #e0e0e0;");
        });
        setOnMouseExited(event -> {
            setStyle("-fx-cursor: hand; -fx-background-color: #f0f0f0;");
        });

        setPadding(new Insets(5));

        ContextMenu contextMenu = new ContextMenu();
        deleteMenuItem = new MenuItem("Delete");
        renameMenuItem = new MenuItem("Rename");
        shareMenuItem = new MenuItem("Share");

        contextMenu.getItems().addAll(renameMenuItem, deleteMenuItem, shareMenuItem);

        setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(this,
                                                                       contextMenuEvent.getScreenX(),
                                                                       contextMenuEvent.getScreenY()
        ));

    }

    public void setDeleteMenuItemAction(EventHandler<ActionEvent> event) {
        deleteMenuItem.onActionProperty().set(event);
    }

    public void setRenameMenuItemAction(EventHandler<ActionEvent> event) {
        renameMenuItem.onActionProperty().set(event);
    }

    public void setShareMenuItemAction(EventHandler<ActionEvent> event) {
        shareMenuItem.onActionProperty().set(event);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String s) {
        titleLabel.setText(s);
    }

    public String getDescription() {
        return descriptionLabel.getText();
    }

    public void setDescription(String s) {
        descriptionLabel.setText(s);
    }

    public T getValue() {
        return value;
    }
}
