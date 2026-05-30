module com.company.system {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires org.apache.poi.ooxml;
    requires itextpdf;

    opens com.company.system to javafx.fxml;
    opens com.company.system.controller to javafx.fxml;
    opens com.company.system.model to javafx.base;

    exports com.company.system;
}

