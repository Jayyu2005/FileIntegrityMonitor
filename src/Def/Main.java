package Def;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Main extends JFrame {

    // Components
    private JTextField folderPathField;
    private JTextArea alertArea;
    private JLabel statusLabel;
    private JButton browseButton;
    private JButton baselineButton;
    private JButton checkButton;
    private JButton clearButton;

    // Constants
    static final String SNAPSHOT_FILE = "snapshot.dat";

    public Main() {
        setTitle("File Integrity Monitor");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.decode("#1e1e1e"));

        // ── TOP PANEL - Folder Selection ──
        JPanel topPanel = new JPanel(new BorderLayout(8, 0));
        topPanel.setBackground(Color.decode("#1e1e1e"));

        JLabel folderLabel = new JLabel("Watch Folder:");
        folderLabel.setForeground(Color.WHITE);
        folderLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        folderPathField = new JTextField();
        folderPathField.setBackground(Color.decode("#2d2d2d"));
        folderPathField.setForeground(Color.WHITE);
        folderPathField.setCaretColor(Color.WHITE);
        folderPathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#555555")),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        folderPathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        browseButton = new JButton("Browse");
        styleButton(browseButton, "#4a90d9");
        browseButton.addActionListener(e -> browseFolder());

        topPanel.add(folderLabel, BorderLayout.WEST);
        topPanel.add(folderPathField, BorderLayout.CENTER);
        topPanel.add(browseButton, BorderLayout.EAST);

        // ── MIDDLE PANEL - Alert Log ──
        JPanel middlePanel = new JPanel(new BorderLayout(0, 5));
        middlePanel.setBackground(Color.decode("#1e1e1e"));

        JLabel logLabel = new JLabel("Alert Log:");
        logLabel.setForeground(Color.WHITE);
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        alertArea = new JTextArea();
        alertArea.setEditable(false);
        alertArea.setBackground(Color.decode("#2d2d2d"));
        alertArea.setForeground(Color.decode("#00ff99"));
        alertArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        alertArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        alertArea.setText("No alerts yet. Select a folder and create a baseline to begin.\n");

        JScrollPane scrollPane = new JScrollPane(alertArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#555555")));

        middlePanel.add(logLabel, BorderLayout.NORTH);
        middlePanel.add(scrollPane, BorderLayout.CENTER);

        // ── BOTTOM PANEL - Buttons + Status ──
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setBackground(Color.decode("#1e1e1e"));

        // Button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(Color.decode("#1e1e1e"));

        baselineButton = new JButton("Create Baseline");
        styleButton(baselineButton, "#27ae60");
        baselineButton.addActionListener(e -> createBaseline());

        checkButton = new JButton("Check Integrity");
        styleButton(checkButton, "#e67e22");
        checkButton.addActionListener(e -> checkIntegrity());

        clearButton = new JButton("Clear Log");
        styleButton(clearButton, "#7f8c8d");
        clearButton.addActionListener(e -> alertArea.setText(""));

        buttonPanel.add(baselineButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(clearButton);

        // Status label
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setForeground(Color.decode("#aaaaaa"));
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        // ── ASSEMBLE ──
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ── BUTTON ACTIONS ──

    private void browseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Folder to Watch");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            folderPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            log("Folder selected: " + folderPathField.getText());
            statusLabel.setText("Status: Folder selected");
        }
    }

    private void createBaseline() {
        String folderPath = folderPathField.getText().trim();
        if (folderPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a folder first.",
                "No Folder Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            log("Creating baseline snapshot...");
            statusLabel.setText("Status: Creating baseline...");
            Map<String, String> snapshot = SnapshotManager.takeSnapshot(folderPath);
            SnapshotManager.saveSnapshot(snapshot, SNAPSHOT_FILE);
            log("Baseline created successfully. " + snapshot.size() + " files recorded.");
            statusLabel.setText("Status: Baseline saved — " + snapshot.size() + " files");
        } catch (Exception e) {
            log("ERROR creating baseline: " + e.getMessage());
            statusLabel.setText("Status: Error");
        }
    }

    private void checkIntegrity() {
        String folderPath = folderPathField.getText().trim();
        if (folderPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a folder first.",
                "No Folder Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!new File(SNAPSHOT_FILE).exists()) {
            JOptionPane.showMessageDialog(this, "No baseline found. Create a baseline first.",
                "No Baseline", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            log("Running integrity check...");
            statusLabel.setText("Status: Checking...");
            Map<String, String> oldSnapshot = SnapshotManager.loadSnapshot(SNAPSHOT_FILE);
            Map<String, String> newSnapshot = SnapshotManager.takeSnapshot(folderPath);
            
            // Capture changes
            int changes = 0;
            for (Map.Entry<String, String> entry : oldSnapshot.entrySet()) {
                String filePath = entry.getKey();
                String oldHash = entry.getValue();
                if (!newSnapshot.containsKey(filePath)) {
                    log("[DELETED] " + filePath);
                    changes++;
                } else if (!newSnapshot.get(filePath).equals(oldHash)) {
                    log("[MODIFIED] " + filePath);
                    changes++;
                }
            }
            for (Map.Entry<String, String> entry : newSnapshot.entrySet()) {
                if (!oldSnapshot.containsKey(entry.getKey())) {
                    log("[ADDED] " + entry.getKey());
                    changes++;
                }
            }

            if (changes == 0) {
                log("No changes detected. All files intact.");
                statusLabel.setText("Status: Clean — no changes detected");
            } else {
                log(changes + " change(s) detected.");
                statusLabel.setText("Status: " + changes + " change(s) found");
            }

            // Update snapshot after check
            SnapshotManager.saveSnapshot(newSnapshot, SNAPSHOT_FILE);

        } catch (Exception e) {
            log("ERROR during check: " + e.getMessage());
            statusLabel.setText("Status: Error");
        }
    }

    // ── HELPER ──

    private void log(String message) {
        alertArea.append(message + "\n");
        alertArea.setCaretPosition(alertArea.getDocument().getLength());
    }

    // ── ENTRY POINT ──

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
    private void styleButton(JButton button, String hexColor) {
        button.setBackground(Color.decode(hexColor));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
