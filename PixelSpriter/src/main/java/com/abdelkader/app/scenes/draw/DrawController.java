package com.abdelkader.app.scenes.draw;

import com.abdelkader.app.akomponents.AKColorPicker;
import com.abdelkader.app.akomponents.AKPixelArtCanvas;
import com.abdelkader.meta.annotations.Component;
import com.abdelkader.meta.interfaces.isController;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DrawController implements isController {
    @Component
    private Rectangle currentColorRect;

    @Component
    private AKColorPicker colorPicker;

    @Component
    private AKPixelArtCanvas canvas;
    @Override
    public void init() {
        currentColorRect.fillProperty().bind(colorPicker.currentColorProperty());

    }
}
