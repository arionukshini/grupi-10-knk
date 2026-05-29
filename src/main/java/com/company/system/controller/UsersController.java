package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

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

    @FXML
    public void initialize() {
        loadTexts();
        setupIcon();
        setupTable();
        setupSearch();
        setupSelection();
        loadUsers();
    }

    private void loadTexts() {
        boolean sq = isAlbanian();

        titleLabel.setText(LanguageManager.get("menu.users"));
        searchField.setPromptText(sq ? "Kerko perdorues, punetor ose rol" : "Search by user, employee or role");
        idColumn.setText(LanguageManager.get("table.id"));
        usernameColumn.setText(sq ? "Perdoruesi" : "Username");
        employeeColumn.setText(sq ? "Punetori" : "Employee");
        roleColumn.setText(LanguageManager.get("account.role").replace(":", ""));
        createdColumn.setText(sq ? "Krijuar" : "Created");
        selectedTitleLabel.setText(sq ? "Zgjidhni nje perdorues" : "Select a user");
        usernameDetailLabel.setText(sq ? "Perdoruesi" : "Username");
        employeeDetailLabel.setText(sq ? "Punetori" : "Employee");
        passwordHashDetailLabel.setText(sq ? "Hash i fjalekalimit" : "Password hash");
        roleDetailLabel.setText(LanguageManager.get("account.role").replace(":", ""));
        createdDetailLabel.setText(sq ? "Krijuar" : "Created");
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
            selectedTitleLabel.setText(isAlbanian() ? "Zgjidhni nje perdorues" : "Select a user");
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
}
