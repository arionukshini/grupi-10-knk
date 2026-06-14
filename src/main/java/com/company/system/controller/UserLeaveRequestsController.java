package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.models.LeaveRequest;
import com.company.system.models.User;
import com.company.system.service.LeaveRequestService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class UserLeaveRequestsController {

    private static final double TYPE_COLUMN_WIDTH = 160;
    private static final double DATE_COLUMN_WIDTH = 165;
    private static final double DAYS_COLUMN_WIDTH = 80;

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label approvedDaysLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Button newRequestBtn;
    @FXML private VBox requestsBox;

    private final ObservableList<LeaveRequest> myRequests = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        loadTexts();
        loadMyRequests();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("leave.user.title"));
        subtitleLabel.setText(LanguageManager.get("leave.user.subtitle"));
        newRequestBtn.setText("+ " + LanguageManager.get("leave.user.new"));
    }

    private void loadMyRequests() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            showEmptyState(LanguageManager.get("leave.user.notLinked"));
            return;
        }

        myRequests.setAll(LeaveRequestService.getRequestsByEmployee(user.getEmployeeId()));
        renderRequests(myRequests);
        updateStats();
    }

    private void updateStats() {
        int currentYear = LocalDate.now().getYear();

        int approvedDays = myRequests.stream()
                .filter(request -> "Approved".equalsIgnoreCase(request.getStatus()))
                .filter(request -> request.getStartDate() != null
                        && request.getStartDate().toLocalDate().getYear() == currentYear)
                .mapToInt(LeaveRequest::getWorkingDays)
                .sum();

        long pendingCount = myRequests.stream()
                .filter(request -> "Pending".equalsIgnoreCase(request.getStatus()))
                .count();

        approvedDaysLabel.setText(String.format(LanguageManager.get("leave.user.approvedDays"), approvedDays));
        pendingCountLabel.setText(String.format(LanguageManager.get("leave.user.pendingCount"), pendingCount));
    }

    private void renderRequests(List<LeaveRequest> visibleRequests) {
        requestsBox.getChildren().clear();

        if (visibleRequests.isEmpty()) {
            showEmptyState(LanguageManager.get("leave.user.empty"));
            return;
        }

        requestsBox.getChildren().add(createHeaderRow());
        for (LeaveRequest request : visibleRequests) {
            requestsBox.getChildren().add(createRequestRow(request));
        }
    }

    private HBox createHeaderRow() {
        Label typeHeader = createHeaderLabel(LanguageManager.get("leave.type"), TYPE_COLUMN_WIDTH);
        Label dateHeader = createHeaderLabel(LanguageManager.get("leave.dates"), DATE_COLUMN_WIDTH);
        Label daysHeader = createHeaderLabel(LanguageManager.get("leave.days"), DAYS_COLUMN_WIDTH);

        HBox info = new HBox(18, typeHeader, dateHeader, daysHeader);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusHeader = createHeaderLabel(LanguageManager.get("leave.status"), 82);
        Button spacer = new Button(LanguageManager.get("leave.cancel"));
        spacer.getStyleClass().add("secondary-button");
        spacer.setVisible(false);
        spacer.setManaged(true);

        HBox header = new HBox(14, info, statusHeader, spacer);
        header.getStyleClass().add("leave-request-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox createRequestRow(LeaveRequest request) {
        Label typeLabel = createMetaLabel(displayType(request), TYPE_COLUMN_WIDTH);
        Label dateLabel = createMetaLabel(
                formatDate(request.getStartDate()) + " - " + formatDate(request.getEndDate()),
                DATE_COLUMN_WIDTH);
        Label daysLabel = createMetaLabel(String.valueOf(request.getWorkingDays()), DAYS_COLUMN_WIDTH);

        HBox info = new HBox(18, typeLabel, dateLabel, daysLabel);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusBadge = new Label(displayStatus(request));
        statusBadge.getStyleClass().addAll("status-badge", statusClass(request.getStatus()));

        Button cancelButton = new Button(LanguageManager.get("leave.cancel"));
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setDisable(!"Pending".equalsIgnoreCase(request.getStatus()));
        cancelButton.setOnAction(event -> confirmCancel(request));

        HBox row = new HBox(14, info, statusBadge, cancelButton);
        row.getStyleClass().add("leave-request-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseClicked(event -> openDetailDialog(request));
        return row;
    }

    private void openDetailDialog(LeaveRequest request) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("leave.details.title"));
        alert.setHeaderText(null);

        ButtonType closeType = new ButtonType(LanguageManager.get("leave.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(closeType);

        Label title = new Label(displayType(request));
        title.getStyleClass().add("section-title");

        Label date = new Label(formatDate(request.getStartDate())
                + " - " + formatDate(request.getEndDate())
                + " | " + request.getWorkingDays() + " "
                + LanguageManager.get("leave.workDays").toLowerCase(Locale.ROOT)
                + " | " + displayStatus(request));
        date.getStyleClass().add("page-subtitle");
        date.setWrapText(true);

        Label reasonTitle = new Label(LanguageManager.get("leave.reason.title"));
        reasonTitle.getStyleClass().add("field-label");

        Label reason = new Label(request.getReason() == null || request.getReason().isBlank()
                ? LanguageManager.get("leave.reason.none")
                : request.getReason());
        reason.getStyleClass().add("body-text");
        reason.setWrapText(true);

        VBox content = new VBox(10, title, date, reasonTitle, reason);

        if (request.getAdminResponse() != null && !request.getAdminResponse().isBlank()) {
            Label adminTitle = new Label(LanguageManager.get("leave.adminResponse"));
            adminTitle.getStyleClass().add("field-label");
            Label adminResponse = new Label(request.getAdminResponse());
            adminResponse.getStyleClass().add("body-text");
            adminResponse.setWrapText(true);
            content.getChildren().addAll(adminTitle, adminResponse);
        }

        Button closeButton = new Button(LanguageManager.get("leave.close"));
        closeButton.getStyleClass().add("secondary-button");
        closeButton.setOnAction(event -> {
            alert.setResult(closeType);
            alert.close();
        });
        content.getChildren().add(closeButton);

        content.setPadding(new Insets(8));
        content.setPrefWidth(480);
        alert.getDialogPane().setContent(content);

        Platform.runLater(() -> {
            Button defaultButton = (Button) alert.getDialogPane().lookupButton(closeType);
            if (defaultButton != null) {
                defaultButton.setVisible(false);
                defaultButton.setManaged(false);
            }
        });

        alert.showAndWait();
    }

    @FXML
    private void onNewRequest() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            showError(LanguageManager.get("leave.user.notLinked"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("leave.form.title"));
        alert.setHeaderText(null);

        ButtonType submitType = new ButtonType(LanguageManager.get("leave.submit"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType(LanguageManager.get("leave.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(submitType, cancelType);

        Label typeLabel = new Label(LanguageManager.get("leave.form.type"));
        typeLabel.getStyleClass().add("field-label");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
                LanguageManager.get("leave.type.annual"),
                LanguageManager.get("leave.type.medical"),
                LanguageManager.get("leave.type.holiday")
        );
        typeCombo.setValue(LanguageManager.get("leave.type.annual"));
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        Label startLabel = new Label(LanguageManager.get("leave.form.startDate"));
        startLabel.getStyleClass().add("field-label");
        DatePicker startPicker = new DatePicker(LocalDate.now().plusDays(1));
        startPicker.setMaxWidth(Double.MAX_VALUE);

        Label endLabel = new Label(LanguageManager.get("leave.form.endDate"));
        endLabel.getStyleClass().add("field-label");
        DatePicker endPicker = new DatePicker(LocalDate.now().plusDays(1));
        endPicker.setMaxWidth(Double.MAX_VALUE);

        Label reasonLabel = new Label(LanguageManager.get("leave.form.reason"));
        reasonLabel.getStyleClass().add("field-label");
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText(LanguageManager.get("leave.form.reasonPrompt"));
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(3);

        VBox content = new VBox(10,
                typeLabel, typeCombo,
                startLabel, startPicker,
                endLabel, endPicker,
                reasonLabel, reasonArea);
        content.setPadding(new Insets(8));
        content.setPrefWidth(400);
        alert.getDialogPane().setContent(content);

        alert.showAndWait().ifPresent(result -> {
            if (result != submitType) {
                return;
            }

            if (startPicker.getValue() == null || endPicker.getValue() == null) {
                showError(LanguageManager.get("leave.validation.datesRequired"));
                return;
            }
            if (endPicker.getValue().isBefore(startPicker.getValue())) {
                showError(LanguageManager.get("leave.validation.endBeforeStart"));
                return;
            }

            boolean success = LeaveRequestService.submitRequest(
                    user.getEmployeeId(),
                    toDatabaseType(typeCombo.getValue()),
                    startPicker.getValue(),
                    endPicker.getValue(),
                    reasonArea.getText().trim()
            );

            if (success) {
                showInfo(LanguageManager.get("leave.submit.success"));
                loadMyRequests();
            } else {
                showError(LanguageManager.get("leave.submit.error"));
            }
        });
    }

    private void confirmCancel(LeaveRequest request) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtils.style(confirm);
        confirm.setTitle(LanguageManager.get("leave.cancel.title"));
        confirm.setHeaderText(null);
        confirm.setContentText(String.format(LanguageManager.get("leave.cancel.confirm"), displayType(request)));

        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                boolean success = LeaveRequestService.cancelRequest(request.getId());
                if (success) {
                    showInfo(LanguageManager.get("leave.cancel.success"));
                    loadMyRequests();
                } else {
                    showError(LanguageManager.get("leave.cancel.error"));
                }
            }
        });
    }

    private void showEmptyState(String message) {
        requestsBox.getChildren().clear();
        Label empty = new Label(message);
        empty.getStyleClass().add("empty-state-text");
        VBox card = new VBox(empty);
        card.getStyleClass().add("content-card");
        requestsBox.getChildren().add(card);
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.getStyleClass().add("leave-request-header-label");
        label.setPrefWidth(width);
        label.setMaxWidth(width);
        return label;
    }

    private Label createMetaLabel(String text, double width) {
        Label label = new Label(text == null || text.isBlank() ? "-" : text);
        label.getStyleClass().add("leave-request-meta");
        label.setPrefWidth(width);
        label.setMaxWidth(width);
        return label;
    }

    private String displayType(LeaveRequest request) {
        return switch (request.getRequestType() == null ? "" : request.getRequestType()) {
            case "Annual Leave" -> LanguageManager.get("leave.type.annual");
            case "Medical Leave" -> LanguageManager.get("leave.type.medical");
            case "Holiday" -> LanguageManager.get("leave.type.holiday");
            default -> request.getRequestType();
        };
    }

    private String toDatabaseType(String displayType) {
        if (LanguageManager.get("leave.type.medical").equals(displayType)) {
            return "Medical Leave";
        }
        if (LanguageManager.get("leave.type.holiday").equals(displayType)) {
            return "Holiday";
        }
        return "Annual Leave";
    }

    private String displayStatus(LeaveRequest request) {
        return switch (request.getStatus() == null ? "" : request.getStatus()) {
            case "Approved" -> LanguageManager.get("leave.status.approved");
            case "Rejected" -> LanguageManager.get("leave.status.rejected");
            default -> LanguageManager.get("leave.status.pending");
        };
    }

    private String statusClass(String status) {
        return switch (status == null ? "" : status) {
            case "Approved" -> "approved";
            case "Rejected" -> "rejected";
            default -> "pending";
        };
    }

    private String formatDate(java.sql.Date date) {
        if (date == null) {
            return "-";
        }
        return date.toLocalDate().format(dateFormatter);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("message.success.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("message.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
