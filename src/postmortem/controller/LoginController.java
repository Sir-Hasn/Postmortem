package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// ── 1. LoginController.java ─────────────────────────────────
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    @FXML
    private void onSignIn() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Username and password are required.");
            return;
        }
        // PROTOTYPE: accept any credentials
        SceneNavigator.goTo("dashboard.fxml");
    }

    @FXML
    private void onGoToSignup() {
        SceneNavigator.goTo("signup.fxml");
    }
}