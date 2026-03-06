package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;


// ── 2. SignupController.java ─────────────────────────────────
public class SignupController {

    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void onSignup() {
        String user    = usernameField.getText().trim();
        String pass    = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!pass.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        // PROTOTYPE: just navigate back to login
        postmortem.util.SceneNavigator.goTo("login.fxml");
    }

    @FXML
    private void onGoToLogin() {
        postmortem.util.SceneNavigator.goTo("login.fxml");
    }
}