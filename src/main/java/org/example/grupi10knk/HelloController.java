package org.example.grupi10knk;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import org.example.grupi10knk.help.HelpWindow;

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

    @FXML
    protected void onHelpButtonClick() {
        HelpWindow.show();
    }

}
