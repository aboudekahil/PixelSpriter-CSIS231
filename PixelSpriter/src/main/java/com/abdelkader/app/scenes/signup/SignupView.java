package com.abdelkader.app.scenes.signup;

import com.abdelkader.backend.modals.CountryModel;
import com.abdelkader.meta.annotations.View;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

@View(controller = SignupController.class)
public class SignupView extends Scene {
    private final TextField usernameTxtField;
    private final TextField emailTxtField;
    private final PasswordField passwordTxtField;
    private final Button submitBtn;
    private final Hyperlink gotoLogin;
    private final ComboBox<CountryModel> counrtyDropdown;

    public SignupView() {
        super(new VBox());
        VBox root = (VBox) getRoot();

        Label usernameLabel = new Label("Username");
        usernameTxtField = new TextField();

        Label passwordLable = new Label("Password");
        passwordTxtField = new PasswordField();

        Label emailLable = new Label("Email");
        emailTxtField = new TextField();

        Label countryLable = new Label("Country");
        counrtyDropdown = new ComboBox<>(FXCollections.observableArrayList(new CountryModel(1, "Lb")));

        submitBtn = new Button("Sign up");
        submitBtn.setDefaultButton(true);

        gotoLogin = new Hyperlink("Already have an account? Log in");

        root.getChildren().addAll(
                usernameLabel,
                usernameTxtField,
                emailLable,
                emailTxtField,
                passwordLable,
                passwordTxtField,
                countryLable,
                counrtyDropdown,
                submitBtn,
                gotoLogin
        );
    }
}
