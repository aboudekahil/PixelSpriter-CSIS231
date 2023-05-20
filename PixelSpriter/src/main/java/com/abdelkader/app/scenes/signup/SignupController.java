package com.abdelkader.app.scenes.signup;


import com.abdelkader.app.scenes.SceneSwitcher;
import com.abdelkader.app.scenes.draw.DrawController;
import com.abdelkader.app.scenes.draw.DrawView;
import com.abdelkader.app.scenes.login.LoginView;
import com.abdelkader.backend.RequestsHandler;
import com.abdelkader.backend.modals.CountryModel;
import com.abdelkader.meta.annotations.Component;
import com.abdelkader.meta.interfaces.isController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.util.Collections;
import java.util.Optional;

public class SignupController implements isController {

    private static final ObservableList<CountryModel> countryList = FXCollections.observableArrayList();
    @Component
    private Button submitBtn;
    @Component
    private Hyperlink gotoLogin;
    @Component
    private ComboBox<CountryModel> counrtyDropdown;
    @Component
    private TextField usernameTxtField;
    @Component
    private TextField emailTxtField;
    @Component
    private PasswordField passwordTxtField;

    @Override
    public void init() {
        if (countryList.isEmpty()) {
            Collections.addAll(countryList, RequestsHandler.getInstance().getAllCountries());
        }

        counrtyDropdown.itemsProperty().set(countryList);

        gotoLogin.setOnAction(event -> {
            SceneSwitcher.goTo(LoginView.class);
        });

        submitBtn.setOnAction(this::signUp);
    }

    private void signUp(ActionEvent actionEvent) {
        if (!(usernameTxtField.getText().isEmpty()
                || emailTxtField.getText().isEmpty()
                || passwordTxtField.getText().isEmpty()
                || counrtyDropdown.getValue() == null)) {
            try {
                RequestsHandler.getInstance().signUp(emailTxtField.getText(),
                        usernameTxtField.getText(),
                        passwordTxtField.getText(),
                        counrtyDropdown.getValue().id());

                DrawController.newCanvas();
                return;
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to Sign up");
                alert.setHeaderText("Something happened");

                alert.showAndWait();
            }

            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to Sign up");
        alert.setHeaderText("Please fill out all the form");

        alert.showAndWait();
    }


}
