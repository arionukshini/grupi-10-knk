module com.company.system {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.company.system to javafx.fxml;
    opens com.company.system.ui to javafx.fxml;

    exports com.company.system;
}