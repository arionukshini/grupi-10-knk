package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Contract;
import com.company.system.model.Department;
import com.company.system.model.Employee;
import com.company.system.model.LeaveRequest;
import com.company.system.model.Salary;
import com.company.system.model.User;
import com.company.system.service.ContractService;
import com.company.system.service.DepartmentService;
import com.company.system.service.EmployeeService;
import com.company.system.service.LeaveRequestService;
import com.company.system.service.SalaryService;
import com.company.system.utils.Session;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.layout.StackPane;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label employeeTitleLabel;
    @FXML private Label contractTitleLabel;
    @FXML private Label salaryTitleLabel;
    @FXML private Label departmentTitleLabel;
    @FXML private Label colleaguesTitleLabel;

    @FXML private VBox employeeDetailsBox;
    @FXML private VBox contractDetailsBox;
    @FXML private VBox salaryDetailsBox;
    @FXML private VBox departmentDetailsBox;

    @FXML private TableView<Employee> colleaguesTable;
    @FXML private TableColumn<Employee, String> colleagueNameColumn;
    @FXML private TableColumn<Employee, String> colleaguePositionColumn;
    @FXML private TableColumn<Employee, String> colleagueEmailColumn;
    @FXML private TableColumn<Employee, String> colleagueStatusColumn;


    @FXML private Label greetingLabel;        // "Pershendetje, Arjanita!"
    @FXML private VBox documentsBox;          // Dokumentet e rendesishme
    @FXML private VBox notificationsBox;      // Njoftime te fundit (pushimet)

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        loadTexts();
        setupColleaguesTable();
        loadUserDashboard();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("dashboard.title"));
        subtitleLabel.setText(isAlbanian()
                ? "Permbledhje personale e punes, kontrates, pages dhe departamentit tuaj."
                : "Personal overview of your work, contract, salary and department.");
        employeeTitleLabel.setText(isAlbanian() ? "Informata personale" : "Personal details");
        contractTitleLabel.setText(LanguageManager.get("profile.contractInfo"));
        if (salaryTitleLabel != null)
            salaryTitleLabel.setText(isAlbanian() ? "Informata te pages" : "Salary information");
        departmentTitleLabel.setText(LanguageManager.get("profile.departmentInfo"));
        colleaguesTitleLabel.setText(LanguageManager.get("profile.colleagues"));
    }

    private void setupColleaguesTable() {
        colleaguesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colleaguesTable.setPlaceholder(new Label(LanguageManager.get("profile.noColleagues")));
        colleagueNameColumn.setText(LanguageManager.get("profile.colleagueName"));
        colleagueNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                valueOrDash(cellData.getValue().getFirstName()) + " " + valueOrDash(cellData.getValue().getLastName())));
        colleaguePositionColumn.setText(LanguageManager.get("profile.colleaguePosition"));
        colleaguePositionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        colleagueEmailColumn.setText(LanguageManager.get("profile.colleagueEmail"));
        colleagueEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        colleagueStatusColumn.setText(LanguageManager.get("profile.colleagueStatus"));
        colleagueStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadUserDashboard() {
        User user = Session.getUser();


        if (greetingLabel != null) {
            String name = user != null ? user.getUsername() : "-";
            greetingLabel.setText((isAlbanian() ? "Pershendetje, " : "Welcome, ") + name + "!");
        }

        if (user == null || user.getEmployeeId() == null) {
            employeeDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("message.userNotFound")));
            contractDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("profile.noContract")));
            loadSalaryDetails(null);
            departmentDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("profile.noDepartment")));
            colleaguesTable.setItems(FXCollections.observableArrayList());
            loadDocuments(null);
            loadNotifications(null);
            return;
        }

        Employee employee   = EmployeeService.getEmployeeById(user.getEmployeeId());
        Department department = employee == null ? null : DepartmentService.getDepartmentById(employee.getDepartmentId());
        Contract contract   = ContractService.getLatestContractByEmployeeId(user.getEmployeeId());
        Salary salary       = SalaryService.getLatestSalaryByEmployeeId(user.getEmployeeId());

        List<Employee> colleagues = new ArrayList<>();
        if (department != null && employee != null)
            colleagues = EmployeeService.getEmployeesByDepartment(department.getId(), employee.getId());

        loadEmployeeDetails(employee, department);
        loadContractDetails(contract);
        loadSalaryDetails(salary);
        loadDepartmentDetails(department);
        colleaguesTable.setItems(FXCollections.observableArrayList(colleagues));


        loadDocuments(employee);
        loadNotifications(user);
    }

    private void loadEmployeeDetails(Employee employee, Department department) {
        employeeDetailsBox.getChildren().setAll(
                createDetail(LanguageManager.get("profile.position") + ": " + valueOrDash(employee == null ? null : employee.getPosition())),
                createDetail(LanguageManager.get("profile.hireDate") + ": " + formatSqlDate(employee == null ? null : employee.getHireDate())),
                createDetail(LanguageManager.get("profile.baseSalary") + ": " + formatCurrency(employee == null ? 0 : employee.getBaseSalary())),
                createDetail(LanguageManager.get("profile.employeeStatus") + ": " + valueOrDash(employee == null ? null : employee.getStatus())),
                createDetail(LanguageManager.get("profile.department") + ": " + valueOrDash(department == null ? null : department.getName()))
        );
    }

    private void loadContractDetails(Contract contract) {
        if (contract == null) {
            contractDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("profile.noContract")));
            return;
        }
        contractDetailsBox.getChildren().setAll(
                createDetail(LanguageManager.get("profile.contractType") + ": " + valueOrDash(contract.getContractType())),
                createDetail(LanguageManager.get("profile.contractStart") + ": " + formatSqlDate(contract.getStartDate())),
                createDetail(LanguageManager.get("profile.contractEnd") + ": " + formatSqlDate(contract.getEndDate())),
                createDetail(LanguageManager.get("profile.contractStatus") + ": " + valueOrDash(contract.getStatus())),
                createDetail(LanguageManager.get("contracts.salary") + ": " + formatCurrency(contract.getSalary()))
        );
    }

    private void loadSalaryDetails(Salary salary) {
        if (salaryDetailsBox == null) return;
        if (salary == null) {
            salaryDetailsBox.getChildren().setAll(createDetail(
                    isAlbanian() ? "Nuk u gjeten informata per page." : "No salary information found."));
            return;
        }
        salaryDetailsBox.getChildren().setAll(
                createDetail((isAlbanian() ? "Paga bruto" : "Gross salary") + ": " + formatCurrency(salary.getGrossSalary())),
                createDetail((isAlbanian() ? "Bonusi" : "Bonus") + ": " + formatCurrency(salary.getBonus())),
                createDetail((isAlbanian() ? "Zbritjet" : "Deductions") + ": " + formatCurrency(salary.getDeductions())),
                createDetail((isAlbanian() ? "Paga neto" : "Net salary") + ": " + formatCurrency(salary.getNetSalary())),
                createDetail((isAlbanian() ? "Data e pageses" : "Payment date") + ": " + formatSqlDate(salary.getPaymentDate()))
        );
    }

    private void loadDepartmentDetails(Department department) {
        if (department == null) {
            departmentDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("profile.noDepartment")));
            return;
        }
        departmentDetailsBox.getChildren().setAll(
                createDetail(LanguageManager.get("departments.name") + ": " + valueOrDash(department.getName())),
                createDetail(LanguageManager.get("departments.description") + ": " + valueOrDash(department.getDescription()))
        );
    }

    private void loadDocuments(Employee employee) {
        if (documentsBox == null) return;
        documentsBox.getChildren().clear();

        String[][] docs = {
                {isAlbanian() ? "Kontrata e punes"              : "Work Contract",            "contract"},
                {isAlbanian() ? "Marreveshja e konfidencialitetit" : "Confidentiality Agreement","confidentiality"},
                {isAlbanian() ? "Rregullorja e punes"           : "Work Regulations",          "regulations"},
                {isAlbanian() ? "Politikat e sigurise se informacionit" : "Information Security Policy", "security"},
                {isAlbanian() ? "Mbrojtja e te dhenave personale" : "Personal Data Protection", "data"},
                {isAlbanian() ? "Politikat e tavolines se paster" : "Clean Desk Policy",        "cleandesk"},
        };


        HBox row = null;
        for (int i = 0; i < docs.length; i++) {
            if (i % 2 == 0) {
                row = new HBox(12);
                row.setMaxWidth(Double.MAX_VALUE);
                documentsBox.getChildren().add(row);
            }
            HBox.setHgrow(createDocCard(docs[i][0], docs[i][1]), Priority.ALWAYS);
            row.getChildren().add(createDocCard(docs[i][0], docs[i][1]));
            HBox.setHgrow(row.getChildren().get(row.getChildren().size() - 1), Priority.ALWAYS);
        }
    }


    private HBox createDocCard(String title, String type) {
        Label name = new Label(title);
        name.setWrapText(true);
        name.getStyleClass().add("body-text");
        HBox.setHgrow(name, Priority.ALWAYS);

        Button readBtn = new Button(isAlbanian() ? "Lexo" : "Read");
        readBtn.getStyleClass().add("btn-secondary");
        readBtn.setOnAction(e -> showDocumentInfo(title));

        HBox card = new HBox(12, name, readBtn);
        card.getStyleClass().add("content-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }


    private void showDocumentInfo(String title) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(isAlbanian()
                ? "Dokumenti \"" + title + "\" eshte ne dispozicion. Kontaktoni HR per me shume informacion."
                : "Document \"" + title + "\" is available. Contact HR for more information.");
        alert.showAndWait();
    }

    private void loadNotifications(User user) {
        if (notificationsBox == null) return;
        notificationsBox.getChildren().clear();

        if (user == null || user.getEmployeeId() == null) {
            notificationsBox.getChildren().add(createEmptyNotif());
            return;
        }

        List<LeaveRequest> requests = LeaveRequestService.getRequestsByEmployee(user.getEmployeeId());

        if (requests.isEmpty()) {
            notificationsBox.getChildren().add(createEmptyNotif());
            return;
        }


        int count = Math.min(requests.size(), 8);
        for (int i = 0; i < count; i++) {
            notificationsBox.getChildren().add(createNotifRow(requests.get(i)));
        }
    }


    private VBox createNotifRow(LeaveRequest r) {
        String status   = r.getStatus() == null ? "Pending" : r.getStatus();
        String dotColor = switch (status) {
            case "Approved" -> "#22c55e";
            case "Rejected" -> "#ef4444";
            default          -> "#f97316";
        };


        StackPane dot = new StackPane();
        dot.setMinSize(10, 10);
        dot.setMaxSize(10, 10);
        dot.setStyle("-fx-background-color: " + dotColor + "; -fx-background-radius: 5;");


        String statusText = switch (status) {
            case "Approved" -> isAlbanian() ? "Kerkesa juaj per pushim eshte aprovuar" : "Your leave request was approved";
            case "Rejected" -> isAlbanian() ? "Kerkesa juaj per pushim eshte refuzuar" : "Your leave request was rejected";
            default          -> isAlbanian() ? "Kerkesa juaj per pushim eshte ne pritje" : "Your leave request is pending";
        };

        Label titleLbl = new Label(statusText);
        titleLbl.getStyleClass().add("notification-title");
        titleLbl.setWrapText(true);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");


        String dateRange = formatDate(r.getStartDate()) + " - " + formatDate(r.getEndDate());
        String type      = displayType(r);
        String detail    = isAlbanian()
                ? "Pushimi " + dateRange + " (" + type + ") eshte " + statusText.toLowerCase() + "."
                : "Leave " + dateRange + " (" + type + ") status: " + status + ".";

        Label detailLbl = new Label(detail);
        detailLbl.getStyleClass().add("body-text");
        detailLbl.setWrapText(true);
        detailLbl.setStyle("-fx-font-size: 11.5px; -fx-opacity: 0.75;");


        Label timeLbl = new Label(formatTimestamp(r.getRequestedAt()));
        timeLbl.setStyle("-fx-font-size: 11px; -fx-opacity: 0.5;");

        HBox header = new HBox(8, dot, titleLbl);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox row = new VBox(4, header, detailLbl, timeLbl);
        row.getStyleClass().add("notification-row");
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-border-color: -border; -fx-border-width: 0 0 1 0;");

        return row;
    }

    private VBox createEmptyNotif() {
        Label lbl = new Label(isAlbanian()
                ? "Nuk keni njoftime aktualisht."
                : "No notifications at this time.");
        lbl.getStyleClass().add("body-text");
        VBox box = new VBox(lbl);
        box.setPadding(new Insets(16));
        return box;
    }



    private String displayType(LeaveRequest r) {
        return switch (r.getRequestType() == null ? "" : r.getRequestType()) {
            case "Annual Leave"  -> isAlbanian() ? "Pushim vjetor"   : "Annual Leave";
            case "Medical Leave" -> isAlbanian() ? "Pushim mjekesor" : "Medical Leave";
            case "Holiday"       -> isAlbanian() ? "Feste"           : "Holiday";
            default              -> r.getRequestType();
        };
    }

    private String formatDate(java.sql.Date date) {
        if (date == null) return "-";
        return date.toLocalDate().format(DATE_FMT);
    }

    private String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        long diff = System.currentTimeMillis() - ts.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        if (days == 0) return isAlbanian() ? "sot" : "today";
        if (days == 1) return isAlbanian() ? "dje" : "yesterday";
        return days + (isAlbanian() ? " dite me pare" : " days ago");
    }

    private Label createDetail(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("profile-detail");
        return label;
    }

    private String formatSqlDate(Date date) {
        if (date == null) return "-";
        return date.toLocalDate().format(DATE_FMT);
    }

    private String formatCurrency(double value) {
        return String.format("%.2f EUR", value);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }
}