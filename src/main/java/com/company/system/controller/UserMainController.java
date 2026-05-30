package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

import java.time.format.DateTimeFormatter;

public class UserMainController {

    private String currentView = "dashboard";
    private boolean darkMode = false;
    private boolean sidebarVisible = true;

    @FXML private BorderPane mainShell;
    @FXML private HBox topBar;
    @FXML private VBox sidebar;
    @FXML private HBox expandedFooter;
    @FXML private HBox statusBar;
    @FXML private StackPane contentArea;

    @FXML private Button menuToggleButton;
    @FXML private Button languageButton;
    @FXML private Button helpFooterButton;
    @FXML private Button themeButton;

    @FXML private Button dashboardButton;
    @FXML private Button contractButton;
    @FXML private Button salaryButton;
    @FXML private Button departmentButton;
    @FXML private Button settingsButton;

    @FXML private Label appTitleLabel;
    @FXML private Label loggedInLabel;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        refreshTexts();
        applyTheme();
        showDashboard();
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    private String t(String en, String sq) {
        return isAlbanian() ? sq : en;
    }

    private void refreshTexts() {
        User user = Session.getUser();
        String username = user != null ? user.getUsername() : "Guest";

        appTitleLabel.setText("Contract Manager");
        loggedInLabel.setText(t("Logged in as: ", "I kycur si: ") + username);

        dashboardButton.setText(t("Dashboard", "Paneli"));
        contractButton.setText(t("My Contract", "Kontrata ime"));
        salaryButton.setText(t("My Salary", "Paga ime"));
        departmentButton.setText(t("My Department", "Departamenti im"));
        settingsButton.setText(t("Settings", "Cilesimet"));

        languageButton.setText(isAlbanian() ? "SQ" : "EN");
        helpFooterButton.setText("?");
        themeButton.setText(darkMode ? "☼" : "☾");
        menuToggleButton.setText("☰");

        refreshStatusText();
    }

    private void refreshStatusText() {
        if ("dashboard".equals(currentView)) {
            setStatus(t("Dashboard opened", "Paneli u hap"));
        } else if ("contract".equals(currentView)) {
            setStatus(t("My Contract opened", "Kontrata ime u hap"));
        } else if ("salary".equals(currentView)) {
            setStatus(t("My Salary opened", "Paga ime u hap"));
        } else if ("department".equals(currentView)) {
            setStatus(t("My Department opened", "Departamenti im u hap"));
        } else if ("settings".equals(currentView)) {
            setStatus(t("Settings opened", "Cilesimet u hapen"));
        } else if ("help".equals(currentView)) {
            setStatus(t("Help opened", "Ndihma u hap"));
        } else {
            setStatus(t("Ready", "Gati"));
        }
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    @FXML
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        if (sidebar != null) {
            sidebar.setVisible(sidebarVisible);
            sidebar.setManaged(sidebarVisible);
        }
    }

    @FXML
    private void toggleLanguage() {
        LanguageManager.setLanguage(isAlbanian() ? "en" : "sq");
        refreshTexts();
        refreshCurrentView();
    }

    @FXML
    private void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
        refreshTexts();
        updateNavStyles();
    }

    private void applyTheme() {
        if (mainShell == null) {
            return;
        }

        if (darkMode) {
            mainShell.setStyle("-fx-background-color: #0f172a;");
            topBar.setStyle("-fx-background-color: linear-gradient(to right, #071f46, #0a8fa5); -fx-min-height: 96px; -fx-pref-height: 96px;");
            sidebar.setStyle("-fx-background-color: #0b2b54; -fx-border-color: #071f46; -fx-border-width: 0 1 0 0;");
            contentArea.setStyle("-fx-background-color: #111827;");
            statusBar.setStyle("-fx-background-color: linear-gradient(to right, #071f46, #0a8fa5);");
        } else {
            mainShell.setStyle("-fx-background-color: #f4f6f8;");
            topBar.setStyle("-fx-background-color: linear-gradient(to right, #0d2f66, #0eb3cf); -fx-min-height: 96px; -fx-pref-height: 96px;");
            sidebar.setStyle("-fx-background-color: #123b72; -fx-border-color: #0b2b54; -fx-border-width: 0 1 0 0;");
            contentArea.setStyle("-fx-background-color: #f4f6f8;");
            statusBar.setStyle("-fx-background-color: linear-gradient(to right, #0d2f66, #0eb3cf);");
        }
    }

    @FXML
    private void showHelp() {
        currentView = "help";
        refreshTexts();
        updateNavStyles();
        setContent(buildHelpView());
    }

    @FXML
    private void showDashboard() {
        currentView = "dashboard";
        refreshTexts();
        updateNavStyles();
        setContent(buildDashboardView());
    }

    @FXML
    private void showContract() {
        currentView = "contract";
        refreshTexts();
        updateNavStyles();
        setContent(buildSectionView(
                t("My Contract", "Kontrata ime"),
                t("This is a placeholder for your contract details.", "Ky eshte placeholder per te dhenat e kontrates."),
                t("Contract type", "Lloji i kontrates"),
                t("Start date", "Data e fillimit"),
                t("End date", "Data e perfundimit"),
                t("Status", "Statusi")
        ));
    }

    @FXML
    private void showSalary() {
        currentView = "salary";
        refreshTexts();
        updateNavStyles();
        setContent(buildSectionView(
                t("My Salary", "Paga ime"),
                t("This is a placeholder for your salary details.", "Ky eshte placeholder per te dhenat e pages."),
                t("Base salary", "Paga baze"),
                t("Bonus", "Bonusi"),
                t("Deductions", "Zbritjet"),
                t("Net salary", "Paga neto")
        ));
    }

    @FXML
    private void showDepartment() {
        currentView = "department";
        refreshTexts();
        updateNavStyles();
        setContent(buildSectionView(
                t("My Department", "Departamenti im"),
                t("Your colleagues in the same department will appear here.", "Ketu do te shfaqen koleget ne te njejtin department."),
                t("Colleague list", "Lista e kolegeve"),
                t("Department summary", "Permbledhja e departmentit"),
                t("Team updates", "Perditesimet e ekipit")
        ));
    }

    @FXML
    private void showSettings() {
        currentView = "settings";
        refreshTexts();
        updateNavStyles();
        setContent(buildSettingsView());
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        MainApp.showWelcome();
    }

    private void refreshCurrentView() {
        switch (currentView) {
            case "contract" -> showContract();
            case "salary" -> showSalary();
            case "department" -> showDepartment();
            case "settings" -> showSettings();
            case "help" -> showHelp();
            default -> showDashboard();
        }
    }

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    private Node buildDashboardView() {
        User user = Session.getUser();
        String username = user != null ? user.getUsername() : "Guest";
        String role = user != null ? user.getRole() : "USER";
        String createdAt = user != null && user.getCreatedAt() != null
                ? user.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "-";

        VBox root = new VBox(18);
        root.setStyle("-fx-padding: 22; -fx-background-color: transparent;");

        Label title = new Label(t("Personal Dashboard", "Paneli personal"));
        title.setStyle(darkMode
                ? "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #eef7ff;"
                : "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #123b72;");

        Label subtitle = new Label(t(
                "A quick summary of your account and work details.",
                "Permbledhje e shkurter e llogarise dhe te dhenave te punes."
        ));
        subtitle.setStyle(darkMode
                ? "-fx-font-size: 14px; -fx-text-fill: #a8bed7;"
                : "-fx-font-size: 14px; -fx-text-fill: #5b7288;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        grid.add(card(t("Username", "Emri i perdoruesit"), username), 0, 0);
        grid.add(card(t("Role", "Roli"), role), 1, 0);
        grid.add(card(t("Created at", "Krijuar me"), createdAt), 2, 0);
        grid.add(card(t("Position", "Pozita"), t("Placeholder", "Placeholder")), 0, 1);
        grid.add(card(t("Contract", "Kontrata"), t("Placeholder", "Placeholder")), 1, 1);
        grid.add(card(t("Salary", "Paga"), t("Placeholder", "Placeholder")), 2, 1);

        VBox note = new VBox(8);
        note.setStyle(darkMode
                ? "-fx-background-color: #10223a; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #23496f; -fx-padding: 16;"
                : "-fx-background-color: #eef7ff; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #d6e8f5; -fx-padding: 16;");

        Label noteTitle = new Label(t("Note", "Shenim"));
        noteTitle.setStyle(darkMode
                ? "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #eef7ff;"
                : "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #123b72;");

        Label noteText = new Label(t(
                "This is a placeholder summary. Later we will link it to your employee record in the database.",
                "Ky eshte nje summary placeholder. Me vone do ta lidhim me rekordin e punetorit ne databaze."
        ));
        noteText.setWrapText(true);
        noteText.setStyle(darkMode
                ? "-fx-text-fill: #d9ecff;"
                : "-fx-text-fill: #415a77;");

        note.getChildren().addAll(noteTitle, noteText);

        root.getChildren().addAll(title, subtitle, grid, note);
        VBox.setVgrow(grid, Priority.NEVER);
        return root;
    }

    private Node buildSectionView(String sectionTitle, String description, String... items) {
        VBox root = new VBox(16);
        root.setStyle("-fx-padding: 22; -fx-background-color: transparent;");

        Label title = new Label(sectionTitle);
        title.setStyle(darkMode
                ? "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #eef7ff;"
                : "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #123b72;");

        Label desc = new Label(description);
        desc.setWrapText(true);
        desc.setStyle(darkMode
                ? "-fx-font-size: 14px; -fx-text-fill: #a8bed7;"
                : "-fx-font-size: 14px; -fx-text-fill: #5b7288;");

        VBox box = new VBox(10);
        box.setStyle(darkMode
                ? "-fx-background-color: #10223a; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #23496f; -fx-padding: 18;"
                : "-fx-background-color: #f8fbff; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #d6e8f5; -fx-padding: 18;");

        for (String item : items) {
            Label l = new Label("• " + item);
            l.setStyle(darkMode
                    ? "-fx-font-size: 14px; -fx-text-fill: #d9ecff;"
                    : "-fx-font-size: 14px; -fx-text-fill: #334e68;");
            l.setWrapText(true);
            box.getChildren().add(l);
        }

        root.getChildren().addAll(title, desc, box);
        return root;
    }

    private Node buildHelpView() {
        VBox root = new VBox(16);
        root.setStyle("-fx-padding: 22; -fx-background-color: transparent;");

        Label title = new Label(t("Help", "Ndihma"));
        title.setStyle(darkMode
                ? "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #eef7ff;"
                : "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #123b72;");

        Label desc = new Label(t(
                "Use the sidebar to navigate through your personal pages.",
                "Përdorni sidebar-in për me lëvizë nëpër faqet tuaja personale."
        ));
        desc.setWrapText(true);
        desc.setStyle(darkMode
                ? "-fx-font-size: 14px; -fx-text-fill: #a8bed7;"
                : "-fx-font-size: 14px; -fx-text-fill: #5b7288;");

        VBox box = new VBox(10);
        box.setStyle(darkMode
                ? "-fx-background-color: #10223a; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #23496f; -fx-padding: 18;"
                : "-fx-background-color: #f8fbff; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #d6e8f5; -fx-padding: 18;");

        box.getChildren().addAll(
                helpLine(t("Dashboard shows your summary.", "Paneli shfaq përmbledhjen tënde.")),
                helpLine(t("My Contract shows contract details.", "My Contract shfaq detajet e kontratës.")),
                helpLine(t("My Salary shows salary details.", "My Salary shfaq detajet e pagës.")),
                helpLine(t("My Department shows colleagues in your department.", "My Department shfaq kolegët në departamentin tënd.")),
                helpLine(t("Settings contains language and theme toggles.", "Settings ka gjuhën dhe theme toggles."))
        );

        root.getChildren().addAll(title, desc, box);
        return root;
    }

    private Label helpLine(String text) {
        Label label = new Label("• " + text);
        label.setWrapText(true);
        label.setStyle(darkMode
                ? "-fx-font-size: 14px; -fx-text-fill: #d9ecff;"
                : "-fx-font-size: 14px; -fx-text-fill: #334e68;");
        return label;
    }

    private Node buildSettingsView() {
        VBox root = new VBox(16);
        root.setStyle("-fx-padding: 22; -fx-background-color: transparent;");

        Label title = new Label(t("Settings", "Cilesimet"));
        title.setStyle(darkMode
                ? "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #eef7ff;"
                : "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #123b72;");

        Label desc = new Label(t(
                "Use the footer buttons to switch language or theme.",
                "Përdorni butonat poshtë për me ndërru gjuhën ose temën."
        ));
        desc.setWrapText(true);
        desc.setStyle(darkMode
                ? "-fx-font-size: 14px; -fx-text-fill: #a8bed7;"
                : "-fx-font-size: 14px; -fx-text-fill: #5b7288;");

        VBox box = new VBox(12);
        box.setStyle(darkMode
                ? "-fx-background-color: #10223a; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #23496f; -fx-padding: 18;"
                : "-fx-background-color: #f8fbff; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #d6e8f5; -fx-padding: 18;");

        box.getChildren().addAll(
                new Label(t("Theme", "Tema")),
                new Label(t("Use the ☾ / ☼ button in the footer.", "Përdor butonin ☾ / ☼ poshtë.")),
                new Label(t("Language", "Gjuha")),
                new Label(t("Use the SQ / EN button in the footer.", "Përdor butonin SQ / EN poshtë."))
        );

        root.getChildren().addAll(title, desc, box);
        return root;
    }

    private VBox card(String title, String value) {
        VBox card = new VBox(6);
        card.setPrefWidth(180);
        card.setStyle(darkMode
                ? "-fx-background-color: #10223a; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #23496f; -fx-padding: 16;"
                : "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #d6e2ec; -fx-padding: 16;");

        Label t = new Label(title);
        t.setStyle(darkMode
                ? "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #d9ecff;"
                : "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #19316c;");

        Label v = new Label(value);
        v.setStyle(darkMode
                ? "-fx-font-size: 15px; -fx-text-fill: #eef7ff;"
                : "-fx-font-size: 15px; -fx-text-fill: #334e68;");
        v.setWrapText(true);

        card.getChildren().addAll(t, v);
        return card;
    }

    private void updateNavStyles() {
        styleNavButton(dashboardButton, "dashboard".equals(currentView));
        styleNavButton(contractButton, "contract".equals(currentView));
        styleNavButton(salaryButton, "salary".equals(currentView));
        styleNavButton(departmentButton, "department".equals(currentView));
        styleNavButton(settingsButton, "settings".equals(currentView));
    }

    private void styleNavButton(Button button, boolean active) {
        if (button == null) {
            return;
        }

        if (active) {
            button.setStyle("-fx-background-color: #5aa5f6; -fx-background-radius: 12; -fx-border-radius: 12; -fx-text-fill: #0b1f3f; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left; -fx-padding: 12 14 12 14;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-background-radius: 12; -fx-border-radius: 12; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left; -fx-padding: 12 14 12 14;");
        }
    }
}