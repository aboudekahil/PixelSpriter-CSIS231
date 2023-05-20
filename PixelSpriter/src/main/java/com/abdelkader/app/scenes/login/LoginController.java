package com.abdelkader.app.scenes.login;

import com.abdelkader.app.scenes.draw.DrawController;
import com.abdelkader.app.scenes.draw.DrawView;
import com.abdelkader.backend.RequestsHandler;
import com.abdelkader.meta.annotations.Component;
import com.abdelkader.app.scenes.SceneSwitcher;
import com.abdelkader.app.scenes.signup.SignupView;
import com.abdelkader.meta.interfaces.isController;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

import java.util.Optional;

public class LoginController
        implements isController {

    @Component
    private Button    submitBtn;
    @Component
    private TextField emailTF;
    @Component
    private TextField passwordTF;

    @Component(name = "gotoSignUp")
    private Hyperlink link;


    @Override public void init() {
        submitBtn.setOnAction(this::submitLoginHandle);

        link.setOnAction(event -> {
            SceneSwitcher.goTo(SignupView.class);
        });
    }
    private void submitLoginHandle(ActionEvent event) {
        if(RequestsHandler.getInstance().requestLogin(emailTF.getText(), passwordTF.getText())){
            DrawController.newCanvas();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed Login");
        alert.setHeaderText("Wrong email or password");

        alert.showAndWait();

    }

}
