package com.abdelkader.app.login;

import com.abdelkader.annotations.Component;
import com.abdelkader.interfaces.isController;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

    @Component
    private Label wowLabel;


    @Override public void init() {
        submitBtn.setOnAction(this::submitLoginHandle);

        link.setOnAction(event -> {
            submitBtn.setGraphic(link);
        });

        wowLabel.setText("My text");

    }
    private void submitLoginHandle(ActionEvent event) {
        String email = emailTF.getText();
        String password = passwordTF.getText();

        try {
            URL url = new URI("http://localhost:8090/api/v1/users/login").toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("email", email);
            connection.addRequestProperty("password", password);

            connection.connect();

            if (connection.getResponseCode() == 200)
                System.out.println("Yay");
            else
                System.out.println(":(");

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }


    }

}
