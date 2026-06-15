package com.company.system.controller.support;

import com.company.system.i18n.LanguageManager;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public final class HelpViewFactory {

    private HelpViewFactory() {
    }

    public static Node createAdminHelpView() {
        Label title = pageTitle(LanguageManager.get("help.title"));
        Label intro = bodyText(LanguageManager.get("help.admin.intro"));

        VBox sections = new VBox(14);
        sections.getChildren().addAll(
                createHelpSection(LanguageManager.get("help.admin.navigation.title"),
                        LanguageManager.get("help.admin.navigation.line1"),
                        LanguageManager.get("help.admin.navigation.line2"),
                        LanguageManager.get("help.admin.navigation.line3")),
                createHelpSection(LanguageManager.get("help.admin.shortcuts.title"),
                        "Ctrl+E - " + LanguageManager.get("menu.employees"),
                        "Ctrl+K - " + LanguageManager.get("menu.contracts"),
                        "Ctrl+S - " + LanguageManager.get("menu.salaries"),
                        "Ctrl+D - " + LanguageManager.get("menu.dashboard"),
                        "Ctrl+R - " + LanguageManager.get("menu.departments"),
                        "Ctrl+U - " + LanguageManager.get("menu.users"),
                        "Ctrl+P - " + LanguageManager.get("menu.profile"),
                        "Ctrl+L - " + LanguageManager.get("menu.language"),
                        "Ctrl+H / F1 - " + LanguageManager.get("menu.help"),
                        "Alt+Left / Mouse Back - " + LanguageManager.get("help.admin.shortcuts.back"),
                        "Alt+Right / Mouse Forward - " + LanguageManager.get("help.admin.shortcuts.forward"),
                        "F5 - " + LanguageManager.get("help.admin.shortcuts.refresh"),
                        "Esc - " + LanguageManager.get("menu.exit")),
                createHelpSection(LanguageManager.get("help.admin.context.title"),
                        LanguageManager.get("help.admin.context.line1"),
                        LanguageManager.get("help.admin.context.line2")),
                createHelpSection(LanguageManager.get("help.admin.language.title"),
                        LanguageManager.get("help.admin.language.line1"),
                        LanguageManager.get("help.admin.language.line2")),
                createHelpSection(LanguageManager.get("help.admin.account.title"),
                        LanguageManager.get("help.admin.account.line1"),
                        LanguageManager.get("help.admin.account.line2"))
        );

        return wrapHelpView(title, intro, sections);
    }

    public static Node createUserHelpView() {
        Label title = pageTitle(LanguageManager.get("help.title"));
        Label intro = bodyText(LanguageManager.get("help.user.intro"));

        VBox sections = new VBox(14);
        sections.getChildren().addAll(
                createHelpSection(LanguageManager.get("help.user.navigation.title"),
                        LanguageManager.get("help.user.navigation.line1"),
                        LanguageManager.get("help.user.navigation.line2")),
                createHelpSection(LanguageManager.get("help.user.shortcuts.title"),
                        "Ctrl+D - " + LanguageManager.get("menu.dashboard"),
                        "Ctrl+K - " + LanguageManager.get("help.user.shortcuts.contracts"),
                        "Ctrl+S - " + LanguageManager.get("help.user.shortcuts.salaries"),
                        "Ctrl+R - " + LanguageManager.get("help.user.shortcuts.departments"),
                        "Ctrl+P - " + LanguageManager.get("menu.profile"),
                        "Ctrl+L - " + LanguageManager.get("menu.language"),
                        "Ctrl+H / F1 - " + LanguageManager.get("menu.help"),
                        "Alt+Left / Mouse Back - " + LanguageManager.get("help.user.shortcuts.back"),
                        "Alt+Right / Mouse Forward - " + LanguageManager.get("help.user.shortcuts.forward"),
                        "F5 - " + LanguageManager.get("help.user.shortcuts.refresh"),
                        "Esc - " + LanguageManager.get("menu.exit")),
                createHelpSection(LanguageManager.get("help.user.context.title"),
                        LanguageManager.get("help.user.context.line1"),
                        LanguageManager.get("help.user.context.line2"))
        );

        return wrapHelpView(title, intro, sections);
    }

    private static Label pageTitle(String text) {
        Label title = new Label(text);
        title.getStyleClass().add("page-title");
        return title;
    }

    private static Label bodyText(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("body-text");
        return label;
    }

    private static ScrollPane wrapHelpView(Label title, Label intro, VBox sections) {
        VBox helpView = new VBox(18, title, intro, sections);
        helpView.getStyleClass().add("profile-page");
        helpView.setStyle("-fx-padding: 26;");

        ScrollPane scrollPane = new ScrollPane(helpView);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("module-scroll");
        return scrollPane;
    }

    private static VBox createHelpSection(String sectionTitle, String... lines) {
        Label title = new Label(sectionTitle);
        title.getStyleClass().add("section-title");

        VBox content = new VBox(6, title);
        for (String line : lines) {
            Label item = bodyText("- " + line);
            content.getChildren().add(item);
        }

        content.getStyleClass().add("content-card");
        return content;
    }
}
