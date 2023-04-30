package com.abdelkader.app.mycomponents;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class PixelArtButton extends Button {
    public PixelArtButton(String text){
        super(text);

        Canvas canvas = new Canvas(100, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.RED);
        gc.fillRect(0, 0, 50, 100);
        gc.setFill(Color.GREEN);
        gc.fillRect(50, 0, 50, 100);

        setGraphic(canvas);

        setBackground(Background.EMPTY);
    }

}
