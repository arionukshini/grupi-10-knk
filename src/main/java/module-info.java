module org.example.grupi10knk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.grupi10knk to javafx.fxml;
    opens controller to javafx.fxml;

    exports org.example.grupi10knk;
}