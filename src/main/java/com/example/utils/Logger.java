package com.example.utils;

public class Logger {
    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void error(String message, Throwable e) {
        System.err.println("[ERROR] " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
}   