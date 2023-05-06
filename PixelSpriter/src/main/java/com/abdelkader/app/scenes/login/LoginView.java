package com.abdelkader.app.scenes.login;

import com.abdelkader.meta.annotations.View;
import com.abdelkader.app.akomponents.PixelArtButton;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@View(controller = LoginController.class)
public class LoginView
        extends Scene {

    private final TextField emailTF;
    private final TextField passwordTF;
    private final Button    submitBtn;
    private final Hyperlink gotoSignUp;

    public LoginView() {
        super(new VBox());

        Pane root = (Pane) getRoot();

        Label emailLabel = new Label("Email");
        emailTF = new TextField();

        emailLabel.setLabelFor(emailTF);
        emailTF.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password");
        passwordTF = new TextField();

        passwordLabel.setLabelFor(passwordTF);
        passwordTF.setPromptText("Enter your password");

        submitBtn = new PixelArtButton("Submit");

        submitBtn.setDefaultButton(true);

        gotoSignUp = new Hyperlink("Go to sign up");

        root.getChildren().addAll(emailLabel, emailTF, passwordLabel, passwordTF, submitBtn, gotoSignUp);
    }
}
