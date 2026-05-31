package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Contract;
import com.company.system.model.Department;
import com.company.system.model.Employee;
import com.company.system.model.LeaveRequest;
import com.company.system.model.Salary;
import com.company.system.model.User;
import com.company.system.service.ContractPdfService;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
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
    @FXML private Label greetingLabel;
    @FXML private VBox documentsBox;
    @FXML private VBox notificationsBox;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Employee currentEmployee;
    private Contract currentContract;

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
                valueOrDash(cellData.getValue().getFirstName()) + " " +
                        valueOrDash(cellData.getValue().getLastName())));
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
            loadDocuments(null, null);
            loadNotifications(null);
            return;
        }

        currentEmployee = EmployeeService.getEmployeeById(user.getEmployeeId());
        Department department = currentEmployee == null ? null :
                DepartmentService.getDepartmentById(currentEmployee.getDepartmentId());
        currentContract = ContractService.getLatestContractByEmployeeId(user.getEmployeeId());
        Salary salary = SalaryService.getLatestSalaryByEmployeeId(user.getEmployeeId());

        List<Employee> colleagues = new ArrayList<>();
        if (department != null && currentEmployee != null)
            colleagues = EmployeeService.getEmployeesByDepartment(
                    department.getId(), currentEmployee.getId());

        loadEmployeeDetails(currentEmployee, department);
        loadContractDetails(currentContract);
        loadSalaryDetails(salary);
        loadDepartmentDetails(department);
        colleaguesTable.setItems(FXCollections.observableArrayList(colleagues));
        loadDocuments(currentEmployee, currentContract);
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

    private void loadDocuments(Employee employee, Contract contract) {
        if (documentsBox == null) return;
        documentsBox.getChildren().clear();

        String[][] docs = {
                {"Kontrata e punes", "contract"},
                {"Rregullorja e punes", "regulations"},
                {"Mbrojtja e te dhenave personale", "data"},
        };

        HBox row = null;
        for (int i = 0; i < docs.length; i++) {
            if (i % 2 == 0) {
                row = new HBox(12);
                row.setMaxWidth(Double.MAX_VALUE);
                documentsBox.getChildren().add(row);
            }
            HBox card = createDocCard(docs[i][0], docs[i][1], employee, contract);
            HBox.setHgrow(card, Priority.ALWAYS);
            row.getChildren().add(card);
        }
    }

    private HBox createDocCard(String title, String type, Employee employee, Contract contract) {
        Label name = new Label(title);
        name.setWrapText(true);
        name.getStyleClass().add("body-text");
        HBox.setHgrow(name, Priority.ALWAYS);

        Button readBtn = new Button(isAlbanian() ? "Lexo" : "Read");
        readBtn.getStyleClass().add("btn-secondary");

        switch (type) {
            case "contract"     -> readBtn.setOnAction(e -> openContractPdf(title, employee, contract));
            case "regulations"  -> readBtn.setOnAction(e -> openDocumentDialog(title, buildRegulationsText(employee)));
            case "data"         -> readBtn.setOnAction(e -> openDocumentDialog(title, buildDataProtectionText(employee)));
        }

        HBox card = new HBox(12, name, readBtn);
        card.getStyleClass().add("content-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void openContractPdf(String title, Employee employee, Contract contract) {
        if (employee == null || contract == null) {
            showInfo(isAlbanian() ? "Nuk ka kontrate aktive per te shfaqur." : "No active contract found.");
            return;
        }
        try {
            byte[] pdfBytes = ContractPdfService.generateContractPdf(employee, contract);
            File tempFile = File.createTempFile("kontrata_", ".pdf");
            tempFile.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdfBytes);
            }
            java.awt.Desktop.getDesktop().open(tempFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(isAlbanian() ? "Gabim gjate gjenerimit te PDF-se." : "Error generating PDF.");
        }
    }

    private void openDocumentDialog(String title, String content) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f2850;");
        titleLbl.setWrapText(true);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI'; -fx-background-color: white;");
        VBox.setVgrow(textArea, Priority.ALWAYS);

        Button closeBtn = new Button(isAlbanian() ? "Mbyll" : "Close");
        closeBtn.getStyleClass().add("btn-secondary");
        closeBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(16, titleLbl, textArea, closeBtn);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");
        VBox.setVgrow(textArea, Priority.ALWAYS);

        stage.setScene(new Scene(root, 760, 640));
        stage.show();
    }

    private String buildRegulationsText(Employee employee) {
        String name = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "-";
        return """
RREGULLORJA E BRENDSHME E PUNËS
Centrix Solutions SH.P.K

PËRMBAJTJA

KREU I – DISPOZITAT E PËRGJITHSHME

Neni 1 – QËLLIMI
Kjo Rregullore e Brendshme e Punës rregullon të drejtat dhe detyrimet e punëdhënësit dhe punonjësve gjatë marrëdhënies së punës.

Neni 2 – FUSHËVEPRIMI
Kjo rregullore zbatohet për të gjithë punonjësit e Centrix Solutions SH.P.K, duke përfshirë:
  - Punonjës me kohë të plotë
  - Punonjës me kohë të pjesshme
  - Punonjës me kontratë të afatizuar

Neni 3 – PËRKUFIZIMET
"Kompania" nënkupton Centrix Solutions SH.P.K.
"Punonjësi" nënkupton çdo person i angazhuar me kontratë pune.
"Menaxheri" nënkupton mbikëqyrësin direkt të punonjësit.

KREU II – RREGULLAT E SJELLJES

Neni 4 – ETIKA DHE KOMUNIKIMI
Çdo punonjës është i detyruar të:
  - Silleni me respekt ndaj kolegëve dhe klientëve
  - Komunikoni në mënyrë profesionale
  - Ruani konfidencialitetin e informacioneve të kompanisë

Neni 5 – ORARI I PUNËS
  - Orari standard: 08:00 - 16:00, e Hënë deri të Premte
  - Çdo ndryshim i orarit duhet miratuar nga menaxheri

Neni 6 – PUSHIMET DHE LEJET
  - Pushimi vjetor: sipas legjislacionit në fuqi
  - Leja mjekësore: me vërtetim mjekësor
  - Leja për raste familjare: sipas Ligjit të Punës

KREU III – DISPOZITAT FINALE

Neni 7
Kjo rregullore hyn në fuqi nga data e nënshkrimit dhe zbatohet për të gjithë punonjësit.

─────────────────────────────────────
Centrix Solutions SH.P.K
Rr. Lidhjes së Prizrenit, Nr. 15, Prishtinë
""";
    }

    private String buildDataProtectionText(Employee employee) {
        String name = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "-";
        return """
POLITIKA E MBROJTJES SË TË DHËNAVE PERSONALE
Centrix Solutions SH.P.K

1. HYRJE
Centrix Solutions SH.P.K është e angazhuar për mbrojtjen e të dhënave personale të punonjësve, klientëve dhe partnerëve, në përputhje me legjislacionin në fuqi.

2. TË DHËNAT QË MBLIDHEN
Kompania mbledh dhe përpunon:
  - Të dhëna identifikuese (emri, mbiemri, numri personal)
  - Të dhëna kontaktuese (adresa, telefoni, email-i)
  - Të dhëna të punësimit (pozita, paga, kontrata)
  - Të dhëna financiare (llogaria bankare për pagesë të pagës)

3. QËLLIMI I PËRPUNIMIT
Të dhënat përpunohen vetëm për:
  - Ekzekutimin e kontratës së punës
  - Detyrimet ligjore dhe tatimore
  - Administrimin e burimeve njerëzore

4. RUAJTJA E TË DHËNAVE
  - Të dhënat ruhen vetëm për periudhën e nevojshme
  - Pas mbarimit të marrëdhënies së punës, të dhënat ruhen sipas afateve ligjore
  - Aksesi në të dhëna është i kufizuar vetëm për personelin e autorizuar

5. TË DREJTAT E PUNONJËSIT
Çdo punonjës ka të drejtë të:
  - Kërkojë qasje në të dhënat e tij personale
  - Kërkojë korrigjimin e të dhënave të pasakta
  - Kundërshtojë përpunimin e të dhënave

6. KONTAKTI
Për çdo pyetje lidhur me të dhënat personale, kontaktoni:
  hr@centrixsolutions.com

─────────────────────────────────────
Centrix Solutions SH.P.K
Rr. Lidhjes së Prizrenit, Nr. 15, Prishtinë
""";
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
        String status = r.getStatus() == null ? "Pending" : r.getStatus();
        String dotColor = switch (status) {
            case "Approved" -> "#22c55e";
            case "Rejected" -> "#ef4444";
            default -> "#f97316";
        };

        StackPane dot = new StackPane();
        dot.setMinSize(10, 10);
        dot.setMaxSize(10, 10);
        dot.setStyle("-fx-background-color: " + dotColor + "; -fx-background-radius: 5;");

        String statusText = switch (status) {
            case "Approved" -> isAlbanian() ? "Kerkesa juaj per pushim eshte aprovuar" : "Your leave request was approved";
            case "Rejected" -> isAlbanian() ? "Kerkesa juaj per pushim eshte refuzuar" : "Your leave request was rejected";
            default -> isAlbanian() ? "Kerkesa juaj per pushim eshte procesuar" : "Your leave request is pending";
        };

        Label titleLbl = new Label(statusText);
        titleLbl.setWrapText(true);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        String dateRange = formatDate(r.getStartDate()) + " deri " + formatDate(r.getEndDate());
        String detail = isAlbanian()
                ? "Pushimi juaj per datat " + dateRange + " eshte " +
                (status.equals("Approved") ? "aprovuar" : status.equals("Rejected") ? "refuzuar" : "ne pritje") + "."
                : "Your leave request for " + dateRange + " is " + status.toLowerCase() + ".";

        Label detailLbl = new Label(detail);
        detailLbl.setWrapText(true);
        detailLbl.setStyle("-fx-font-size: 11.5px; -fx-opacity: 0.75;");

        Label timeLbl = new Label(formatTimestamp(r.getRequestedAt()));
        timeLbl.setStyle("-fx-font-size: 11px; -fx-opacity: 0.5;");

        HBox header = new HBox(8, dot, titleLbl);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox row = new VBox(4, header, detailLbl, timeLbl);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-border-color: -border; -fx-border-width: 0 0 1 0;");
        return row;
    }

    private VBox createEmptyNotif() {
        Label lbl = new Label(isAlbanian() ? "Nuk keni njoftime aktualisht." : "No notifications at this time.");
        lbl.getStyleClass().add("body-text");
        VBox box = new VBox(lbl);
        box.setPadding(new Insets(16));
        return box;
    }

    private String formatDate(java.sql.Date date) {
        if (date == null) return "-";
        return date.toLocalDate().format(DATE_FMT);
    }

    private String formatTimestamp(Timestamp ts) {
        if (ts == null) return "";
        long days = (System.currentTimeMillis() - ts.getTime()) / (1000 * 60 * 60 * 24);
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

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }
}