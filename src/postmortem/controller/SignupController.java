package postmortem.controller;
import postmortem.util.SceneNavigator;
import postmortem.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;


// ── 2. SignupController.java ─────────────────────────────────
public class SignupController {

    @FXML
    private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField passwordVisibleField;
    @FXML private TextField confirmPasswordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        passwordVisibleField.setVisible(false);
        passwordVisibleField.setManaged(false);
        confirmPasswordVisibleField.setVisible(false);
        confirmPasswordVisibleField.setManaged(false);
    }

    @FXML
    private void onSignup() {
        String user    = usernameField.getText().trim();
        String pass    = getPasswordValue();
        String confirm = getConfirmPasswordValue();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!pass.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        if (pass.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }

        try {
            // Attempt to create user
            if (UserDAO.createUser(user, pass.toCharArray())) {
                errorLabel.setText("");
                // Clear fields on successful signup
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                passwordVisibleField.clear();
                confirmPasswordVisibleField.clear();
                showPasswordCheck.setSelected(false);
                setPasswordVisible(false);

                // Navigate back to login
                SceneNavigator.goTo("login.fxml");
            } else {
                errorLabel.setText("Username already exists. Please choose a different one.");
            }
        } catch (Exception e) {
            errorLabel.setText("Signup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onGoToLogin() {
        postmortem.util.SceneNavigator.goTo("login.fxml");
    }

    @FXML
    private void onTogglePasswordVisibility() {
        if (showPasswordCheck.isSelected()) {
            passwordVisibleField.setText(passwordField.getText());
            confirmPasswordVisibleField.setText(confirmPasswordField.getText());
            setPasswordVisible(true);
            passwordVisibleField.requestFocus();
            passwordVisibleField.positionCaret(passwordVisibleField.getText().length());
        } else {
            passwordField.setText(passwordVisibleField.getText());
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());
            setPasswordVisible(false);
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

    private void setPasswordVisible(boolean visible) {
        passwordField.setVisible(!visible);
        passwordField.setManaged(!visible);
        confirmPasswordField.setVisible(!visible);
        confirmPasswordField.setManaged(!visible);

        passwordVisibleField.setVisible(visible);
        passwordVisibleField.setManaged(visible);
        confirmPasswordVisibleField.setVisible(visible);
        confirmPasswordVisibleField.setManaged(visible);
    }

    private String getPasswordValue() {
        return showPasswordCheck.isSelected()
                ? passwordVisibleField.getText()
                : passwordField.getText();
    }

    private String getConfirmPasswordValue() {
        return showPasswordCheck.isSelected()
                ? confirmPasswordVisibleField.getText()
                : confirmPasswordField.getText();
    }
}