package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.LeaveRequest;
import com.company.system.service.LeaveRequestService;
import com.company.system.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AdminLeaveRequestsController {

    private final ObservableList<LeaveRequest> requests = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private TextField searchField;
    @FXML private Label pendingCountLabel;
    @FXML private VBox requestsBox;

    @FXML
    public void initialize() {
        loadTexts();
        loadRequests();
        setupSearch();
    }

    private void loadTexts() {
        boolean sq = isAlbanian();
        titleLabel.setText(sq ? "Kerkesat per pushime" : "Leave requests");
        subtitleLabel.setText(sq
                ? "Shqyrtoni kerkesat per pushim vjetor, pushim mjekesor dhe festa."
                : "Review annual leave, medical leave and holiday requests.");
        searchField.setPromptText(sq ? "Kerko sipas punetorit, llojit ose statusit" : "Search by employee, type or status");
    }

    private void loadRequests() {
        requests.setAll(LeaveRequestService.getAllRequests());
        renderRequests(requests);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase(Locale.ROOT).trim();
            if (keyword.isEmpty()) {
                renderRequests(requests);
                return;
            }

            List<LeaveRequest> filtered = requests.stream()
                    .filter(request -> contains(request.getEmployeeName(), keyword)
                            || contains(displayType(request), keyword)
                            || contains(displayStatus(request), keyword)
                            || contains(request.getReason(), keyword))
                    .toList();

            renderRequests(filtered);
        });
    }

    private void renderRequests(List<LeaveRequest> visibleRequests) {
        requestsBox.getChildren().clear();
        long pendingCount = requests.stream()
                .filter(request -> "Pending".equalsIgnoreCase(request.getStatus()))
                .count();

        pendingCountLabel.setText(isAlbanian()
                ? pendingCount + " ne pritje"
                : pendingCount + " pending");

        if (visibleRequests.isEmpty()) {
            Label emptyLabel = new Label(isAlbanian()
                    ? "Nuk ka kerkesa per t'u shfaqur."
                    : "There are no requests to show.");
            emptyLabel.getStyleClass().add("empty-state-text");
            VBox emptyCard = new VBox(emptyLabel);
            emptyCard.getStyleClass().add("content-card");
            requestsBox.getChildren().add(emptyCard);
            return;
        }

        for (LeaveRequest request : visibleRequests) {
            requestsBox.getChildren().add(createRequestRow(request));
        }
    }

    private HBox createRequestRow(LeaveRequest request) {
        Label employeeLabel = createMetaLabel(isAlbanian() ? "Punetori" : "Employee", request.getEmployeeName());
        Label typeLabel = createMetaLabel(isAlbanian() ? "Lloji" : "Type", displayType(request));
        Label dateLabel = createMetaLabel(isAlbanian() ? "Datat" : "Dates", formatDateRange(request.getStartDate(), request.getEndDate()));
        Label daysLabel = createMetaLabel(isAlbanian() ? "Dite pune" : "Work days", String.valueOf(request.getWorkingDays()));

        HBox info = new HBox(18, employeeLabel, typeLabel, dateLabel, daysLabel);
        info.setAlignment(Pos.CENTER_LEFT);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusBadge = new Label(displayStatus(request));
        statusBadge.getStyleClass().addAll("status-badge", statusClass(request.getStatus()));

        Button openButton = new Button(isAlbanian() ? "Hap" : "Open");
        openButton.getStyleClass().add("secondary-button");
        openButton.setOnAction(event -> openRequestDialog(request));

        HBox row = new HBox(14, info, statusBadge, openButton);
        row.getStyleClass().add("leave-request-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label createMetaLabel(String label, String value) {
        Label meta = new Label(label + "\n" + valueOrDash(value));
        meta.getStyleClass().add("leave-request-meta");
        meta.setMinWidth(0);
        return meta;
    }

    private void openRequestDialog(LeaveRequest request) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(isAlbanian() ? "Kerkesa per pushim" : "Leave request");
        alert.setHeaderText(null);

        ButtonType closeType = new ButtonType(isAlbanian() ? "Mbyll" : "Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(closeType);

        Label title = new Label(request.getEmployeeName() + " - " + displayType(request));
        title.getStyleClass().add("section-title");
        title.setWrapText(true);

        Label details = new Label(formatDateRange(request.getStartDate(), request.getEndDate())
                + " | " + request.getWorkingDays() + " "
                + (isAlbanian() ? "dite pune" : "work days")
                + " | " + displayStatus(request));
        details.getStyleClass().add("page-subtitle");
        details.setWrapText(true);

        Label messageTitle = new Label(isAlbanian() ? "Mesazhi i punetorit" : "Employee message");
        messageTitle.getStyleClass().add("field-label");

        Label message = new Label(valueOrDash(request.getReason()));
        message.getStyleClass().add("body-text");
        message.setWrapText(true);
        message.setMinHeight(Label.USE_PREF_SIZE);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText(isAlbanian() ? "Komenti i adminit" : "Admin comment");
        commentArea.setText(request.getAdminResponse() == null ? "" : request.getAdminResponse());
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(3);

        Button approveButton = new Button(isAlbanian() ? "Prano" : "Accept");
        approveButton.getStyleClass().add("success-button");
        approveButton.setDisable(!"Pending".equalsIgnoreCase(request.getStatus()));
        approveButton.setOnAction(event -> handleDecision(alert, request, "Approved", commentArea.getText()));

        Button rejectButton = new Button(isAlbanian() ? "Refuzo" : "Deny");
        rejectButton.getStyleClass().add("danger-button");
        rejectButton.setDisable(!"Pending".equalsIgnoreCase(request.getStatus()));
        rejectButton.setOnAction(event -> handleDecision(alert, request, "Rejected", commentArea.getText()));

        HBox actions = new HBox(10, approveButton, rejectButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(12, title, details, messageTitle, message, commentArea, actions);
        content.setPadding(new Insets(8));
        content.setPrefWidth(520);
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void handleDecision(Alert parentAlert, LeaveRequest request, String status, String comment) {
        boolean success = LeaveRequestService.updateStatus(request.getId(), status, comment);
        if (success) {
            parentAlert.close();
            loadRequests();
            showInfo(isAlbanian()
                    ? "Kerkesa u perditesua me sukses."
                    : "Request updated successfully.");
        } else {
            showError(isAlbanian()
                    ? "Kerkesa nuk u perditesua."
                    : "Request was not updated.");
        }
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, LanguageManager.get("message.success.title"), message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, LanguageManager.get("message.error.title"), message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        DialogUtils.style(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String displayType(LeaveRequest request) {
        String type = request.getRequestType();
        if (type == null || type.isBlank()) {
            return isAlbanian() ? "Pushim vjetor" : "Annual leave";
        }

        return switch (type) {
            case "Annual Leave" -> isAlbanian() ? "Pushim vjetor" : "Annual leave";
            case "Medical Leave" -> isAlbanian() ? "Pushim mjekesor" : "Medical leave";
            case "Holiday" -> isAlbanian() ? "Feste" : "Holiday";
            default -> type;
        };
    }

    private String displayStatus(LeaveRequest request) {
        String status = request.getStatus();
        if ("Approved".equalsIgnoreCase(status)) {
            return isAlbanian() ? "Pranuar" : "Approved";
        }
        if ("Rejected".equalsIgnoreCase(status)) {
            return isAlbanian() ? "Refuzuar" : "Rejected";
        }
        return isAlbanian() ? "Ne pritje" : "Pending";
    }

    private String statusClass(String status) {
        if ("Approved".equalsIgnoreCase(status)) {
            return "approved";
        }
        if ("Rejected".equalsIgnoreCase(status)) {
            return "rejected";
        }
        return "pending";
    }

    private String formatDateRange(Date startDate, Date endDate) {
        return formatDate(startDate) + " - " + formatDate(endDate);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "-";
        }
        return date.toLocalDate().format(dateFormatter);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }
}
