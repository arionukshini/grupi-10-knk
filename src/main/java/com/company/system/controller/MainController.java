package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.PasswordUtils;
import com.company.system.utils.Session;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static com.company.system.MainApp.showWelcome;

public class MainController {

    private static final double EXPANDED_SIDEBAR_WIDTH = 220;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;
    private static final double NAV_ICON_SCALE = 0.88;
    private static final double FOOTER_ICON_SCALE = 0.86;

    private static final String ICON_DASHBOARD = "M3 3 H10 V10 H3 Z M14 3 H21 V7 H14 Z M14 11 H21 V21 H14 Z M3 14 H10 V21 H3 Z";
    private static final String ICON_EMPLOYEES = "M16 11 C18.21 11 20 9.21 20 7 C20 4.79 18.21 3 16 3 C13.79 3 12 4.79 12 7 C12 9.21 13.79 11 16 11 Z M8 11 C10.21 11 12 9.21 12 7 C12 4.79 10.21 3 8 3 C5.79 3 4 4.79 4 7 C4 9.21 5.79 11 8 11 Z M8 13 C5.33 13 0 14.34 0 17 V20 H16 V17 C16 14.34 10.67 13 8 13 Z M16 13 C15.69 13 15.34 13.02 14.97 13.05 C16.29 14 17 15.27 17 17 V20 H24 V17 C24 14.34 18.67 13 16 13 Z";
    private static final String ICON_CONTRACTS = "M6 2 H15 L20 7 V22 H6 Z M14 3.5 V8 H18.5 M8 12 H18 M8 16 H18 M8 20 H14";
    private static final String ICON_SALARIES = "M9 3 H15 L14 7 H10 Z M10 7 H14 C18 9 20 12.5 20 17 C20 20.31 16.42 22 12 22 C7.58 22 4 20.31 4 17 C4 12.5 6 9 10 7 Z M12 11 V18 M9.5 13 H13.2 C14.2 13 15 13.67 15 14.55 C15 15.42 14.2 16 13.2 16 H10.8 C9.8 16 9 16.58 9 17.45 C9 18.33 9.8 19 10.8 19 H14.5";
    private static final String ICON_DEPARTMENTS = "M3 21 V9 H9 V21 Z M10 21 V3 H16 V21 Z M17 21 V12 H21 V21 Z M5 12 H7 M12 6 H14 M12 10 H14 M12 14 H14 M19 15 H19";
    private static final String ICON_SETTINGS = "M19.43 12.98 C19.47 12.66 19.5 12.34 19.5 12 C19.5 11.66 19.47 11.34 19.43 11.02 L21.54 9.37 L19.54 5.91 L17.05 6.91 C16.54 6.52 15.99 6.2 15.38 5.95 L15 3.29 H11 L10.62 5.95 C10.01 6.2 9.46 6.52 8.95 6.91 L6.46 5.91 L4.46 9.37 L6.57 11.02 C6.53 11.34 6.5 11.66 6.5 12 C6.5 12.34 6.53 12.66 6.57 12.98 L4.46 14.63 L6.46 18.09 L8.95 17.09 C9.46 17.48 10.01 17.8 10.62 18.05 L11 20.71 H15 L15.38 18.05 C15.99 17.8 16.54 17.48 17.05 17.09 L19.54 18.09 L21.54 14.63 Z M13 15.5 C11.07 15.5 9.5 13.93 9.5 12 C9.5 10.07 11.07 8.5 13 8.5 C14.93 8.5 16.5 10.07 16.5 12 C16.5 13.93 14.93 15.5 13 15.5 Z";
    private static final String ICON_USER = "M12 12 C14.76 12 17 9.76 17 7 C17 4.24 14.76 2 12 2 C9.24 2 7 4.24 7 7 C7 9.76 9.24 12 12 12 Z M4 22 C4 17.58 7.58 14 12 14 C16.42 14 20 17.58 20 22 Z";
    private static final String ICON_LANGUAGE = "M4 4 H13 V7 H11 C10.7 8.4 10.12 9.69 9.25 10.83 C10 11.45 10.9 12.04 12 12.56 L11 14.3 C9.9 13.76 8.93 13.13 8.08 12.43 C7.08 13.25 5.83 14.08 4.3 14.9 L3.35 13.22 C4.73 12.52 5.85 11.82 6.74 11.12 C6.14 10.45 5.62 9.72 5.17 8.92 L6.88 8.05 C7.2 8.6 7.57 9.1 8 9.57 C8.55 8.82 8.94 7.97 9.18 7 H4 Z M15 10 H17 L21 20 H18.9 L18.1 18 H13.9 L13.1 20 H11 Z M14.58 16.2 H17.42 L16 12.55 Z";
    private static final String ICON_HELP = "M12 2 C6.48 2 2 6.48 2 12 C2 17.52 6.48 22 12 22 C17.52 22 22 17.52 22 12 C22 6.48 17.52 2 12 2 Z M11 18 H13 V16 H11 Z M12 6 C9.79 6 8 7.79 8 10 H10 C10 8.9 10.9 8 12 8 C13.1 8 14 8.9 14 10 C14 12 11 11.75 11 15 H13 C13 12.75 16 12.5 16 10 C16 7.79 14.21 6 12 6 Z";
    private static final String ICON_EXPORT = "M14 2 H6 C4.9 2 4 2.9 4 4 V20 C4 21.1 4.9 22 6 22 H18 C19.1 22 20 21.1 20 20 V8 Z M16 18 H8 V16 H16 Z M16 14 H8 V12 H16 Z M13 9 V3.5 L18.5 9 Z";
    private static final String ICON_EXIT = "M15 3 H5 C3.9 3 3 3.9 3 5 V19 C3 20.1 3.9 21 5 21 H15 M10 12 H21 M17 8 L21 12 L17 16";
    private static final String ICON_DELETE = "M3 6 H21 M8 6 V4 H16 V6 M6 6 L7 21 H17 L18 6 M10 10 V17 M14 10 V17";
    private static final String ICON_SUN = "M12 4 V2 M12 22 V20 M4.93 4.93 L3.52 3.52 M20.48 20.48 L19.07 19.07 M4 12 H2 M22 12 H20 M4.93 19.07 L3.52 20.48 M20.48 3.52 L19.07 4.93 M12 7 C9.24 7 7 9.24 7 12 C7 14.76 9.24 17 12 17 C14.76 17 17 14.76 17 12 C17 9.24 14.76 7 12 7 Z";
    private static final String ICON_MOON = "M21 12.79 C20.16 13.05 19.28 13.18 18.36 13.18 C14.2 13.18 10.82 9.8 10.82 5.64 C10.82 4.72 10.95 3.84 11.21 3 C6.56 3.45 3 7.36 3 12.12 C3 17.07 6.93 21 11.88 21 C16.64 21 20.55 17.44 21 12.79 Z";

    private final List<String> themeClasses = List.of("light", "dark");
    private final Deque<String> backHistory = new ArrayDeque<>();
    private final Deque<String> forwardHistory = new ArrayDeque<>();

    private String currentView = "welcome";
    private boolean sidebarExpanded = true;
    private boolean darkMode = false;
    private boolean profileLanguageChanged = false;
    private boolean navigatingHistory = false;
    private Timeline sidebarAnimation;

    @FXML private BorderPane mainShell;
    @FXML private VBox sidebar;
    @FXML private HBox expandedFooter;
    @FXML private Button menuToggleButton;
    @FXML private Button dashboardButton;
    @FXML private Button employeesButton;
    @FXML private Button contractsButton;
    @FXML private Button salariesButton;
    @FXML private Button departmentsButton;
    @FXML private Button usersButton;
    @FXML private Button exportButton;
    @FXML private Button profileButton;
    @FXML private Button languageButton;
    @FXML private Button themeButton;
    @FXML private Button helpFooterButton;
    @FXML private Button themeButton2;
    @FXML private StackPane contentArea;
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label loggedInLabel;

    @FXML
    public void initialize() {
        mainShell.getStyleClass().add("light");
        updateTexts();
        updateLoggedInUser();
        updateThemeButton();
        updateSidebarLabels();
        setStatus(LanguageManager.get("status.ready"));
        setupKeyboardShortcuts();
        setupMouseNavigation();
        setupContextMenu();
        applyRolePermissions();
        showDashboard();
        checkExpiringContractsPopup();
    }

    @FXML
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        updateSidebarState();
    }

    @FXML
    private void toggleTheme() {
        darkMode = !darkMode;
        mainShell.getStyleClass().removeAll(themeClasses);
        mainShell.getStyleClass().add(darkMode ? "dark" : "light");
        updateThemeButton();
    }

    @FXML
    private void toggleLanguage() {
        if (isAlbanian()) {
            switchToEnglish();
        } else {
            switchToAlbanian();
        }
    }

    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> contentArea.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShortcutDown() && event.getCode() == KeyCode.D) {
                showDashboard(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.E) {
                showEmployees(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.K) {
                showContracts(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.S) {
                showSalaries(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.R) {
                showDepartments(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.U) {
                showUsers(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.P) {
                showProfile(); event.consume();
            } else if (event.isAltDown() && event.getCode() == KeyCode.LEFT) {
                goBack(); event.consume();
            } else if (event.isAltDown() && event.getCode() == KeyCode.RIGHT) {
                goForward(); event.consume();
            } else if (event.getCode() == KeyCode.F5) {
                refreshCurrentView(); event.consume();
            } else if (event.getCode() == KeyCode.F1 || (event.isShortcutDown() && event.getCode() == KeyCode.H)) {
                showHelp(); event.consume();
            } else if (KeyCombination.keyCombination("Shortcut+L").match(event)) {
                toggleLanguage(); event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                handleExit(); event.consume();
            }
        }));
    }

    private void setupMouseNavigation() {
        Platform.runLater(() -> contentArea.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.BACK) {
                goBack(); event.consume();
            } else if (event.getButton() == MouseButton.FORWARD) {
                goForward(); event.consume();
            }
        }));
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("app-context-menu");
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);

        MenuItem refreshItem = new MenuItem(LanguageManager.get("context.refresh"));
        refreshItem.setOnAction(event -> refreshCurrentView());

        MenuItem helpItem = new MenuItem(LanguageManager.get("menu.help"));
        helpItem.setOnAction(event -> showHelp());

        MenuItem exitItem = new MenuItem(LanguageManager.get("context.exitProgram"));
        exitItem.setOnAction(event -> handleExit());

        contextMenu.getItems().setAll(refreshItem, helpItem, new SeparatorMenuItem(), exitItem);

        Platform.runLater(() -> contentArea.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (contextMenu.isShowing() && event.getButton() != MouseButton.SECONDARY) {
                contextMenu.hide();
            }
        }));

        mainShell.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            refreshItem.setText(LanguageManager.get("context.refresh"));
            helpItem.setText(LanguageManager.get("menu.help"));
            exitItem.setText(LanguageManager.get("context.exitProgram"));
            if (contextMenu.isShowing()) contextMenu.hide();
            contextMenu.show(mainShell, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private void checkExpiringContracts() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) return;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) return;

        List<com.company.system.model.Contract> expiring =
                com.company.system.service.ContractService.getExpiringContractsForEmployee(user.getEmployeeId());

        if (expiring.isEmpty()) return;

        boolean sq = isAlbanian();
        StringBuilder message = new StringBuilder();
        message.append(LanguageManager.get("contract.expiring.intro"));

        for (com.company.system.model.Contract c : expiring) {
            message.append(LanguageManager.get("contract.expiring.itemPrefix"))
                    .append(c.getContractType())
                    .append(LanguageManager.get("contract.expiring.statusSuffix"))
                    .append(LanguageManager.get("contract.expiring.expires")).append(": ").append(c.getEndDate()).append("\n");}

        message.append(LanguageManager.get("contract.expiring.footer"));


        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogUtils.style(alert);
            alert.setTitle(LanguageManager.get("contract.expiring.alertTitle"));
            alert.setHeaderText(LanguageManager.get("contract.expiring.alertHeader"));
            Label content = new Label(message.toString());
            content.setWrapText(true);
            content.setMaxWidth(400);
            content.setStyle("-fx-font-size: 13px;");
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefWidth(480);
            alert.showAndWait();
        });
    }

    private void checkExpiringContractsPopup() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) return;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) return;

        List<com.company.system.model.Contract> expiring =
                com.company.system.service.ContractService.getExpiringContractsForEmployee(user.getEmployeeId());

        if (expiring.isEmpty()) return;

        boolean sq = isAlbanian();
        StringBuilder message = new StringBuilder();
        message.append(sq
                ? "Kontratat tuaja te meposhtme do te skadojne brenda 14 diteve:\n\n"
                : "The following contracts will expire within 14 days:\n\n");

        for (com.company.system.model.Contract contract : expiring) {
            message.append("- ").append(contract.getContractType())
                    .append(" - ").append(sq ? "Skadon" : "Expires")
                    .append(": ").append(contract.getEndDate()).append("\n");
        }

        message.append(sq
                ? "\nJu lutem kontaktoni administratorin per rinovim."
                : "\nPlease contact the administrator for renewal.");

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogUtils.style(alert);
            alert.setTitle(sq ? "Paralajmerim - Kontrata" : "Warning - Contract");
            alert.setHeaderText(sq ? "Kontrata juaj po skadon!" : "Your contract is expiring!");
            Label content = new Label(message.toString());
            content.setWrapText(true);
            content.setMaxWidth(400);
            content.setStyle("-fx-font-size: 13px;");
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefWidth(480);
            alert.showAndWait();
        });
    }

    private void applyRolePermissions() {
        User user = Session.getUser();
        if (user == null) return;

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        contractsButton.setVisible(isAdmin);
        contractsButton.setManaged(isAdmin);
        salariesButton.setVisible(isAdmin);
        salariesButton.setManaged(isAdmin);
        usersButton.setVisible(isAdmin);
        usersButton.setManaged(isAdmin);
        exportButton.setVisible(isAdmin);
        exportButton.setManaged(isAdmin);
    }

    private void updateTexts() {
        dashboardButton.setUserData(new NavItem(ICON_DASHBOARD, LanguageManager.get("menu.dashboard")));
        employeesButton.setUserData(new NavItem(ICON_EMPLOYEES, LanguageManager.get("menu.employees")));
        contractsButton.setUserData(new NavItem(ICON_CONTRACTS, LanguageManager.get("menu.contracts")));
        salariesButton.setUserData(new NavItem(ICON_SALARIES, LanguageManager.get("menu.salaries")));
        departmentsButton.setUserData(new NavItem(ICON_DEPARTMENTS, LanguageManager.get("menu.departments")));
        usersButton.setUserData(new NavItem(ICON_USER, LanguageManager.get("menu.users")));
        exportButton.setUserData(new NavItem(ICON_EXPORT, isAlbanian() ? "Eksporto" : "Export"));
        profileButton.setUserData(new NavItem(ICON_SETTINGS, LanguageManager.get("menu.profile")));

        welcomeLabel.setText(LanguageManager.get("app.welcome"));
        setIconOnlyButton(languageButton, ICON_LANGUAGE);
        setIconOnlyButton(helpFooterButton, ICON_HELP);
        setIconOnlyButton(themeButton2, ICON_SETTINGS);
        updateSidebarLabels();
        updateLoggedInUser();

        if ("welcome".equals(currentView)) {
            setStatus(LanguageManager.get("status.ready"));
        }
    }

    private void updateLoggedInUser() {
        User user = Session.getUser();
        loggedInLabel.setText(LanguageManager.get("status.loggedInAs") + " " + UserService.getDisplayName(user));    }

    private void updateThemeButton() {
        setIconOnlyButton(themeButton, darkMode ? ICON_SUN : ICON_MOON);
        setIconOnlyButton(themeButton2, darkMode ? ICON_SUN : ICON_MOON);
    }

    private void updateSidebarState() {
        if (sidebarAnimation != null) sidebarAnimation.stop();

        double startWidth = sidebar.getWidth() > 0 ? sidebar.getWidth() : sidebar.getPrefWidth();
        double targetWidth = sidebarExpanded ? EXPANDED_SIDEBAR_WIDTH : COLLAPSED_SIDEBAR_WIDTH;

        sidebar.getStyleClass().remove("collapsed");
        menuToggleButton.setDisable(true);

        if (sidebarExpanded) {
            expandedFooter.setVisible(true);
            expandedFooter.setManaged(true);
            themeButton2.setVisible(false);
            themeButton2.setManaged(false);
        } else {
            sidebar.getStyleClass().add("collapsed");
            expandedFooter.setOpacity(1);
        }

        updateSidebarLabels();

        sidebarAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(sidebar.prefWidthProperty(), startWidth),
                        new KeyValue(sidebar.minWidthProperty(), startWidth),
                        new KeyValue(sidebar.maxWidthProperty(), startWidth),
                        new KeyValue(expandedFooter.opacityProperty(), sidebarExpanded ? 0 : 1)),
                new KeyFrame(Duration.millis(280),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.minWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.maxWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(expandedFooter.opacityProperty(), sidebarExpanded ? 1 : 0, Interpolator.EASE_BOTH))
        );

        sidebarAnimation.setOnFinished(event -> {
            sidebar.setPrefWidth(targetWidth);
            sidebar.setMinWidth(targetWidth);
            sidebar.setMaxWidth(targetWidth);
            expandedFooter.setVisible(sidebarExpanded);
            expandedFooter.setManaged(sidebarExpanded);
            expandedFooter.setOpacity(sidebarExpanded ? 1 : 0);
            themeButton2.setVisible(!sidebarExpanded);
            themeButton2.setManaged(!sidebarExpanded);
            menuToggleButton.setDisable(false);
            updateSidebarLabels();
        });

        sidebarAnimation.play();
    }

    private void updateSidebarLabels() {
        setNavButtonText(dashboardButton);
        setNavButtonText(employeesButton);
        setNavButtonText(contractsButton);
        setNavButtonText(salariesButton);
        setNavButtonText(departmentsButton);
        setNavButtonText(usersButton);
        setNavButtonText(exportButton);
        setNavButtonText(profileButton);
    }

    private void setNavButtonText(Button button) {
        if (!(button.getUserData() instanceof NavItem item)) return;
        button.setGraphic(createSidebarIconBox(item.iconPath(), NAV_ICON_SCALE));
        button.setText(sidebarExpanded ? item.label() : "");
        button.setContentDisplay(sidebarExpanded ? ContentDisplay.LEFT : ContentDisplay.GRAPHIC_ONLY);
        button.setGraphicTextGap(14);
        button.setAlignment(sidebarExpanded ? Pos.CENTER_LEFT : Pos.CENTER);
    }

    private void setIconOnlyButton(Button button, String iconPath) {
        button.setText("");
        button.setGraphic(createSidebarIconBox(iconPath, FOOTER_ICON_SCALE));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER);
    }

    private StackPane createSidebarIconBox(String iconPath, double scale) {
        StackPane iconBox = new StackPane(createSidebarIcon(iconPath, scale));
        iconBox.getStyleClass().add("sidebar-icon-box");
        return iconBox;
    }

    private SVGPath createSidebarIcon(String iconPath, double scale) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.getStyleClass().add("sidebar-svg-icon");
        icon.setScaleX(scale);
        icon.setScaleY(scale);
        return icon;
    }

    private StackPane createPopupIcon(String iconPath, String color) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.setStyle("-fx-fill: transparent; -fx-stroke: " + color + "; -fx-stroke-width: 2.2; -fx-stroke-line-cap: round; -fx-stroke-line-join: round;");
        icon.setScaleX(2.2);
        icon.setScaleY(2.2);

        StackPane box = new StackPane(icon);
        box.setMinSize(72, 72);
        box.setPrefSize(72, 72);
        box.setMaxSize(72, 72);
        return box;
    }

    private void setActiveButton(Button activeButton) {
        List<Button> buttons = List.of(dashboardButton, employeesButton, contractsButton,
                salariesButton, departmentsButton, usersButton, exportButton, profileButton);
        for (Button button : buttons) button.getStyleClass().remove("active");
        if (!activeButton.getStyleClass().contains("active")) activeButton.getStyleClass().add("active");
        activeButton.requestFocus();
    }

    private void clearActiveButton() {
        List<Button> buttons = List.of(dashboardButton, employeesButton, contractsButton,
                salariesButton, departmentsButton, usersButton, exportButton, profileButton);
        for (Button button : buttons) button.getStyleClass().remove("active");
    }

    @FXML private void switchToAlbanian() { LanguageManager.setLanguage("sq"); updateTexts(); refreshCurrentView(); }
    @FXML private void switchToEnglish() { LanguageManager.setLanguage("en"); updateTexts(); refreshCurrentView(); }

    private void refreshCurrentView() {
        switch (currentView) {
            case "employees" -> showEmployees();
            case "contracts" -> showContracts();
            case "salaries" -> showSalaries();
            case "dashboard" -> showDashboard();
            case "departments" -> showDepartments();
            case "users" -> showUsers();
            case "exports" -> showExports();
            case "help" -> showHelp();
            case "profile" -> showProfile();
            default -> { setContent(welcomeLabel); setStatus(LanguageManager.get("status.ready")); }
        }
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("exit.title"));
        alert.setHeaderText(null);
        ButtonType mainMenuType = new ButtonType(LanguageManager.get("exit.mainMenu"), ButtonBar.ButtonData.OTHER);
        ButtonType desktopType = new ButtonType(LanguageManager.get("exit.desktop"), ButtonBar.ButtonData.OTHER);
        ButtonType cancelType = new ButtonType(LanguageManager.get("exit.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(mainMenuType, desktopType, cancelType);

        Label icon = new Label("🚪");
        icon.setText("");
        icon.setGraphic(createPopupIcon(ICON_EXIT, "#3b82f6"));
        icon.setStyle("-fx-padding: 10;");

        Label title = new Label(LanguageManager.get("exit.confirmTitle"));
        title.setWrapText(true); title.setMaxWidth(360); title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitle = new Label(LanguageManager.get("exit.confirmSubtitle"));
        subtitle.setStyle("-fx-font-size: 13px; -fx-opacity: 0.8;");

        VBox content = new VBox(12, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);
        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(365);
        alert.getDialogPane().setContent(content);

        Platform.runLater(() -> {
            Button mainMenuBtn = (Button) alert.getDialogPane().lookupButton(mainMenuType);
            Button desktopBtn = (Button) alert.getDialogPane().lookupButton(desktopType);
            Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelType);

            mainMenuBtn.setMaxWidth(Double.MAX_VALUE); mainMenuBtn.setPrefHeight(42);
            desktopBtn.setMaxWidth(Double.MAX_VALUE); desktopBtn.setPrefHeight(42);
            cancelBtn.setMaxWidth(Double.MAX_VALUE); cancelBtn.setPrefHeight(42);

            mainMenuBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            desktopBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            cancelBtn.setStyle("-fx-background-radius: 10; -fx-cursor: hand;");

            VBox buttonBox = new VBox(10, mainMenuBtn, desktopBtn, cancelBtn);
            buttonBox.setAlignment(Pos.CENTER); buttonBox.setFillWidth(true);
            alert.getDialogPane().setContent(new VBox(18, content, buttonBox));
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == mainMenuType) showWelcome();
            else if (result.get() == desktopType) System.exit(0);
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        showWelcome();
    }

    public void setContent(Node node) { contentArea.getChildren().setAll(node); }
    public void setStatus(String message) { statusLabel.setText(message); }

    private void recordNavigation(String targetView) {
        if (navigatingHistory || targetView.equals(currentView)) return;
        if (!"welcome".equals(currentView)) backHistory.push(currentView);
        forwardHistory.clear();
    }

    private void goBack() {
        if (backHistory.isEmpty()) return;
        forwardHistory.push(currentView);
        navigateHistoryTo(backHistory.pop());
    }

    private void goForward() {
        if (forwardHistory.isEmpty()) return;
        backHistory.push(currentView);
        navigateHistoryTo(forwardHistory.pop());
    }

    private void navigateHistoryTo(String view) {
        navigatingHistory = true;
        try { showViewByName(view); } finally { navigatingHistory = false; }
    }

    private void showViewByName(String view) {
        switch (view) {
            case "employees" -> showEmployees();
            case "contracts" -> showContracts();
            case "salaries" -> showSalaries();
            case "dashboard" -> showDashboard();
            case "departments" -> showDepartments();
            case "users" -> showUsers();
            case "exports" -> showExports();
            case "help" -> showHelp();
            case "profile" -> showProfile();
            default -> showDashboard();
        }
    }

    @FXML public void showDashboard() {
        recordNavigation("dashboard"); currentView = "dashboard";
        setStatus(LanguageManager.get("status.dashboard"));

        User user = Session.getUser();
        if (user != null && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            loadView("/views/user-dashboard-view.fxml", dashboardButton, "Failed to load user dashboard.");
            return;
        }

        loadView("/views/dashboard-view.fxml", dashboardButton, "Failed to load dashboard.");
    }

    @FXML public void showEmployees() {
        recordNavigation("employees"); currentView = "employees";
        setStatus(LanguageManager.get("status.employees"));
        loadView("/views/employees-view.fxml", employeesButton, "Failed to load employees.");
    }

    @FXML public void showContracts() {
        recordNavigation("contracts"); currentView = "contracts";
        setStatus(LanguageManager.get("status.contracts"));
        loadView("/views/contracts-view.fxml", contractsButton, "Failed to load contracts.");
    }

    @FXML public void showSalaries() {
        recordNavigation("salaries"); currentView = "salaries";
        setStatus(LanguageManager.get("status.salaries"));
        loadView("/views/salaries-view.fxml", salariesButton, "Failed to load salaries.");
    }

    @FXML public void showDepartments() {
        recordNavigation("departments"); currentView = "departments";
        setStatus(LanguageManager.get("status.departments"));
        loadView("/views/departments-view.fxml", departmentsButton, "Failed to load departments.");
    }

    @FXML public void showUsers() {
        recordNavigation("users"); currentView = "users";
        setStatus(LanguageManager.get("status.users"));
        loadView("/views/users-view.fxml", usersButton, "Failed to load users.");
    }

    @FXML
    public void showExports() {
        recordNavigation("exports");
        currentView = "exports";
        boolean sq = isAlbanian();
        setStatus(LanguageManager.get("export.status"));
        setActiveButton(exportButton);

        VBox page = new VBox(18);
        page.getStyleClass().add("module-page");
        page.setPadding(new javafx.geometry.Insets(24));

        Label title = new Label(LanguageManager.get("export.title"));
        title.getStyleClass().add("page-title");

        Label empTitle = new Label(LanguageManager.get("export.employees.title"));
        empTitle.getStyleClass().add("section-title");
        HBox empRow = new HBox(10);
        Button empExcel = new Button(LanguageManager.get("export.excel"));
        Button empPdf = new Button(LanguageManager.get("export.pdf"));
        empExcel.getStyleClass().add("primary-button");
        empPdf.getStyleClass().add("primary-button");
        empRow.getChildren().addAll(empExcel, empPdf);

        Label conTitle = new Label(LanguageManager.get("export.contracts.title"));
        conTitle.getStyleClass().add("section-title");
        HBox conRow = new HBox(10);
        Button conExcel = new Button(LanguageManager.get("export.excel"));
        Button conPdf = new Button(LanguageManager.get("export.pdf"));
        conExcel.getStyleClass().add("primary-button");
        conPdf.getStyleClass().add("primary-button");
        conRow.getChildren().addAll(conExcel, conPdf);

        Label salTitle = new Label(LanguageManager.get("export.salaries.title"));
        salTitle.getStyleClass().add("section-title");
        HBox salRow = new HBox(10);
        Button salExcel = new Button(LanguageManager.get("export.excel"));
        Button salPdf = new Button(LanguageManager.get("export.pdf"));
        salExcel.getStyleClass().add("primary-button");
        salPdf.getStyleClass().add("primary-button");
        salRow.getChildren().addAll(salExcel, salPdf);

        Label depTitle = new Label(LanguageManager.get("export.departments.title"));
        depTitle.getStyleClass().add("section-title");
        HBox depRow = new HBox(10);
        Button depExcel = new Button(LanguageManager.get("export.excel"));
        Button depPdf = new Button(LanguageManager.get("export.pdf"));
        depExcel.getStyleClass().add("primary-button");
        depPdf.getStyleClass().add("primary-button");
        depRow.getChildren().addAll(depExcel, depPdf);

        Label userTitle = new Label(LanguageManager.get("export.users.title"));
        userTitle.getStyleClass().add("section-title");
        HBox userRow = new HBox(10);
        Button userExcel = new Button(LanguageManager.get("export.excel"));
        Button userPdf = new Button(LanguageManager.get("export.pdf"));
        userExcel.getStyleClass().add("primary-button");
        userPdf.getStyleClass().add("primary-button");
        userRow.getChildren().addAll(userExcel, userPdf);

        Label statusMsg = new Label("");
        statusMsg.setWrapText(true);

        empExcel.setOnAction(e -> handleExport("employees", "excel", statusMsg, sq));
        empPdf.setOnAction(e -> handleExport("employees", "pdf", statusMsg, sq));
        conExcel.setOnAction(e -> handleExport("contracts", "excel", statusMsg, sq));
        conPdf.setOnAction(e -> handleExport("contracts", "pdf", statusMsg, sq));
        salExcel.setOnAction(e -> handleExport("salaries", "excel", statusMsg, sq));
        salPdf.setOnAction(e -> handleExport("salaries", "pdf", statusMsg, sq));
        depExcel.setOnAction(e -> handleExport("departments", "excel", statusMsg, sq));
        depPdf.setOnAction(e -> handleExport("departments", "pdf", statusMsg, sq));
        userExcel.setOnAction(e -> handleExport("users", "excel", statusMsg, sq));
        userPdf.setOnAction(e -> handleExport("users", "pdf", statusMsg, sq));

        page.getChildren().addAll(title, empTitle, empRow, conTitle, conRow, salTitle, salRow,
                depTitle, depRow, userTitle, userRow, statusMsg);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("module-scroll");
        contentArea.getChildren().setAll(scroll);
    }

    private void handleExport(String type, String format, Label statusLabel, boolean sq) {
        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle(LanguageManager.get("export.saveFile"));
        String ext = "excel".equals(format) ? "xlsx" : "pdf";
        chooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter(ext.toUpperCase() + " Files", "*." + ext));
        chooser.setInitialFileName(type + "-export." + ext);

        File file = chooser.showSaveDialog(contentArea.getScene().getWindow());
        if (file == null) return;

        try {
            switch (type + "-" + format) {
                case "employees-excel" -> com.company.system.service.ExportService.exportEmployeesToExcel(com.company.system.service.EmployeeService.getAllEmployees(), file);
                case "employees-pdf" -> com.company.system.service.ExportService.exportEmployeesToPdf(com.company.system.service.EmployeeService.getAllEmployees(), file);
                case "contracts-excel" -> com.company.system.service.ExportService.exportContractsToExcel(com.company.system.service.ContractService.getAllContracts(), file);
                case "contracts-pdf" -> com.company.system.service.ExportService.exportContractsToPdf(com.company.system.service.ContractService.getAllContracts(), file);
                case "salaries-excel" -> com.company.system.service.ExportService.exportSalariesToExcel(com.company.system.service.SalaryService.getAllSalaries(), file);
                case "salaries-pdf" -> com.company.system.service.ExportService.exportSalariesToPdf(com.company.system.service.SalaryService.getAllSalaries(), file);
                case "departments-excel" -> com.company.system.service.ExportService.exportDepartmentsToExcel(com.company.system.service.DepartmentService.getAllDepartments(), file);
                case "departments-pdf" -> com.company.system.service.ExportService.exportDepartmentsToPdf(com.company.system.service.DepartmentService.getAllDepartments(), file);
                case "users-excel" -> com.company.system.service.ExportService.exportUsersToExcel(com.company.system.service.UserService.getAllUsers(), file);
                case "users-pdf" -> com.company.system.service.ExportService.exportUsersToPdf(com.company.system.service.UserService.getAllUsers(), file);
            }
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText(LanguageManager.get("export.success") + file.getName());
        } catch (Exception ex) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText(LanguageManager.get("export.error") + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void showHelp() {
        recordNavigation("help"); currentView = "help";
        setStatus(LanguageManager.get("status.help"));
        clearActiveButton();
        setContent(createAdminHelpView());
    }

    public static Node createAdminHelpView() {
        boolean sq = isAlbanianLocale();
        Label title = new Label(LanguageManager.get("help.title"));
        title.getStyleClass().add("page-title");

        Label intro = new Label(LanguageManager.get("help.admin.intro"));
        intro.setWrapText(true);
        intro.getStyleClass().add("body-text");

        VBox sections = new VBox(14);
        sections.getChildren().addAll(
                createHelpSection(LanguageManager.get("help.admin.navigation.title"),
                        LanguageManager.get("help.admin.navigation.line1"),
                        LanguageManager.get("help.admin.navigation.line2"),
                        LanguageManager.get("help.admin.navigation.line3")),
                createHelpSection(LanguageManager.get("help.admin.shortcuts.title"),
                "Ctrl+E - " + LanguageManager.get("menu.employees"),
                        "Ctrl+K - " + LanguageManager.get("menu.contracts"),
                        "Ctrl+S - " + LanguageManager.get("menu.salaries"),
                        "Ctrl+D - " + LanguageManager.get("menu.dashboard"),
                        "Ctrl+R - " + LanguageManager.get("menu.departments"),
                        "Ctrl+U - " + LanguageManager.get("menu.users"),
                        "Ctrl+P - " + LanguageManager.get("menu.profile"),
                        "Ctrl+L - " + LanguageManager.get("menu.language"),
                        "Ctrl+H / F1 - " + LanguageManager.get("menu.help"),
                        "Alt+Left / Mouse Back - " + LanguageManager.get("help.admin.shortcuts.back"),
                        "Alt+Right / Mouse Forward - " + LanguageManager.get("help.admin.shortcuts.forward"),
                        "F5 - " + LanguageManager.get("help.admin.shortcuts.refresh"),
                        "Esc - " + LanguageManager.get("menu.exit")),
                createHelpSection(LanguageManager.get("help.admin.context.title"),
                        LanguageManager.get("help.admin.context.line1"),
                        LanguageManager.get("help.admin.context.line2")),
                createHelpSection(LanguageManager.get("help.admin.language.title"),
                        LanguageManager.get("help.admin.language.line1"),
                        LanguageManager.get("help.admin.language.line2")),
                createHelpSection(LanguageManager.get("help.admin.account.title"),
                        LanguageManager.get("help.admin.account.line1"),
                        LanguageManager.get("help.admin.account.line2"))
        );

        return wrapHelpView(title, intro, sections);
    }

    public static Node createUserHelpView() {
        boolean sq = isAlbanianLocale();
        Label title = new Label(LanguageManager.get("help.title"));
        title.getStyleClass().add("page-title");

        Label intro = new Label(LanguageManager.get("help.user.intro"));
        intro.setWrapText(true);
        intro.getStyleClass().add("body-text");

        VBox sections = new VBox(14);
        sections.getChildren().addAll(
                createHelpSection(LanguageManager.get("help.user.navigation.title"),
                        LanguageManager.get("help.user.navigation.line1"),
                        LanguageManager.get("help.user.navigation.line2")),
                createHelpSection(LanguageManager.get("help.user.shortcuts.title"),
                        "Ctrl+D - " + LanguageManager.get("menu.dashboard"),
                        "Ctrl+K - " + LanguageManager.get("help.user.shortcuts.contracts"),
                        "Ctrl+S - " + LanguageManager.get("help.user.shortcuts.salaries"),
                        "Ctrl+R - " + LanguageManager.get("help.user.shortcuts.departments"),
                        "Ctrl+P - " + LanguageManager.get("menu.profile"),
                        "Ctrl+L - " + LanguageManager.get("menu.language"),
                        "Ctrl+H / F1 - " + LanguageManager.get("menu.help"),
                        "Alt+Left / Mouse Back - " + LanguageManager.get("help.user.shortcuts.back"),
                        "Alt+Right / Mouse Forward - " + LanguageManager.get("help.user.shortcuts.forward"),
                        "F5 - " + LanguageManager.get("help.user.shortcuts.refresh"),
                        "Esc - " + LanguageManager.get("menu.exit")),
                createHelpSection(LanguageManager.get("help.user.context.title"),
                        LanguageManager.get("help.user.context.line1"),
                        LanguageManager.get("help.user.context.line2"))
        );

        return wrapHelpView(title, intro, sections);
    }

    private static ScrollPane wrapHelpView(Label title, Label intro, VBox sections) {
        VBox helpView = new VBox(18, title, intro, sections);
        helpView.getStyleClass().add("profile-page");
        helpView.setStyle("-fx-padding: 26;");
        ScrollPane scrollPane = new ScrollPane(helpView);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("module-scroll");
        return scrollPane;
    }

    private static VBox createHelpSection(String sectionTitle, String... lines) {
        Label title = new Label(sectionTitle);
        title.getStyleClass().add("section-title");
        VBox content = new VBox(6, title);
        for (String line : lines) {
            Label item = new Label("• " + line);
            item.setWrapText(true);
            item.getStyleClass().add("body-text");
            content.getChildren().add(item);
        }
        content.getStyleClass().add("content-card");
        return content;
    }

    private static boolean isAlbanianLocale() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    @FXML
    public void showProfile() {
        recordNavigation("profile"); currentView = "profile";
        setStatus(LanguageManager.get("status.account"));
        setActiveButton(profileButton);

        User user = Session.getUser();
        if (user == null) {setContent(new Label(LanguageManager.get("profile.noUserLoggedIn")));
            return;}
        String roleText = "ADMIN".equalsIgnoreCase(user.getRole())
                ? LanguageManager.get("account.role.admin")
                : LanguageManager.get("account.role.user");

        Label title = new Label(LanguageManager.get("profile.title"));
        title.getStyleClass().add("page-title");


        StackPane userIcon = createSidebarIconBox(ICON_USER, 1.08);
        userIcon.getStyleClass().add("profile-icon");

        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("section-title");

        Label roleLabel = new Label(LanguageManager.get("account.role") + roleText);
        roleLabel.getStyleClass().add("profile-detail");

        Label createdAtLabel = new Label(LanguageManager.get("account.createdat") + formatCreatedAt(user.getCreatedAt()));
        createdAtLabel.getStyleClass().add("profile-detail");

        VBox userDetails = new VBox(5, usernameLabel, roleLabel, createdAtLabel);
        HBox userHeader = new HBox(14, userIcon, userDetails);
        VBox userCard = new VBox(userHeader);
        userCard.getStyleClass().add("profile-card");
        userCard.setMaxWidth(900);

        Label languageTitle = new Label(LanguageManager.get("account.language") + ":");
        languageTitle.getStyleClass().add("section-title");

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll(LanguageManager.get("language.english"), LanguageManager.get("language.albanian"));
        languageBox.setValue(isAlbanian() ? LanguageManager.get("language.albanian") : LanguageManager.get("language.english"));
        languageBox.setPrefWidth(210);

        Label languageMessage = new Label();
        languageMessage.getStyleClass().add("success-text");

        languageBox.setOnAction(event -> {
            String selected = languageBox.getValue();
            String albanianText = LanguageManager.get("language.albanian");
            if (albanianText.equals(selected)) LanguageManager.setLanguage("sq");
            else LanguageManager.setLanguage("en");
            profileLanguageChanged = true;
            updateTexts();
            showProfile();
        });

        VBox languageRow = new VBox(6, languageBox, languageMessage);
        languageMessage.setText(profileLanguageChanged ? LanguageManager.get("account.language.success") : "");
        profileLanguageChanged = false;

        Button logout = new Button(LanguageManager.get("account.logout"));
        logout.getStyleClass().add("secondary-button");
        logout.setOnAction(e -> handleLogout());

        VBox languageCard = new VBox(12, languageTitle, languageRow);
        languageCard.getStyleClass().add("profile-card");
        languageCard.setPrefWidth(430);
        languageCard.setMaxWidth(Double.MAX_VALUE);

        Label passwordTitle = new Label(LanguageManager.get("profile.password.title"));
        passwordTitle.getStyleClass().add("section-title");


        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText(LanguageManager.get("profile.password.current"));
        currentPasswordField.setMaxWidth(Double.MAX_VALUE);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText(LanguageManager.get("profile.password.new"));
        newPasswordField.setMaxWidth(Double.MAX_VALUE);

        Button changePasswordButton = new Button(LanguageManager.get("profile.password.change"));
        changePasswordButton.getStyleClass().add("primary-button");
        changePasswordButton.setOnAction(e -> changePassword(user, currentPasswordField, newPasswordField));

        VBox passwordCard = new VBox(12, passwordTitle, currentPasswordField, newPasswordField, changePasswordButton);
        passwordCard.getStyleClass().add("profile-card");
        passwordCard.setPrefWidth(430);
        passwordCard.setMaxWidth(Double.MAX_VALUE);

        HBox accountOptions = new HBox(18, languageCard, passwordCard);
        accountOptions.setMaxWidth(900);
        HBox.setHgrow(languageCard, Priority.ALWAYS);
        HBox.setHgrow(passwordCard, Priority.ALWAYS);

        Button deleteAccount = new Button(LanguageManager.get("profile.deleteAccount"));
        deleteAccount.getStyleClass().add("danger-text-button");
        deleteAccount.setOnAction(e -> confirmDeleteAccount(user));

        VBox actionsCard = new VBox(14, logout, deleteAccount);
        actionsCard.getStyleClass().add("profile-card");
        actionsCard.setMaxWidth(900);

        VBox profileView = new VBox(18, title, userCard, accountOptions, actionsCard);
        profileView.getStyleClass().add("profile-page");
        profileView.setStyle("-fx-padding: 28;");
        VBox.setVgrow(profileView, Priority.NEVER);
        setContent(profileView);
    }

private String formatCurrency(double value) {
    return String.format("%.2f €", value);
}

private String valueOrDash(String value) {
    return value == null || value.isBlank() ? "-" : value;
}

    private void changePassword(User user, PasswordField currentPasswordField, PasswordField newPasswordField) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String storedHash = UserService.getPasswordHashByUsername(user.getUsername());

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            showStyledAlert(Alert.AlertType.ERROR, LanguageManager.get("message.error.title"),
                    LanguageManager.get("profile.password.fillBothFields"));
            return;
        }
        if (!PasswordUtils.verifyPassword(currentPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR, LanguageManager.get("message.error.title"),
                    LanguageManager.get("profile.password.currentIncorrect"));
            return;
        }
        if (PasswordUtils.verifyPassword(newPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR, LanguageManager.get("message.error.title"),
                    LanguageManager.get("profile.password.sameAsOld"));
            return;
        }
        if (UserService.resetPassword(user.getUsername(), newPassword)) {
            currentPasswordField.clear(); newPasswordField.clear();
            showStyledAlert(Alert.AlertType.INFORMATION, LanguageManager.get("message.success.title"),
                    LanguageManager.get("profile.password.changed"));
        } else {
            showStyledAlert(Alert.AlertType.ERROR, LanguageManager.get("message.error.title"),
                    LanguageManager.get("profile.password.notChanged"));
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

    private void confirmDeleteAccount(User user) {
        if (UserService.isOnlyAdmin(user)) {
            showStyledAlert(Alert.AlertType.WARNING, LanguageManager.get("message.warning.title"),
                    LanguageManager.get("profile.delete.onlyAdmin"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("profile.delete.title"));
        alert.setHeaderText(null);

        ButtonType yesType = new ButtonType(LanguageManager.get("profile.delete.confirmYes"), ButtonBar.ButtonData.YES);
        ButtonType noType = new ButtonType(LanguageManager.get("profile.delete.confirmNo"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesType, noType);

        Label icon = new Label("🗑");
        icon.setText("");
        icon.setGraphic(createPopupIcon(ICON_DELETE, "#dc2626"));
        icon.setStyle("-fx-padding: 10;");

        Label title = new Label(LanguageManager.get("profile.delete.confirmTitle"));
        title.setWrapText(true); title.setMaxWidth(370); title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");

        Label subtitle = new Label(LanguageManager.get("profile.delete.confirmSubtitle"));
        subtitle.setWrapText(true); subtitle.setMaxWidth(370); subtitle.setMinHeight(Label.USE_PREF_SIZE);
        subtitle.setStyle("-fx-font-size: 13px; -fx-opacity: 0.8;");

        VBox content = new VBox(15, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(360);

        Platform.runLater(() -> {
            Button yesButton = (Button) alert.getDialogPane().lookupButton(yesType);
            Button noButton = (Button) alert.getDialogPane().lookupButton(noType);
            yesButton.setMaxWidth(Double.MAX_VALUE); yesButton.setPrefHeight(42);
            noButton.setMaxWidth(Double.MAX_VALUE); noButton.setPrefHeight(42);
            yesButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            noButton.setStyle("-fx-background-radius: 10; -fx-cursor: hand;");
            VBox buttonBox = new VBox(10, yesButton, noButton);
            buttonBox.setAlignment(Pos.CENTER); buttonBox.setFillWidth(true);
            alert.getDialogPane().setContent(new VBox(20, content, buttonBox));
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesType) {
            boolean deleted = UserService.deleteAccountAndEmployeeData(user);
            if (deleted) { Session.clear(); showWelcome(); }
            else showStyledAlert(Alert.AlertType.ERROR, LanguageManager.get("message.error.title"),
                    LanguageManager.get("profile.delete.failed"));
        }
    }

    private void loadView(String fxmlPath, Button activeButton, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            setContent(view);
            setActiveButton(activeButton);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(errorMessage);
        }
    }

    private String formatCreatedAt(Timestamp createdAt) {
        if (createdAt == null) return "-";
        return createdAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    private record NavItem(String iconPath, String label) {
    }
}
