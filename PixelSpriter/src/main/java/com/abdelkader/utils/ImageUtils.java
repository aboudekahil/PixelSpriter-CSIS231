package com.abdelkader.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage scaleImage(Image image, int width, int height) {
        BufferedImage writableToBuffered = SwingFXUtils.fromFXImage(image, null);
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        scaledImage.createGraphics().drawImage(writableToBuffered.getScaledInstance(
                width,
                height,
                java.awt.Image.SCALE_DEFAULT
        ), 0, 0, null);

        return scaledImage;
    }
}
