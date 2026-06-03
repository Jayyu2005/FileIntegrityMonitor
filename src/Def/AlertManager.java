package Def;

import java.io.*;
import java.time.*;
import java.time.format.*;
import javax.swing.*;

public class AlertManager {

    static final String LOG_FILE = "alerts.log";
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void alert(String type, String filePath) {
        String timestamp = LocalDateTime.now().format(formatter);
        String message = "[" + timestamp + "] [" + type + "] " + filePath;

        switch (type) {
            case "MODIFIED" -> System.out.println("\u001B[33m" + message + "\u001B[0m"); // Yellow
            case "DELETED"  -> System.out.println("\u001B[31m" + message + "\u001B[0m"); // Red
            case "ADDED"    -> System.out.println("\u001B[32m" + message + "\u001B[0m"); // Green
            default         -> System.out.println(message);
        }

        
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(message);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to log: " + e.getMessage());
        }

       
        SwingUtilities.invokeLater(() -> {
            int messageType;
            switch (type) {
                case "DELETED"  -> messageType = JOptionPane.ERROR_MESSAGE;
                case "MODIFIED" -> messageType = JOptionPane.WARNING_MESSAGE;
                default         -> messageType = JOptionPane.INFORMATION_MESSAGE;
            }
            JOptionPane.showMessageDialog(null, message, "File Integrity Alert", messageType);
        });
    }
}