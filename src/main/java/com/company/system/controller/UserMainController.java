package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import com.company.system.model.Contract;
import com.company.system.model.Department;
import com.company.system.model.Employee;
import com.company.system.model.Salary;
import com.company.system.model.User;
import com.company.system.service.ContractService;
import com.company.system.service.DepartmentService;
import com.company.system.service.EmployeeService;
import com.company.system.service.SalaryService;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.PasswordUtils;
import com.company.system.utils.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class UserMainController {

    private static final double FOOTER_ICON_SCALE = 0.86;

    private static final String ICON_LANGUAGE = "M4 4 H13 V7 H11 C10.7 8.4 10.12 9.69 9.25 10.83 C10 11.45 10.9 12.04 12 12.56 L11 14.3 C9.9 13.76 8.93 13.13 8.08 12.43 C7.08 13.25 5.83 14.08 4.3 14.9 L3.35 13.22 C4.73 12.52 5.85 11.82 6.74 11.12 C6.14 10.45 5.62 9.72 5.17 8.92 L6.88 8.05 C7.2 8.6 7.57 9.1 8 9.57 C8.55 8.82 8.94 7.97 9.18 7 H4 Z M15 10 H17 L21 20 H18.9 L18.1 18 H13.9 L13.1 20 H11 Z M14.58 16.2 H17.42 L16 12.55 Z";
    private static final String ICON_HELP = "M12 2 C6.48 2 2 6.48 2 12 C2 17.52 6.48 22 12 22 C17.52 22 22 17.52 22 12 C22 6.48 17.52 2 12 2 Z M11 18 H13 V16 H11 Z M12 6 C9.79 6 8 7.79 8 10 H10 C10 8.9 10.9 8 12 8 C13.1 8 14 8.9 14 10 C14 12 11 11.75 11 15 H13 C13 12.75 16 12.5 16 10 C16 7.79 14.21 6 12 6 Z";
    private static final String ICON_SUN = "M12 4 V2 M12 22 V20 M4.93 4.93 L3.52 3.52 M20.48 20.48 L19.07 19.07 M4 12 H2 M22 12 H20 M4.93 19.07 L3.52 20.48 M20.48 3.52 L19.07 4.93 M12 7 C9.24 7 7 9.24 7 12 C7 14.76 9.24 17 12 17 C14.76 17 17 14.76 17 12 C17 9.24 14.76 7 12 7 Z";
    private static final String ICON_MOON = "M21 12.79 C20.16 13.05 19.28 13.18 18.36 13.18 C14.2 13.18 10.82 9.8 10.82 5.64 C10.82 4.72 10.95 3.84 11.21 3 C6.56 3.45 3 7.36 3 12.12 C3 17.07 6.93 21 11.88 21 C16.64 21 20.55 17.44 21 12.79 Z";

    @FXML private BorderPane mainShell;
    @FXML private VBox sidebar;
    @FXML private HBox expandedFooter;
    @FXML private Button menuToggleButton;
    @FXML private Button dashboardButton;
    @FXML private Button contractButton;
    @FXML private Button salaryButton;
    @FXML private Button departmentButton;
    @FXML private Button settingsButton;
    @FXML private Button languageButton;
    @FXML private Button helpFooterButton;
    @FXML private Button themeButton;
    @FXML private StackPane contentArea;
    @FXML private Label appTitleLabel;
    @FXML private Label loggedInLabel;
    @FXML private Label statusLabel;

    private String currentView = "dashboard";
    private boolean sidebarVisible = true;
    private boolean darkMode = false;
    private boolean profileLanguageChanged = false;

    @FXML
    public void initialize() {
        mainShell.getStyleClass().add("light");
        refreshTexts();
        updateLoggedInUser();
        showDashboard();
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    private String t(String en, String sq) {
        return isAlbanian() ? sq : en;
    }

    private void refreshTexts() {
        if (appTitleLabel != null) {
            appTitleLabel.setText("Contract Manager");
        }

        dashboardButton.setText(t("Dashboard", "Dashboard"));
        contractButton.setText(t("My Contract", "My Contract"));
        salaryButton.setText(t("My Salary", "My Salary"));
        departmentButton.setText(t("My Department", "My Department"));
        settingsButton.setText(t("Settings", "Settings"));

        setIconOnlyButton(languageButton, ICON_LANGUAGE);
        setIconOnlyButton(helpFooterButton, ICON_HELP);
        setIconOnlyButton(themeButton, darkMode ? ICON_SUN : ICON_MOON);

        menuToggleButton.setText("☰");

        updateStatusText();
    }

    private void updateStatusText() {
        switch (currentView) {
            case "dashboard" -> setStatus(t("Dashboard opened", "Dashboard u hap"));
            case "contract" -> setStatus(t("My Contract opened", "My Contract u hap"));
            case "salary" -> setStatus(t("My Salary opened", "My Salary u hap"));
            case "department" -> setStatus(t("My Department opened", "My Department u hap"));
            case "settings" -> setStatus(t("Settings opened", "Settings u hapen"));
            case "help" -> setStatus(t("Help opened", "Ndihma u hap"));
            default -> setStatus(t("Ready", "Gati"));
        }
    }

    private void updateLoggedInUser() {
        User user = Session.getUser();
        String username = user != null ? user.getUsername() : "-";
        if (loggedInLabel != null) {
            loggedInLabel.setText(t("Logged in as: ", "I kycur si: ") + username);
        }
    }

    @FXML
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebar.setVisible(sidebarVisible);
        sidebar.setManaged(sidebarVisible);
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
        mainShell.getStyleClass().removeAll("light", "dark");
        mainShell.getStyleClass().add(darkMode ? "dark" : "light");
        refreshTexts();
    }

    @FXML
    private void showDashboard() {
        currentView = "dashboard";
        refreshTexts();
        setContent(loadView("/views/user-dashboard-view.fxml"));
    }

    @FXML
    private void showContract() {
        currentView = "contract";
        refreshTexts();
        setContent(buildContractView());
    }

    @FXML
    private void showSalary() {
        currentView = "salary";
        refreshTexts();
        setContent(buildSalaryView());
    }

    @FXML
    private void showDepartment() {
        currentView = "department";
        refreshTexts();
        setContent(buildDepartmentView());
    }

    @FXML
    private void showSettings() {
        currentView = "settings";
        refreshTexts();
        setContent(buildSettingsView());
    }

    @FXML
    private void showHelp() {
        currentView = "help";
        refreshTexts();
        setContent(buildHelpView());
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        MainApp.showWelcome();
    }

    private void refreshCurrentView() {
        switch (currentView) {
            case "dashboard" -> showDashboard();
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

    private Node loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return simplePlaceholder("Failed to load view.");
        }
    }

    private Node buildContractView() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            return simplePlaceholder(t("No contract data found.", "Nuk u gjeten te dhena te kontrates."));
        }

        Contract contract = ContractService.getLatestContractByEmployeeId(user.getEmployeeId());

        VBox root = createPageContainer(
                t("My Contract", "My Contract"),
                t("Your latest contract details.", "Detajet e kontrates suaj me te fundit.")
        );

        if (contract == null) {
            root.getChildren().add(simpleCard(t("No contract found.", "Nuk u gjet kontrate.")));
            return wrap(root);
        }

        root.getChildren().add(simpleCard(
                t("Type", "Lloji") + ": " + valueOrDash(contract.getContractType()),
                t("Start date", "Data e fillimit") + ": " + formatSqlDate(contract.getStartDate()),
                t("End date", "Data e mbarimit") + ": " + formatSqlDate(contract.getEndDate()),
                t("Status", "Statusi") + ": " + valueOrDash(contract.getStatus()),
                t("Contract salary", "Paga e kontrates") + ": " + formatCurrency(contract.getSalary())
        ));

        return wrap(root);
    }

    private Node buildSalaryView() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            return simplePlaceholder(t("No salary data found.", "Nuk u gjeten te dhena te pages."));
        }

        Salary salary = SalaryService.getLatestSalaryByEmployeeId(user.getEmployeeId());

        VBox root = createPageContainer(
                t("My Salary", "My Salary"),
                t("Your latest salary summary.", "Permbledhja e pages suaj me te fundit.")
        );

        if (salary == null) {
            root.getChildren().add(simpleCard(t("No salary record found.", "Nuk u gjet page.")));
            return wrap(root);
        }

        root.getChildren().add(simpleCard(
                t("Gross salary", "Paga bruto") + ": " + formatCurrency(salary.getGrossSalary()),
                t("Bonus", "Bonusi") + ": " + formatCurrency(salary.getBonus()),
                t("Deductions", "Zbritjet") + ": " + formatCurrency(salary.getDeductions()),
                t("Net salary", "Paga neto") + ": " + formatCurrency(salary.getNetSalary()),
                t("Payment date", "Data e pageses") + ": " + formatSqlDate(salary.getPaymentDate())
        ));

        return wrap(root);
    }

    private Node buildDepartmentView() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            return simplePlaceholder(t("No department data found.", "Nuk u gjeten te dhena te departamentit."));
        }

        Employee employee = EmployeeService.getEmployeeById(user.getEmployeeId());
        if (employee == null) {
            return simplePlaceholder(t("Employee not found.", "Punetori nuk u gjet."));
        }

        Department department = DepartmentService.getDepartmentById(employee.getDepartmentId());
        List<Employee> colleagues = EmployeeService.getEmployeesByDepartment(employee.getDepartmentId(), employee.getId());

        VBox root = createPageContainer(
                t("My Department", "My Department"),
                t("People from your department.", "Personat nga departamenti yt.")
        );

        root.getChildren().add(simpleCard(
                t("Department", "Departamenti") + ": " + valueOrDash(department == null ? null : department.getName()),
                t("Description", "Pershkrimi") + ": " + valueOrDash(department == null ? null : department.getDescription()),
                t("Colleagues", "Koleget") + ": " + colleagues.size()
        ));

        VBox colleagueBox = new VBox(8);
        colleagueBox.getStyleClass().add("content-card");
        colleagueBox.setPadding(new Insets(18));

        Label colleaguesTitle = new Label(t("Colleagues", "Koleget"));
        colleaguesTitle.getStyleClass().add("section-title");

        colleagueBox.getChildren().add(colleaguesTitle);

        if (colleagues.isEmpty()) {
            colleagueBox.getChildren().add(simpleLabel(t("No colleagues found in this department.", "Nuk u gjeten kolege ne kete departament.")));
        } else {
            for (Employee colleague : colleagues) {
                colleagueBox.getChildren().add(simpleLabel(
                        colleague.getFirstName() + " " + colleague.getLastName() + " - " + colleague.getPosition()
                ));
            }
        }

        root.getChildren().add(colleagueBox);
        return wrap(root);
    }

    private Node buildSettingsView() {
        User user = Session.getUser();

        VBox root = createPageContainer(
                t("Settings", "Settings"),
                t("Change language, theme or logout.", "Ndrysho gjuhen, temen ose dil nga sistemi.")
        );

        VBox card = new VBox(12);
        card.getStyleClass().add("content-card");
        card.setPadding(new Insets(18));

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll(LanguageManager.get("language.english"), LanguageManager.get("language.albanian"));
        languageBox.setValue(isAlbanian() ? LanguageManager.get("language.albanian") : LanguageManager.get("language.english"));

        Label languageMessage = new Label();
        languageMessage.getStyleClass().add("success-text");

        languageBox.setOnAction(event -> {
            String selected = languageBox.getValue();
            if (LanguageManager.get("language.albanian").equals(selected)) {
                LanguageManager.setLanguage("sq");
            } else {
                LanguageManager.setLanguage("en");
            }
            profileLanguageChanged = true;
            refreshTexts();
            showSettings();
        });

        if (profileLanguageChanged) {
            languageMessage.setText(t("Language changed successfully!", "Gjuha u ndryshua me sukses!"));
            profileLanguageChanged = false;
        }

        Button logoutButton = new Button(t("Logout", "Logout"));
        logoutButton.getStyleClass().add("secondary-button");
        logoutButton.setOnAction(e -> handleLogout());

        card.getChildren().addAll(
                simpleLabel(t("Language", "Gjuha")),
                languageBox,
                languageMessage,
                simpleLabel(t("Theme", "Tema")),
                simpleLabel(t("Use the theme button in the footer.", "Përdor butonin e temës poshtë.")),
                logoutButton
        );

        root.getChildren().add(card);

        if (user != null) {
            root.getChildren().add(simpleCard(
                    t("Username", "Emri i perdoruesit") + ": " + valueOrDash(user.getUsername()),
                    t("Role", "Roli") + ": " + valueOrDash(user.getRole()),
                    t("Created at", "Krijuar me") + ": " + formatCreatedAt(user.getCreatedAt())
            ));
        }

        VBox passwordCard = new VBox(12);
        passwordCard.getStyleClass().add("content-card");
        passwordCard.setPadding(new Insets(18));

        Label passwordTitle = new Label(isAlbanian() ? "Change password" : "Change password");
        passwordTitle.getStyleClass().add("section-title");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText(isAlbanian() ? "Current password" : "Current password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText(isAlbanian() ? "New password" : "New password");

        Button changePasswordButton = new Button(isAlbanian() ? "Change password" : "Change password");
        changePasswordButton.getStyleClass().add("primary-button");
        changePasswordButton.setOnAction(e -> changePassword(user, currentPasswordField, newPasswordField));

        passwordCard.getChildren().addAll(passwordTitle, currentPasswordField, newPasswordField, changePasswordButton);
        root.getChildren().add(passwordCard);

        return wrap(root);
    }

    private Node buildHelpView() {
        VBox root = createPageContainer(
                t("Help", "Ndihma"),
                t("Use the sidebar to navigate your personal pages.", "Përdor sidebar-in për me lëvizë në faqet personale.")
        );

        root.getChildren().add(simpleCard(
                t("Dashboard shows your summary.", "Dashboard shfaq përmbledhjen tënde."),
                t("My Contract shows contract details.", "My Contract shfaq detajet e kontratës."),
                t("My Salary shows salary details.", "My Salary shfaq detajet e pagës."),
                t("My Department shows colleagues.", "My Department shfaq kolegët."),
                t("Settings contains language and theme controls.", "Settings ka gjuhën dhe temën.")
        ));

        return wrap(root);
    }

    private VBox createPageContainer(String titleText, String subtitleText) {
        VBox root = new VBox(16);
        root.getStyleClass().add("profile-page");
        root.setPadding(new Insets(28));

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(subtitleText);
        subtitle.setWrapText(true);
        subtitle.getStyleClass().add("page-subtitle");

        root.getChildren().addAll(title, subtitle);
        return root;
    }

    private VBox simpleCard(String... lines) {
        VBox card = new VBox(8);
        card.getStyleClass().add("profile-card");
        card.setPadding(new Insets(18));

        for (String line : lines) {
            card.getChildren().add(simpleLabel(line));
        }

        return card;
    }

    private Label simpleLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("body-text");
        return label;
    }

    private Node simplePlaceholder(String text) {
        VBox box = createPageContainer(t("Placeholder", "Placeholder"), text);
        box.getChildren().add(simpleCard(text));
        return wrap(box);
    }

    private ScrollPane wrap(VBox root) {
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("module-scroll");
        return scroll;
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void setIconOnlyButton(Button button, String iconPath) {
        button.setText("");
        button.setGraphic(createSidebarIconBox(iconPath, FOOTER_ICON_SCALE));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER);
    }

    private StackPane createSidebarIconBox(String iconPath, double scale) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.getStyleClass().add("sidebar-svg-icon");
        icon.setScaleX(scale);
        icon.setScaleY(scale);

        StackPane box = new StackPane(icon);
        box.getStyleClass().add("sidebar-icon-box");
        return box;
    }

    private void changePassword(User user, PasswordField currentPasswordField, PasswordField newPasswordField) {
        if (user == null) {
            return;
        }

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String storedHash = UserService.getPasswordHashByUsername(user.getUsername());

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            showStyledAlert(Alert.AlertType.ERROR, isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Plotesoni te dy fushat e fjalekalimit." : "Fill both password fields.");
            return;
        }

        if (!PasswordUtils.verifyPassword(currentPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR, isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Fjalekalimi aktual nuk eshte i sakte." : "Current password is not correct.");
            return;
        }

        if (PasswordUtils.verifyPassword(newPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR, isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Fjalekalimi i ri nuk mund te jete i njejte me te vjetrin." : "New password cannot be the same as the old one.");
            return;
        }

        if (UserService.resetPassword(user.getUsername(), newPassword)) {
            currentPasswordField.clear();
            newPasswordField.clear();
            showStyledAlert(Alert.AlertType.INFORMATION, isAlbanian() ? "Sukses" : "Success",
                    isAlbanian() ? "Fjalekalimi u ndryshua me sukses." : "Password changed successfully.");
        } else {
            showStyledAlert(Alert.AlertType.ERROR, isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Fjalekalimi nuk u ndryshua." : "Password was not changed.");
        }
    }

    private void showStyledAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        DialogUtils.style(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(createWrappedDialogLabel(message, 380));
        alert.getDialogPane().setPrefWidth(460);
        alert.showAndWait();
    }

    private Label createWrappedDialogLabel(String message, double width) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(width);
        label.setMinHeight(Label.USE_PREF_SIZE);
        label.getStyleClass().add("body-text");
        return label;
    }

    private String formatSqlDate(Date date) {
        if (date == null) return "-";
        return date.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatCreatedAt(Timestamp createdAt) {
        if (createdAt == null) return "-";
        return createdAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String formatCurrency(double value) {
        return String.format("%.2f EUR", value);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}