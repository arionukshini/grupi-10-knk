module org.example.grupi10knk {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.grupi10knk to javafx.fxml;
    exports org.example.grupi10knk;
}