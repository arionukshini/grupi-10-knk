package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.models.Department;
import com.company.system.models.Employee;
import com.company.system.models.User;
import com.company.system.service.DepartmentService;
import com.company.system.service.EmployeeService;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class MyDepartmentController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label overviewTitleLabel;
    @FXML private Label departmentNameTitleLabel;
    @FXML private Label departmentNameLabel;
    @FXML private Label positionTitleLabel;
    @FXML private Label positionLabel;
    @FXML private Label descriptionTitleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label colleaguesTitleLabel;
    @FXML private VBox colleaguesContainer;
    @FXML private Label detailsTitleLabel;
    @FXML private Label departmentIdTitleLabel;
    @FXML private Label departmentIdLabel;
    @FXML private Label employeeIdTitleLabel;
    @FXML private Label employeeIdLabel;
    @FXML private Label colleagueCountTitleLabel;
    @FXML private Label colleagueCountLabel;
    @FXML private Label sideNoteLabel;

    @FXML
    public void initialize() {
        applyTranslations();
        loadDepartment();
    }

    private void applyTranslations() {
        titleLabel.setText(LanguageManager.get("user.department.title"));
        subtitleLabel.setText(LanguageManager.get("user.department.subtitle"));
        overviewTitleLabel.setText(LanguageManager.get("user.department.overview"));
        departmentNameTitleLabel.setText(LanguageManager.get("user.department.name"));
        positionTitleLabel.setText(LanguageManager.get("user.department.position"));
        descriptionTitleLabel.setText(LanguageManager.get("user.department.description"));
        colleaguesTitleLabel.setText(LanguageManager.get("user.department.colleagues"));
        detailsTitleLabel.setText(LanguageManager.get("user.department.department"));
        departmentIdTitleLabel.setText(LanguageManager.get("employees.departmentId"));
        employeeIdTitleLabel.setText(LanguageManager.get("salaries.employeeId"));
        colleagueCountTitleLabel.setText(LanguageManager.get("user.department.colleagues"));
        sideNoteLabel.setText(LanguageManager.get("user.department.sameDepartment"));
    }

    private void loadDepartment() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) {
            showEmpty(LanguageManager.get("user.department.noData"));
            return;
        }

        Employee employee = EmployeeService.getEmployeeById(user.getEmployeeId());
        if (employee == null) {
            showEmpty(LanguageManager.get("user.department.employeeNotFound"));
            return;
        }

        Department department = DepartmentService.getDepartmentById(employee.getDepartmentId());
        List<Employee> colleagues = EmployeeService.getEmployeesByDepartment(employee.getDepartmentId(), employee.getId());

        departmentNameLabel.setText(valueOrDash(department == null ? null : department.getName()));
        positionLabel.setText(valueOrDash(employee.getPosition()));
        descriptionLabel.setText(valueOrDash(department == null ? null : department.getDescription()));
        departmentIdLabel.setText(String.valueOf(employee.getDepartmentId()));
        employeeIdLabel.setText(String.valueOf(employee.getId()));
        colleagueCountLabel.setText(String.valueOf(colleagues.size()));

        showColleagues(colleagues);
    }

    private void showColleagues(List<Employee> colleagues) {
        colleaguesContainer.getChildren().clear();

        if (colleagues.isEmpty()) {
            VBox emptyCard = new VBox(6);
            emptyCard.getStyleClass().add("info-card");
            emptyCard.setPadding(new Insets(14));

            Label title = new Label(LanguageManager.get("user.department.noColleagues"));
            title.getStyleClass().add("section-title");
            Label subtitle = new Label(LanguageManager.get("user.department.noColleaguesSub"));
            subtitle.getStyleClass().add("body-text");
            subtitle.setWrapText(true);

            emptyCard.getChildren().addAll(title, subtitle);
            colleaguesContainer.getChildren().add(emptyCard);
            return;
        }

        for (Employee colleague : colleagues) {
            colleaguesContainer.getChildren().add(createColleagueCard(colleague));
        }
    }

    private VBox createColleagueCard(Employee colleague) {
        VBox card = new VBox(8);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(14));

        Label name = new Label(colleague.getFirstName() + " " + colleague.getLastName());
        name.getStyleClass().add("section-title");

        HBox meta = new HBox(18);
        meta.getChildren().addAll(
                detail(LanguageManager.get("user.colleague.position"), valueOrDash(colleague.getPosition())),
                detail(LanguageManager.get("user.colleague.email"), valueOrDash(colleague.getEmail())),
                detail(LanguageManager.get("user.colleague.status"), valueOrDash(colleague.getStatus()))
        );

        card.getChildren().addAll(name, meta);
        return card;
    }

    private Label detail(String label, String value) {
        Label detail = new Label(label + ": " + value);
        detail.getStyleClass().add("body-text");
        detail.setWrapText(true);
        return detail;
    }

    private void showEmpty(String message) {
        departmentNameLabel.setText("-");
        positionLabel.setText("-");
        descriptionLabel.setText(message);
        departmentIdLabel.setText("-");
        employeeIdLabel.setText("-");
        colleagueCountLabel.setText("0");
        Label emptyLabel = new Label(message);
        emptyLabel.getStyleClass().add("body-text");
        emptyLabel.setWrapText(true);
        colleaguesContainer.getChildren().setAll(emptyLabel);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
