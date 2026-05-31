package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.LeaveRequest;
import com.company.system.model.User;
import com.company.system.service.LeaveRequestService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Kontrolluesi i faqes "Pushimet e mia" për përdoruesin e zakonshëm (USER).
 *
 * Funksionalitetet:
 *  - Shikon listën e kërkesave të veta (Pending / Approved / Rejected)
 *  - Dërgon kërkesë të re pushimi
 *  - Anulon kërkesë që është ende Pending
 */
public class UserLeaveRequestsController {

    // ── FXML fushat ────────────────────────────────────────────────────────
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label approvedDaysLabel;   // Numri i ditëve të aprovuara këtë vit
    @FXML private Label pendingCountLabel;   // Numri i kërkesave në pritje
    @FXML private Button newRequestBtn;      // Butoni "Kërko Pushim"
    @FXML private VBox requestsBox;          // Kontejneri ku renditen rreshtat

    private final ObservableList<LeaveRequest> myRequests = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Gjerësia e kolonave (e njëjtë me admin view për konsistencë)
    private static final double EMPLOYEE_COLUMN_WIDTH = 170;
    private static final double TYPE_COLUMN_WIDTH     = 160;
    private static final double DATE_COLUMN_WIDTH     = 165;
    private static final double DAYS_COLUMN_WIDTH     = 80;

    @FXML
    public void initialize() {
        loadTexts();
        loadMyRequests();
    }

    // ════════════════════════════════════════════════════════════════════════
    // NGARKIMI I TË DHËNAVE
    // ════════════════════════════════════════════════════════════════════════

    private void loadTexts() {
        boolean sq = isAlbanian();
        titleLabel.setText(sq ? "Pushimet e mia" : "My leave requests");
        subtitleLabel.setText(sq
                ? "Shikoni dhe menaxhoni kerkesat tuaja per pushim."
                : "View and manage your leave requests.");
        newRequestBtn.setText(sq ? "+ Kerko Pushim" : "+ Request Leave");
    }

    /**
     * Ngarkon vetëm kërkesat e punonjësit të loguar.
     * Punonjësi shikon vetëm të dhënat e veta — jo të kolegëve.
     */
    private void loadMyRequests() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            showEmptyState(isAlbanian()
                    ? "Llogaria juaj nuk eshte e lidhur me nje punonjës."
                    : "Your account is not linked to an employee.");
            return;
        }

        // Merr kërkesat vetëm për employee_id-në e userit aktual
        myRequests.setAll(LeaveRequestService.getRequestsByEmployee(user.getEmployeeId()));
        renderRequests(myRequests);
        updateStats();
    }

    /**
     * Llogarit dhe shfaq statistikat:
     * - Ditë pushimi të aprovuara këtë vit
     * - Kërkesa në pritje
     */
    private void updateStats() {
        int currentYear = LocalDate.now().getYear();

        // Ditët e aprovuara për vitin aktual
        int approvedDays = myRequests.stream()
                .filter(r -> "Approved".equalsIgnoreCase(r.getStatus()))
                .filter(r -> r.getStartDate() != null &&
                        r.getStartDate().toLocalDate().getYear() == currentYear)
                .mapToInt(LeaveRequest::getWorkingDays)
                .sum();

        // Kërkesat në pritje
        long pendingCount = myRequests.stream()
                .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()))
                .count();

        boolean sq = isAlbanian();
        approvedDaysLabel.setText(approvedDays + " " + (sq ? "dite te aprovuara" : "approved days"));
        pendingCountLabel.setText(pendingCount + " " + (sq ? "ne pritje" : "pending"));
    }

    // ════════════════════════════════════════════════════════════════════════
    // RENDERIMI I LISTËS
    // ════════════════════════════════════════════════════════════════════════

    private void renderRequests(List<LeaveRequest> visible) {
        requestsBox.getChildren().clear();

        if (visible.isEmpty()) {
            showEmptyState(isAlbanian()
                    ? "Nuk keni kerkesa per pushim ende."
                    : "You have no leave requests yet.");
            return;
        }

        requestsBox.getChildren().add(createHeaderRow());
        for (LeaveRequest r : visible) {
            requestsBox.getChildren().add(createRequestRow(r));
        }
    }

    /** Krijon rreshtin e header-it (titujt e kolonave). */
    private HBox createHeaderRow() {
        boolean sq = isAlbanian();
        Label typeH  = createHeaderLabel(sq ? "Lloji" : "Type", TYPE_COLUMN_WIDTH);
        Label dateH  = createHeaderLabel(sq ? "Datat" : "Dates", DATE_COLUMN_WIDTH);
        Label daysH  = createHeaderLabel(sq ? "Dite" : "Days", DAYS_COLUMN_WIDTH);

        HBox info = new HBox(18, typeH, dateH, daysH);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusH  = createHeaderLabel(sq ? "Statusi" : "Status", 82);
        Button spacer = new Button(sq ? "Anulo" : "Cancel");
        spacer.getStyleClass().add("secondary-button");
        spacer.setVisible(false);
        spacer.setManaged(true);

        HBox header = new HBox(14, info, statusH, spacer);
        header.getStyleClass().add("leave-request-header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    /** Krijon një rresht për secilën kërkesë pushimi. */
    private HBox createRequestRow(LeaveRequest r) {
        Label typeLabel = createMetaLabel(displayType(r), TYPE_COLUMN_WIDTH);
        Label dateLabel = createMetaLabel(
                formatDate(r.getStartDate()) + " - " + formatDate(r.getEndDate()),
                DATE_COLUMN_WIDTH);
        Label daysLabel = createMetaLabel(String.valueOf(r.getWorkingDays()), DAYS_COLUMN_WIDTH);

        HBox info = new HBox(18, typeLabel, dateLabel, daysLabel);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Badge i statusit me ngjyrë (green/orange/red)
        Label statusBadge = new Label(displayStatus(r));
        statusBadge.getStyleClass().addAll("status-badge", statusClass(r.getStatus()));

        // Butoni "Anulo" — aktiv vetëm nëse statusi është Pending
        Button cancelBtn = new Button(isAlbanian() ? "Anulo" : "Cancel");
        cancelBtn.getStyleClass().add("secondary-button");
        cancelBtn.setDisable(!"Pending".equalsIgnoreCase(r.getStatus()));
        cancelBtn.setOnAction(e -> confirmCancel(r));

        HBox row = new HBox(14, info, statusBadge, cancelBtn);
        row.getStyleClass().add("leave-request-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Klik mbi rresht për të hapur detajet
        row.setOnMouseClicked(e -> openDetailDialog(r));

        return row;
    }

    // ════════════════════════════════════════════════════════════════════════
    // DIALOGU I DETAJEVE
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Hap dialogun e detajeve të kërkesës.
     * Shfaq arsyen, datat, dhe përgjigjen e adminit nëse ekziston.
     */
    private void openDetailDialog(LeaveRequest r) {
        boolean sq = isAlbanian();

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(sq ? "Detajet e kerkeses" : "Request details");
        alert.setHeaderText(null);

        ButtonType closeType = new ButtonType(sq ? "Mbyll" : "Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(closeType);

        Label titleLbl = new Label(displayType(r));
        titleLbl.getStyleClass().add("section-title");

        Label dateLbl = new Label(
                formatDate(r.getStartDate()) + " — " + formatDate(r.getEndDate())
                        + "  |  " + r.getWorkingDays() + " " + (sq ? "dite pune" : "work days")
                        + "  |  " + displayStatus(r));
        dateLbl.getStyleClass().add("page-subtitle");
        dateLbl.setWrapText(true);

        // Arsyeja e punonjësit
        Label reasonTitle = new Label(sq ? "Arsyeja juaj:" : "Your reason:");
        reasonTitle.getStyleClass().add("field-label");

        Label reasonText = new Label(r.getReason() == null || r.getReason().isBlank()
                ? (sq ? "(pa arsye)" : "(no reason given)") : r.getReason());
        reasonText.getStyleClass().add("body-text");
        reasonText.setWrapText(true);

        VBox content = new VBox(10, titleLbl, dateLbl, reasonTitle, reasonText);

        // Shfaq përgjigjen e adminit nëse ekziston
        if (r.getAdminResponse() != null && !r.getAdminResponse().isBlank()) {
            Label adminTitle = new Label(sq ? "Pergjigja e adminit:" : "Admin response:");
            adminTitle.getStyleClass().add("field-label");
            Label adminText = new Label(r.getAdminResponse());
            adminText.getStyleClass().add("body-text");
            adminText.setWrapText(true);
            content.getChildren().addAll(adminTitle, adminText);
        }

        content.setPadding(new Insets(8));
        content.setPrefWidth(480);
        alert.getDialogPane().setContent(content);

        // Fshih butonin default të JavaFX
        Platform.runLater(() -> {
            Button defaultBtn = (Button) alert.getDialogPane().lookupButton(closeType);
            if (defaultBtn != null) { defaultBtn.setVisible(false); defaultBtn.setManaged(false); }
        });

        // Shto buton të dukshëm "Mbyll"
        Button closeBtn = new Button(sq ? "Mbyll" : "Close");
        closeBtn.getStyleClass().add("secondary-button");
        closeBtn.setOnAction(e -> { alert.setResult(closeType); alert.close(); });
        content.getChildren().add(closeBtn);

        alert.showAndWait();
    }

    // ════════════════════════════════════════════════════════════════════════
    // KËRKESA E RE
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Hap formularin për të dërguar kërkesë të re pushimi.
     * Thirret nga butoni "+ Kërko Pushim".
     */
    @FXML
    private void onNewRequest() {
        boolean sq = isAlbanian();
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            showError(sq ? "Nuk jeni te lidhur me nje punonjës." : "No employee linked to your account.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(sq ? "Kerko Pushim" : "Request Leave");
        alert.setHeaderText(null);

        ButtonType submitType = new ButtonType(sq ? "Dergo" : "Submit", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType(sq ? "Anulo" : "Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(submitType, cancelType);

        // Lloji i pushimit
        Label typeLabel = new Label(sq ? "Lloji i pushimit *" : "Leave type *");
        typeLabel.getStyleClass().add("field-label");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
                sq ? "Pushim vjetor"   : "Annual Leave",
                sq ? "Pushim mjekesor" : "Medical Leave",
                sq ? "Feste"           : "Holiday"
        );
        typeCombo.setValue(sq ? "Pushim vjetor" : "Annual Leave");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        // Data e fillimit
        Label startLabel = new Label(sq ? "Data e fillimit *" : "Start date *");
        startLabel.getStyleClass().add("field-label");
        DatePicker startPicker = new DatePicker(LocalDate.now().plusDays(1));
        startPicker.setMaxWidth(Double.MAX_VALUE);

        // Data e përfundimit
        Label endLabel = new Label(sq ? "Data e mbarimit *" : "End date *");
        endLabel.getStyleClass().add("field-label");
        DatePicker endPicker = new DatePicker(LocalDate.now().plusDays(1));
        endPicker.setMaxWidth(Double.MAX_VALUE);

        // Arsyeja
        Label reasonLabel = new Label(sq ? "Arsyeja (opsionale)" : "Reason (optional)");
        reasonLabel.getStyleClass().add("field-label");
        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText(sq ? "Shkruani arsyen e kerkeses..." : "Enter reason for your request...");
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(3);

        Label errorLbl = new Label();
        errorLbl.getStyleClass().add("error-label");
        errorLbl.setVisible(false);
        errorLbl.setWrapText(true);

        VBox content = new VBox(10,
                typeLabel, typeCombo,
                startLabel, startPicker,
                endLabel, endPicker,
                reasonLabel, reasonArea,
                errorLbl);
        content.setPadding(new Insets(8));
        content.setPrefWidth(400);
        alert.getDialogPane().setContent(content);

        // Proceso dërgimin
        alert.showAndWait().ifPresent(result -> {
            if (result != submitType) return;

            // Validimi
            if (startPicker.getValue() == null || endPicker.getValue() == null) {
                showError(sq ? "Datat jane te detyrueshme." : "Dates are required."); return;
            }
            if (endPicker.getValue().isBefore(startPicker.getValue())) {
                showError(sq ? "Data e mbarimit nuk mund te jete para fillimit." : "End date cannot be before start date."); return;
            }

            // Konverto llojin nga shqip në anglisht për DB
            String typeEn = switch (typeCombo.getValue()) {
                case "Pushim vjetor", "Annual Leave"     -> "Annual Leave";
                case "Pushim mjekesor", "Medical Leave"  -> "Medical Leave";
                case "Feste", "Holiday"                  -> "Holiday";
                default                                   -> typeCombo.getValue();
            };

            boolean ok = LeaveRequestService.submitRequest(
                    user.getEmployeeId(),
                    typeEn,
                    startPicker.getValue(),
                    endPicker.getValue(),
                    reasonArea.getText().trim()
            );

            if (ok) {
                showInfo(sq ? "Kerkesa u dergua me sukses!" : "Request submitted successfully!");
                loadMyRequests(); // Ringarko listën
            } else {
                showError(sq ? "Gabim gjate dergimit. Provoni perseri." : "Failed to submit. Please try again.");
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // ANULIMI I KËRKESËS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Kërkon konfirmim dhe anulon kërkesën nëse statusi është Pending.
     */
    private void confirmCancel(LeaveRequest r) {
        boolean sq = isAlbanian();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtils.style(confirm);
        confirm.setTitle(sq ? "Anulo kerkesen" : "Cancel request");
        confirm.setHeaderText(null);
        confirm.setContentText(sq
                ? "A jeni te sigurt qe doni ta anuloni kerkesen per " + displayType(r) + "?"
                : "Are you sure you want to cancel the " + displayType(r) + " request?");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = LeaveRequestService.cancelRequest(r.getId());
                if (ok) {
                    showInfo(sq ? "Kerkesa u anulua." : "Request cancelled.");
                    loadMyRequests();
                } else {
                    showError(sq ? "Gabim gjate anulimit." : "Failed to cancel request.");
                }
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // METODA NDIHMËSE
    // ════════════════════════════════════════════════════════════════════════

    private void showEmptyState(String msg) {
        requestsBox.getChildren().clear();
        Label empty = new Label(msg);
        empty.getStyleClass().add("empty-state-text");
        VBox card = new VBox(empty);
        card.getStyleClass().add("content-card");
        requestsBox.getChildren().add(card);
    }

    private Label createHeaderLabel(String text, double width) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("leave-request-header-label");
        lbl.setPrefWidth(width);
        lbl.setMaxWidth(width);
        return lbl;
    }

    private Label createMetaLabel(String text, double width) {
        Label lbl = new Label(text == null || text.isBlank() ? "-" : text);
        lbl.getStyleClass().add("leave-request-meta");
        lbl.setPrefWidth(width);
        lbl.setMaxWidth(width);
        return lbl;
    }

    /** Konverton llojin e pushimit nga anglisht në gjuhën aktuale. */
    private String displayType(LeaveRequest r) {
        return switch (r.getRequestType() == null ? "" : r.getRequestType()) {
            case "Annual Leave"  -> isAlbanian() ? "Pushim vjetor"   : "Annual Leave";
            case "Medical Leave" -> isAlbanian() ? "Pushim mjekesor" : "Medical Leave";
            case "Holiday"       -> isAlbanian() ? "Feste"           : "Holiday";
            default              -> r.getRequestType();
        };
    }

    /** Konverton statusin nga anglisht në gjuhën aktuale. */
    private String displayStatus(LeaveRequest r) {
        return switch (r.getStatus() == null ? "" : r.getStatus()) {
            case "Approved" -> isAlbanian() ? "Pranuar"  : "Approved";
            case "Rejected" -> isAlbanian() ? "Refuzuar" : "Rejected";
            default         -> isAlbanian() ? "Ne pritje" : "Pending";
        };
    }

    /** Kthen CSS class-in sipas statusit për ngjyrën e badge-it. */
    private String statusClass(String status) {
        return switch (status == null ? "" : status) {
            case "Approved" -> "approved";
            case "Rejected" -> "rejected";
            default         -> "pending";
        };
    }

    private String formatDate(java.sql.Date date) {
        if (date == null) return "-";
        return date.toLocalDate().format(dateFormatter);
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        DialogUtils.style(a);
        a.setTitle(LanguageManager.get("message.success.title"));
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(a);
        a.setTitle(LanguageManager.get("message.error.title"));
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }
}
