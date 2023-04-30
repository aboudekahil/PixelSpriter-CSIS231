package com.abdelkader.app;

import com.abdelkader.app.login.LoginView;
import com.abdelkader.app.scenehandle.SceneSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class PixelSpriter
        extends Application {
    private static final int   MINIMUM_WINDOW_WIDTH  = 300;
    private static final int   MINIMUM_WINDOW_HEIGHT = 300;
    private static       Stage stage;

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        stage.setTitle("Pixel Spriter");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

        SceneSwitcher.goTo(LoginView.class);

        stage.show();
    }
}