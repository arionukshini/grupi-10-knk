module com.company.system {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.company.system;
    exports com.company.system.model;
    exports com.company.system.service;
    exports com.company.system.db;

    opens com.company.system to javafx.fxml;
    opens com.company.system.ui to javafx.fxml;
    opens com.company.system.model to javafx.fxml;
    opens com.company.system.controller to javafx.fxml;
}