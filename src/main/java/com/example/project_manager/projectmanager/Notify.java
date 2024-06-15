package com.example.project_manager.projectmanager;

import javafx.scene.control.Alert;

public class Notify {

    public static void send(String title, String message) {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                // Для Windows используем PowerShell
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(title);
                alert.setContentText(message);
                alert.showAndWait();
            } else if (osName.contains("mac")) {
                // Для macOS используем osascript
                String[] cmd = { "osascript", "-e", "display notification \"" + message + "\" with title \"" + title + "\"" };
                Runtime.getRuntime().exec(cmd);
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                // Для Linux используем notify-send
                Runtime.getRuntime().exec(new String[]{"notify-send", title, message});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
