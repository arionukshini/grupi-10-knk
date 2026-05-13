module com.company.system {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.company.system;

    opens com.company.system to javafx.fxml;
    opens com.company.system.controller to javafx.fxml;
    opens com.company.system.model to javafx.base;
}