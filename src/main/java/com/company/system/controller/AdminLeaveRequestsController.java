package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.LeaveRequest;
import com.company.system.service.LeaveRequestService;
import com.company.system.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class AdminLeaveRequestsController {

    private final ObservableList<LeaveRequest> requests = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final double EMPLOYEE_COLUMN_WIDTH = 170;
    private static final double TYPE_COLUMN_WIDTH = 145;
    private static final double DATE_COLUMN_WIDTH = 165;
    private static final double DAYS_COLUMN_WIDTH = 90;

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
        titleLabel.setText(LanguageManager.get("leave.admin.title"));
        subtitleLabel.setText(LanguageManager.get("leave.admin.subtitle"));
        searchField.setPromptText(LanguageManager.get("leave.admin.search"));
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

        pendingCountLabel.setText(String.format(LanguageManager.get("leave.pendingCount"), pendingCount));

        if (visibleRequests.isEmpty()) {
            Label emptyLabel = new Label(LanguageManager.get("leave.empty"));
            emptyLabel.getStyleClass().add("empty-state-text");
            VBox emptyCard = new VBox(emptyLabel);
            emptyCard.getStyleClass().add("content-card");
            requestsBox.getChildren().add(emptyCard);
            return;
        }

        requestsBox.getChildren().add(createHeaderRow());

        for (LeaveRequest request : visibleRequests) {
            requestsBox.getChildren().add(createRequestRow(request));
        }
    }

    private HBox createHeaderRow() {
        Label employeeHeader = createHeaderLabel(LanguageManager.get("leave.employee"), EMPLOYEE_COLUMN_WIDTH);
        Label typeHeader = createHeaderLabel(LanguageManager.get("leave.type"), TYPE_COLUMN_WIDTH);
        Label dateHeader = createHeaderLabel(LanguageManager.get("leave.dates"), DATE_COLUMN_WIDTH);
        Label daysHeader = createHeaderLabel(LanguageManager.get("leave.workDays"), DAYS_COLUMN_WIDTH);

        HBox info = new HBox(18, employeeHeader, typeHeader, dateHeader, daysHeader);
        info.setAlignment(Pos.CENTER_LEFT);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusHeader = createHeaderLabel(LanguageManager.get("leave.status"), 82);
        Button spacer = new Button(LanguageManager.get("leave.open"));
        spacer.getStyleClass().add("secondary-button");
        spacer.setVisible(false);
        spacer.setManaged(true);

        HBox header = new HBox(14, info, statusHeader, spacer);
        header.getStyleClass().add("leave-request-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox createRequestRow(LeaveRequest request) {
        Label employeeLabel = createMetaLabel(request.getEmployeeName(), EMPLOYEE_COLUMN_WIDTH);
        Label typeLabel = createMetaLabel(displayType(request), TYPE_COLUMN_WIDTH);
        Label dateLabel = createMetaLabel(formatDateRange(request.getStartDate(), request.getEndDate()), DATE_COLUMN_WIDTH);
        Label daysLabel = createMetaLabel(String.valueOf(request.getWorkingDays()), DAYS_COLUMN_WIDTH);

        HBox info = new HBox(18, employeeLabel, typeLabel, dateLabel, daysLabel);
        info.setAlignment(Pos.CENTER_LEFT);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusBadge = new Label(displayStatus(request));
        statusBadge.getStyleClass().addAll("status-badge", statusClass(request.getStatus()));

        Button openButton = new Button(LanguageManager.get("leave.open"));
        openButton.getStyleClass().add("secondary-button");
        openButton.setOnAction(event -> openRequestDialog(request));

        HBox row = new HBox(14, info, statusBadge, openButton);
        row.getStyleClass().add("leave-request-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label createHeaderLabel(String value, double width) {
        Label header = new Label(value);
        header.getStyleClass().add("leave-request-header-label");
        header.setMinWidth(0);
        header.setPrefWidth(width);
        header.setMaxWidth(width);
        return header;
    }

    private Label createMetaLabel(String value, double width) {
        Label meta = new Label(valueOrDash(value));
        meta.getStyleClass().add("leave-request-meta");
        meta.setMinWidth(0);
        meta.setPrefWidth(width);
        meta.setMaxWidth(width);
        return meta;
    }

    private void openRequestDialog(LeaveRequest request) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("leave.request.title"));
        alert.setHeaderText(null);
        ButtonType closeType = new ButtonType(LanguageManager.get("leave.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(closeType);

        Label title = new Label(request.getEmployeeName() + " - " + displayType(request));
        title.getStyleClass().add("section-title");
        title.setWrapText(true);

        Label details = new Label(formatDateRange(request.getStartDate(), request.getEndDate())
                + " | " + request.getWorkingDays() + " "
                + LanguageManager.get("leave.workDays").toLowerCase(Locale.ROOT)
                + " | " + displayStatus(request));
        details.getStyleClass().add("page-subtitle");
        details.setWrapText(true);

        Label messageTitle = new Label(LanguageManager.get("leave.employeeMessage"));
        messageTitle.getStyleClass().add("field-label");

        Label message = new Label(valueOrDash(request.getReason()));
        message.getStyleClass().add("body-text");
        message.setWrapText(true);
        message.setMinHeight(Label.USE_PREF_SIZE);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText(LanguageManager.get("leave.adminComment.prompt"));
        commentArea.setText(request.getAdminResponse() == null ? "" : request.getAdminResponse());
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(3);
        commentArea.setEditable("Pending".equalsIgnoreCase(request.getStatus()));
        commentArea.setDisable(!"Pending".equalsIgnoreCase(request.getStatus()));

        Label commentHint = new Label(LanguageManager.get("leave.adminComment.hint"));
        commentHint.getStyleClass().add("field-label");
        commentHint.setWrapText(true);

        Button approveButton = new Button(LanguageManager.get("leave.accept"));
        approveButton.getStyleClass().add("success-button");
        approveButton.setPrefWidth(92);
        approveButton.setDisable(!"Pending".equalsIgnoreCase(request.getStatus()));
        approveButton.setOnAction(event -> handleDecision(alert, request, "Approved", commentArea.getText()));

        Button rejectButton = new Button(LanguageManager.get("leave.deny"));
        rejectButton.getStyleClass().add("danger-button");
        rejectButton.setPrefWidth(92);
        rejectButton.setDisable(!"Pending".equalsIgnoreCase(request.getStatus()));
        rejectButton.setOnAction(event -> handleDecision(alert, request, "Rejected", commentArea.getText()));

        Button closeButton = new Button(LanguageManager.get("leave.close"));
        closeButton.getStyleClass().add("secondary-button");
        closeButton.setPrefWidth(92);
        closeButton.setOnAction(event -> {
            alert.setResult(closeType);
            alert.close();
        });

        GridPane actions = new GridPane();
        actions.setHgap(10);
        actions.setVgap(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.add(approveButton, 0, 0);
        actions.add(rejectButton, 1, 0);
        actions.add(closeButton, 1, 1);

        VBox content = new VBox(12, title, details, messageTitle, message, commentHint, commentArea, actions);
        content.setPadding(new Insets(8));
        content.setPrefWidth(520);
        alert.getDialogPane().setContent(content);
        Platform.runLater(() -> {
            Button defaultCloseButton = (Button) alert.getDialogPane().lookupButton(closeType);
            if (defaultCloseButton != null) {
                defaultCloseButton.setVisible(false);
                defaultCloseButton.setManaged(false);
            }
        });
        alert.showAndWait();
    }

    private void handleDecision(Alert parentAlert, LeaveRequest request, String status, String comment) {
        if (comment == null || comment.trim().length() < 10) {
            showError(LanguageManager.get("leave.adminComment.invalid"));
            return;
        }

        boolean success = LeaveRequestService.updateStatus(request.getId(), status, comment);
        if (success) {
            parentAlert.close();
            loadRequests();
            MainController.refreshOpenShellNotifications();
            showInfo(LanguageManager.get("leave.update.success"));
        } else {
            showError(LanguageManager.get("leave.update.error"));
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
            return LanguageManager.get("leave.type.annual");
        }

        return switch (type) {
            case "Annual Leave" -> LanguageManager.get("leave.type.annual");
            case "Medical Leave" -> LanguageManager.get("leave.type.medical");
            case "Holiday" -> LanguageManager.get("leave.type.holiday");
            default -> type;
        };
    }

    private String displayStatus(LeaveRequest request) {
        String status = request.getStatus();
        if ("Approved".equalsIgnoreCase(status)) {
            return LanguageManager.get("leave.status.approved");
        }
        if ("Rejected".equalsIgnoreCase(status)) {
            return LanguageManager.get("leave.status.rejected");
        }
        return LanguageManager.get("leave.status.pending");
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
