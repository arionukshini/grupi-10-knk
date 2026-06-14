module com.company.system {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires java.desktop;
    requires java.logging;
    requires org.apache.poi.ooxml;
    requires itextpdf;

    opens com.company.system to javafx.fxml;
    opens com.company.system.controller to javafx.fxml;
    opens com.company.system.models to javafx.base;

    exports com.company.system;
    exports com.company.system.models;
    exports com.company.system.models.dto;
    exports com.company.system.models.mappers;
    exports com.company.system.repository;
}
