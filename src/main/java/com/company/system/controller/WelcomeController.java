package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WelcomeController {

    @FXML
    private Label appTitleLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Button accessButton;

    @FXML
    private Button albanianButton;

    @FXML
    private Button englishButton;

    @FXML
    public void initialize() {
        updateTexts();
        setupKeyboardAccess();
    }

    private void updateTexts() {
        appTitleLabel.setText(LanguageManager.get("app.title"));
        welcomeLabel.setText(LanguageManager.get("app.welcome"));
        accessButton.setText(LanguageManager.get("login.button"));

        if ("sq".equals(LanguageManager.getCurrentLocale().getLanguage())) {
            subtitleLabel.setText("Menaxhoni punetoret, kontratat dhe pagat ne nje sistem te vetem.");
        } else {
            subtitleLabel.setText("Manage employees, contracts and salaries in one system.");
        }

        albanianButton.setText("SQ");
        englishButton.setText("EN");
    }

    private void setupKeyboardAccess() {
        accessButton.setDefaultButton(true);
        accessButton.setAccessibleText("Open login page");
        albanianButton.setAccessibleText("Switch language to Albanian");
        englishButton.setAccessibleText("Switch language to English");
    }

    @FXML
    private void openLogin() {
        MainApp.showLogin();
    }

    @FXML
    private void switchToAlbanian() {
        LanguageManager.setLanguage("sq");
        updateTexts();
    }

    @FXML
    private void switchToEnglish() {
        LanguageManager.setLanguage("en");
        updateTexts();
    }
}

