package com.company.system.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static Locale currentLocale = Locale.forLanguageTag("sq");
    private static ResourceBundle bundle =
            ResourceBundle.getBundle("messages", currentLocale);

    public static void setLanguage(String languageCode) {
        currentLocale = Locale.forLanguageTag(languageCode);
        bundle = ResourceBundle.getBundle("messages", currentLocale);
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
