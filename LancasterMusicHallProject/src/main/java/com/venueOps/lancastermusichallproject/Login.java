package com.venueOps.lancastermusichallproject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.venueOps.lancastermusichallproject.database.DatabaseConnection;

public class Login {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void LoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {
            ScreenController.loadScreen("MainMenu");
        } else {
            Alert("Login Failed", "Invalid username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT password FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // Temporary plaintext comparison
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void Alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // TEMPORARY
    public void SkipLogin() {
        ScreenController.loadScreen("MainMenu");
    }
}
