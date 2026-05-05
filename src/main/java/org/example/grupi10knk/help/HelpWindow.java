package org.example.grupi10knk.help;

import org.example.grupi10knk.i18n.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelpWindow {
    public static void show() {
        Stage stage = new Stage();
        stage.setTitle(LanguageManager.get("help.title"));

        Label content = new Label(LanguageManager.get("help.content"));
        content.setWrapText(true);
        content.setPadding(new Insets(20));
        content.setAccessibleText(LanguageManager.get("help.content"));

        VBox root = new VBox(content);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 450, 250);

        stage.setScene(scene);
        stage.show();
    }
}
