package com.company.system.controller;

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
import com.company.system.utils.Session;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.Date;
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
        if (salaryTitleLabel != null) {
            salaryTitleLabel.setText(isAlbanian() ? "Informata te pages" : "Salary information");
        }
        departmentTitleLabel.setText(LanguageManager.get("profile.departmentInfo"));
        colleaguesTitleLabel.setText(LanguageManager.get("profile.colleagues"));
    }

    private void setupColleaguesTable() {
        colleaguesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colleaguesTable.setPlaceholder(new Label(LanguageManager.get("profile.noColleagues")));

        colleagueNameColumn.setText(LanguageManager.get("profile.colleagueName"));
        colleagueNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                valueOrDash(cellData.getValue().getFirstName()) + " " + valueOrDash(cellData.getValue().getLastName())
        ));

        colleaguePositionColumn.setText(LanguageManager.get("profile.colleaguePosition"));
        colleaguePositionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        colleagueEmailColumn.setText(LanguageManager.get("profile.colleagueEmail"));
        colleagueEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        colleagueStatusColumn.setText(LanguageManager.get("profile.colleagueStatus"));
        colleagueStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadUserDashboard() {
        User user = Session.getUser();

        if (user == null || user.getEmployeeId() == null) {
            employeeDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("message.userNotFound")));
            contractDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("profile.noContract")));
            loadSalaryDetails(null);
            departmentDetailsBox.getChildren().setAll(createDetail(LanguageManager.get("profile.noDepartment")));
            colleaguesTable.setItems(FXCollections.observableArrayList());
            return;
        }

        Employee employee = EmployeeService.getEmployeeById(user.getEmployeeId());
        Department department = employee == null ? null : DepartmentService.getDepartmentById(employee.getDepartmentId());
        Contract contract = ContractService.getLatestContractByEmployeeId(user.getEmployeeId());
        Salary salary = SalaryService.getLatestSalaryByEmployeeId(user.getEmployeeId());

        List<Employee> colleagues = new ArrayList<>();

        if (department != null && employee != null) {
            colleagues = EmployeeService.getEmployeesByDepartment(department.getId(), employee.getId());
        }

        loadEmployeeDetails(employee, department);
        loadContractDetails(contract);
        loadSalaryDetails(salary);
        loadDepartmentDetails(department);
        colleaguesTable.setItems(FXCollections.observableArrayList(colleagues));
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
        if (salaryDetailsBox == null) {
            return;
        }

        if (salary == null) {
            salaryDetailsBox.getChildren().setAll(createDetail(
                    isAlbanian()
                            ? "Nuk u gjeten informata per page."
                            : "No salary information found."
            ));
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

    private Label createDetail(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("profile-detail");
        return label;
    }

    private String formatSqlDate(Date date) {
        if (date == null) {
            return "-";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.toLocalDate().format(formatter);
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