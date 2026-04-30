package org.example.grupi10knk;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        Connection conn = DBConnection.connect();
        if (conn == null) {
            welcomeText.setText("Connection failed!");
        } else {
            welcomeText.setText("Connection worked!");
        }
    }
}
