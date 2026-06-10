package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.KeyboardNavigation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class UsersController {

    private static final String ICON_USER = "M12 12 C14.76 12 17 9.76 17 7 C17 4.24 14.76 2 12 2 C9.24 2 7 4.24 7 7 C7 9.76 9.24 12 12 12 Z M4 22 C4 17.58 7.58 14 12 14 C16.42 14 20 17.58 20 22 Z";

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private FilteredList<User> filteredUsers;


    @FXML private Label titleLabel;
    @FXML private TextField searchField;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> employeeColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Timestamp> createdColumn;
    @FXML private StackPane userIconBox;
    @FXML private Label selectedTitleLabel;
    @FXML private Label usernameDetailLabel;
    @FXML private Label usernameValueLabel;
    @FXML private Label employeeDetailLabel;
    @FXML private Label employeeIdValueLabel;
    @FXML private Label employeeValueLabel;
    @FXML private Label passwordHashDetailLabel;
    @FXML private Label passwordHashValueLabel;
    @FXML private Label roleDetailLabel;
    @FXML private Label roleValueLabel;
    @FXML private Label createdDetailLabel;
    @FXML private Label createdValueLabel;
    @FXML private Button addUserBtn;
    @FXML private Button editUserBtn;
    @FXML private Button deleteUserBtn;

    @FXML
    public void initialize() {
        loadTexts();
        setupIcon();
        setupTable();
        setupSearch();
        setupSelection();
        loadUsers();
        setupActionButtons();
    }


    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("menu.users"));
        searchField.setPromptText(LanguageManager.get("users.search"));
        idColumn.setText(LanguageManager.get("table.id"));
        usernameColumn.setText(LanguageManager.get("users.username"));
        employeeColumn.setText(LanguageManager.get("users.employee"));
        roleColumn.setText(LanguageManager.get("account.role").replace(":", ""));
        createdColumn.setText(LanguageManager.get("users.created"));
        selectedTitleLabel.setText(LanguageManager.get("users.select"));
        usernameDetailLabel.setText(LanguageManager.get("users.username"));
        employeeDetailLabel.setText(LanguageManager.get("users.employee"));
        passwordHashDetailLabel.setText(LanguageManager.get("users.passwordHash"));
        roleDetailLabel.setText(LanguageManager.get("account.role").replace(":", ""));
        createdDetailLabel.setText(LanguageManager.get("users.created"));
    }

    private void setupIcon() {
        SVGPath icon = new SVGPath();
        icon.setContent(ICON_USER);
        icon.getStyleClass().add("sidebar-svg-icon");
        icon.setScaleX(1.08);
        icon.setScaleY(1.08);

        userIconBox.getChildren().setAll(icon);
        userIconBox.getStyleClass().add("profile-icon");
        userIconBox.setAlignment(Pos.CENTER);
    }

    private void setupTable() {
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }

    private void setupSearch() {
        filteredUsers = new FilteredList<>(users, user -> true);
        SortedList<User> sortedUsers = new SortedList<>(filteredUsers);
        sortedUsers.comparatorProperty().bind(usersTable.comparatorProperty());
        usersTable.setItems(sortedUsers);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredUsers.setPredicate(user -> keyword.isEmpty()
                    || contains(user.getUsername(), keyword)
                    || contains(user.getEmployeeName(), keyword)
                    || contains(user.getRole(), keyword));
        });
    }

    private void setupSelection() {
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldUser, selectedUser) -> showUserDetails(selectedUser)
        );
    }

    private void loadUsers() {
        users.setAll(UserService.getAllUsers());

        if (!users.isEmpty()) {
            usersTable.getSelectionModel().selectFirst();
        }
    }

    private void showUserDetails(User user) {
        if (user == null) {
            selectedTitleLabel.setText(LanguageManager.get("users.select"));
            usernameValueLabel.setText("-");
            employeeIdValueLabel.setText("-");
            employeeValueLabel.setText("-");
            passwordHashValueLabel.setText("-");
            roleValueLabel.setText("-");
            createdValueLabel.setText("-");
            return;
        }

        selectedTitleLabel.setText(UserService.getDisplayName(user));
        usernameValueLabel.setText(user.getUsername());
        employeeIdValueLabel.setText(user.getEmployeeId() == null ? "-" : String.valueOf(user.getEmployeeId()));
        employeeValueLabel.setText(user.getEmployeeName() == null ? "-" : user.getEmployeeName());
        passwordHashValueLabel.setText(user.getPasswordHash());
        roleValueLabel.setText(user.getRole());
        createdValueLabel.setText(formatCreatedAt(user.getCreatedAt()));
    }

    private String formatCreatedAt(Timestamp createdAt) {
        if (createdAt == null) {
            return "-";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.toLocalDateTime().format(formatter);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }


    private void setupActionButtons() {
        if (editUserBtn == null || deleteUserBtn == null) return;

        editUserBtn.setDisable(true);
        deleteUserBtn.setDisable(true);

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    boolean noneSelected = (newVal == null);
                    editUserBtn.setDisable(noneSelected);
                    deleteUserBtn.setDisable(noneSelected);
                }
        );
    }

    @FXML
    private void onAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/add-user-wizard.fxml"));
            Parent root = loader.load();

            AddUserWizardController ctrl = loader.getController();
            ctrl.setOnSuccess(this::loadUsers);

            Stage stage = new Stage();
            stage.setTitle(LanguageManager.get("users.add.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            KeyboardNavigation.install(scene);
            KeyboardNavigation.focusFirst(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            showError(LanguageManager.get("users.add.open.error"));
        }
    }

    @FXML
    private void onEditUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/edit-user.fxml"));
            Parent root = loader.load();

            EditUserController ctrl = loader.getController();
            ctrl.setUser(selected);
            ctrl.setOnSuccess(this::loadUsers);

            Stage stage = new Stage();
            stage.setTitle(LanguageManager.get("users.edit.title"));
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root, 420, 420);
            KeyboardNavigation.install(scene);
            KeyboardNavigation.focusFirst(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            showError(LanguageManager.get("users.edit.open.error"));
        }
    }

    @FXML
    private void onDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (UserService.isOnlyAdmin(selected)) {
            showError(LanguageManager.get("users.delete.onlyAdmin"));
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtils.style(confirm);
        confirm.setTitle(LanguageManager.get("users.delete.title"));
        confirm.setHeaderText(LanguageManager.get("users.delete.header"));
        confirm.setContentText(String.format(LanguageManager.get("users.delete.content"), selected.getUsername()));
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = UserService.deleteAccountAndEmployeeData(selected);
                if (ok) {
                    loadUsers();
                } else {
                    showError(LanguageManager.get("users.delete.error"));
                }
            }
        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("message.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
