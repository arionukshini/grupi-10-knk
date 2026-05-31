package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

public class WelcomeController {

    private static final String LANGUAGE_BUTTON_STYLE = """
            -fx-background-color: rgba(255, 255, 255, 0.16);
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: rgba(255, 255, 255, 0.42);
            -fx-text-fill: white;
            -fx-cursor: hand;
            """;

    private static final String LANGUAGE_BUTTON_HOVER_STYLE = """
            -fx-background-color: white;
            -fx-text-fill: #19316c;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: white;
            -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.26), 12, 0.2, 0, 4);
            -fx-translate-y: -1;
            -fx-cursor: hand;
            """;

    private static final String LANGUAGE_BUTTON_FOCUS_STYLE = """
            -fx-background-color: white;
            -fx-text-fill: #19316c;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: #60a5fa;
            -fx-border-width: 1.2;
            -fx-effect: dropshadow(gaussian, rgba(96, 165, 250, 0.42), 10, 0.22, 0, 0);
            -fx-cursor: hand;
            """;

    private static final String ACCESS_BUTTON_STYLE = """
            -fx-background-color: linear-gradient(to right, #ffffff, #dff8ff);
            -fx-text-fill: #0b3d78;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: rgba(255, 255, 255, 0.7);
            -fx-cursor: hand;
            """;

    private static final String ACCESS_BUTTON_HOVER_STYLE = """
            -fx-background-color: linear-gradient(to right, #ffffff, #bdefff);
            -fx-text-fill: #082a56;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: white;
            -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.32), 18, 0.22, 0, 5);
            -fx-translate-y: -1;
            -fx-cursor: hand;
            """;

    private static final String ACCESS_BUTTON_FOCUS_STYLE = """
            -fx-background-color: linear-gradient(to right, #ffffff, #bdefff);
            -fx-text-fill: #082a56;
            -fx-font-size: 16px;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-border-radius: 8;
            -fx-border-color: white;
            -fx-border-width: 1.4;
            -fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.34), 14, 0.28, 0, 0);
            -fx-cursor: hand;
            """;

    @FXML private Label appTitleLabel;
    @FXML private Label headerSubtitleLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label employeesCardTitle;
    @FXML private Label employeesCardText;
    @FXML private Label contractsCardTitle;
    @FXML private Label contractsCardText;
    @FXML private Label salariesCardTitle;
    @FXML private Label salariesCardText;
    @FXML private Button accessButton;
    @FXML private Button albanianButton;
    @FXML private Button englishButton;

    @FXML
    public void initialize() {
        updateTexts();
        setupKeyboardAccess();
        setupHoverEffects();
    }

    private void updateTexts() {
        appTitleLabel.setText(LanguageManager.get("app.title"));
        welcomeLabel.setText(LanguageManager.get("app.welcome"));
        accessButton.setText(LanguageManager.get("welcome.enter"));
        headerSubtitleLabel.setText(LanguageManager.get("welcome.headerSubtitle"));
        subtitleLabel.setText(LanguageManager.get("welcome.subtitle"));
        employeesCardTitle.setText(LanguageManager.get("menu.employees"));
        employeesCardText.setText(LanguageManager.get("welcome.employeesCard"));
        contractsCardTitle.setText(LanguageManager.get("menu.contracts"));
        contractsCardText.setText(LanguageManager.get("welcome.contractsCard"));
        salariesCardTitle.setText(LanguageManager.get("menu.salaries"));
        salariesCardText.setText(LanguageManager.get("welcome.salariesCard"));
        albanianButton.setText("SQ");
        englishButton.setText("EN");
    }

    private void setupKeyboardAccess() {
        accessButton.setDefaultButton(true);
        accessButton.setAccessibleText("Open login page");
        albanianButton.setAccessibleText("Switch language to Albanian");
        englishButton.setAccessibleText("Switch language to English");

        // Tab order: SQ -> EN -> Enter (ciklik)
        albanianButton.setFocusTraversable(true);
        englishButton.setFocusTraversable(true);
        accessButton.setFocusTraversable(true);

        // Enter ose Space aktivizon butonat kur jane ne fokus
        albanianButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                albanianButton.fire();
            }
        });
        englishButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                englishButton.fire();
            }
        });
        accessButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                accessButton.fire();
            }
        });

        accessButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        accessButton.fire();
                        event.consume();
                    }
                });
            }
        });
    }

    private void setupHoverEffects() {
        albanianButton.setStyle(LANGUAGE_BUTTON_STYLE);
        englishButton.setStyle(LANGUAGE_BUTTON_STYLE);
        accessButton.setStyle(ACCESS_BUTTON_STYLE);

        albanianButton.setOnMouseEntered(e -> albanianButton.setStyle(LANGUAGE_BUTTON_HOVER_STYLE));
        albanianButton.setOnMouseExited(e -> albanianButton.setStyle(albanianButton.isFocused() ? LANGUAGE_BUTTON_FOCUS_STYLE : LANGUAGE_BUTTON_STYLE));
        albanianButton.focusedProperty().addListener((obs, oldValue, focused) ->
                albanianButton.setStyle(focused ? LANGUAGE_BUTTON_FOCUS_STYLE : LANGUAGE_BUTTON_STYLE));

        englishButton.setOnMouseEntered(e -> englishButton.setStyle(LANGUAGE_BUTTON_HOVER_STYLE));
        englishButton.setOnMouseExited(e -> englishButton.setStyle(englishButton.isFocused() ? LANGUAGE_BUTTON_FOCUS_STYLE : LANGUAGE_BUTTON_STYLE));
        englishButton.focusedProperty().addListener((obs, oldValue, focused) ->
                englishButton.setStyle(focused ? LANGUAGE_BUTTON_FOCUS_STYLE : LANGUAGE_BUTTON_STYLE));

        accessButton.setOnMouseEntered(e -> accessButton.setStyle(ACCESS_BUTTON_HOVER_STYLE));
        accessButton.setOnMouseExited(e -> accessButton.setStyle(accessButton.isFocused() ? ACCESS_BUTTON_FOCUS_STYLE : ACCESS_BUTTON_STYLE));
        accessButton.focusedProperty().addListener((obs, oldValue, focused) ->
                accessButton.setStyle(focused ? ACCESS_BUTTON_FOCUS_STYLE : ACCESS_BUTTON_STYLE));
    }

    @FXML private void openLogin() { MainApp.showLogin(); }

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
