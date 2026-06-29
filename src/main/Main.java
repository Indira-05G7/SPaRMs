package main;

import com.formdev.flatlaf.FlatLightLaf;
import database.DatabaseConnection;
import view.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set up FlatLaf Light Look and Feel for modern aesthetics
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf theme. Reverting to system default.");
        }

        // Initialize Database & Setup Tables
        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Critical error initializing database: " + e.getMessage());
            // Show user warning dialog but let application run in case they want to review settings
            JOptionPane.showMessageDialog(null, 
                "Unable to connect to MySQL database.\nPlease ensure MySQL Service is running and the password in DatabaseConnection is correct.\n\nError details: " + e.getMessage(), 
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        // Launch Desktop Application GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Failed to launch GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
