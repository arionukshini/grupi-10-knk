package com.company.system.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("com.company.system");

    private AppLogger() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}
