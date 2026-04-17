package postmortem.controller;

import postmortem.util.SceneNavigator;
import postmortem.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// ── 1. LoginController.java ─────────────────────────────────
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField     passwordVisibleField;
    @FXML private CheckBox      showPasswordCheck;
    @FXML private Label         errorLabel;

    @FXML
    public void initialize() {
        passwordVisibleField.setVisible(false);
        passwordVisibleField.setManaged(false);
    }

    @FXML
    private void onSignIn() {
        String user = usernameField.getText().trim();
        String pass = getCurrentPassword();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Username and password are required.");
            return;
        }

        try {
            // Attempt authentication
            if (UserDAO.authenticate(user, pass.toCharArray())) {
                // Get user ID and store both username and ID
                int userId = UserDAO.getUserId(user);

                // Clear fields on successful login
                usernameField.clear();
                passwordField.clear();
                passwordVisibleField.clear();
                showPasswordCheck.setSelected(false);
                passwordVisibleField.setVisible(false);
                passwordVisibleField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                errorLabel.setText("");

                // Store current user and user ID, then navigate to dashboard
                SceneNavigator.setCurrentUser(user);
                SceneNavigator.setCurrentUserId(userId);
                SceneNavigator.goTo("dashboard.fxml");
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGoToSignup() {
        SceneNavigator.goTo("signup.fxml");
    }

    @FXML
    private void onTogglePasswordVisibility() {
        if (showPasswordCheck.isSelected()) {
            passwordVisibleField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);
            passwordVisibleField.requestFocus();
            passwordVisibleField.positionCaret(passwordVisibleField.getText().length());
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    @FXML
    private void onWindowMinimize() {
        SceneNavigator.minimizeWindow();
    }

    @FXML
    private void onWindowMaximize() {
        SceneNavigator.toggleMaximizeWindow();
    }

    @FXML
    private void onWindowClose() {
        SceneNavigator.closeWindow();
    }

    private String getCurrentPassword() {
        return showPasswordCheck.isSelected()
                ? passwordVisibleField.getText()
                : passwordField.getText();
    }
}