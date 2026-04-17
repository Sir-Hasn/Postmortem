package postmortem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import postmortem.dao.LogDAO;
import postmortem.dao.UserDAO;
import postmortem.model.BugEntry;
import postmortem.util.SceneNavigator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfileController {

    @FXML private Label usernameValueLabel;
    @FXML private Label userIdValueLabel;
    @FXML private Label totalEntriesValueLabel;
    @FXML private Label openEntriesValueLabel;
    @FXML private Label resolvedEntriesValueLabel;
    @FXML private Label lastUpdatedValueLabel;
    @FXML private TextField usernameEditField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField newPasswordVisibleField;
    @FXML private TextField confirmPasswordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label updateMessageLabel;

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LogDAO logDAO = new LogDAO();

    @FXML
    public void initialize() {
        String currentUser = SceneNavigator.getCurrentUser();
        int userId = SceneNavigator.getCurrentUserId();

        if (currentUser == null || userId < 0) {
            SceneNavigator.goTo("login.fxml");
            return;
        }

        usernameValueLabel.setText(currentUser);
        userIdValueLabel.setText(String.valueOf(userId));
        usernameEditField.setText(currentUser);

        newPasswordVisibleField.setVisible(false);
        newPasswordVisibleField.setManaged(false);
        confirmPasswordVisibleField.setVisible(false);
        confirmPasswordVisibleField.setManaged(false);

        List<BugEntry> logs = logDAO.getAllLogsByUser(userId);
        long openCount = logs.stream().filter(b -> "OPEN".equalsIgnoreCase(b.getStatus())).count();
        long resolvedCount = logs.stream().filter(b -> "RESOLVED".equalsIgnoreCase(b.getStatus())).count();

        totalEntriesValueLabel.setText(String.valueOf(logs.size()));
        openEntriesValueLabel.setText(String.valueOf(openCount));
        resolvedEntriesValueLabel.setText(String.valueOf(resolvedCount));

        LocalDateTime latestUpdate = logs.stream()
                .map(b -> b.getUpdatedAt() != null ? b.getUpdatedAt() : b.getCreatedAt())
                .filter(d -> d != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        lastUpdatedValueLabel.setText(latestUpdate == null ? "No entries yet" : latestUpdate.format(DATE_TIME_FMT));
    }

    @FXML
    private void onUpdateUsername() {
        String newUsername = usernameEditField.getText() == null ? "" : usernameEditField.getText().trim();
        String currentUsername = SceneNavigator.getCurrentUser();
        int userId = SceneNavigator.getCurrentUserId();

        if (newUsername.isEmpty()) {
            updateMessageLabel.setText("Username cannot be empty.");
            return;
        }

        if (newUsername.equals(currentUsername)) {
            updateMessageLabel.setText("Username is unchanged.");
            return;
        }

        try {
            boolean updated = UserDAO.updateUsername(userId, newUsername);
            if (!updated) {
                updateMessageLabel.setText("Username already exists.");
                return;
            }

            SceneNavigator.setCurrentUser(newUsername);
            usernameValueLabel.setText(newUsername);
            updateMessageLabel.setText("Username updated successfully.");
        } catch (Exception e) {
            updateMessageLabel.setText("Failed to update username: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdatePassword() {
        String newPassword = getNewPassword();
        String confirmPassword = getConfirmPassword();
        int userId = SceneNavigator.getCurrentUserId();

        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            updateMessageLabel.setText("Password fields are required.");
            return;
        }

        if (newPassword.length() < 6) {
            updateMessageLabel.setText("Password must be at least 6 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            updateMessageLabel.setText("Passwords do not match.");
            return;
        }

        try {
            boolean updated = UserDAO.updatePasswordByUserId(userId, newPassword.toCharArray());
            if (!updated) {
                updateMessageLabel.setText("Unable to update password.");
                return;
            }

            newPasswordField.clear();
            confirmPasswordField.clear();
            newPasswordVisibleField.clear();
            confirmPasswordVisibleField.clear();
            showPasswordCheck.setSelected(false);
            setPasswordVisible(false);
            updateMessageLabel.setText("Password updated successfully.");
        } catch (Exception e) {
            updateMessageLabel.setText("Failed to update password: " + e.getMessage());
        }
    }

    @FXML
    private void onTogglePasswordVisibility() {
        if (showPasswordCheck.isSelected()) {
            newPasswordVisibleField.setText(newPasswordField.getText());
            confirmPasswordVisibleField.setText(confirmPasswordField.getText());
            setPasswordVisible(true);
        } else {
            newPasswordField.setText(newPasswordVisibleField.getText());
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());
            setPasswordVisible(false);
        }
    }

    @FXML
    private void onBackToDashboard() {
        SceneNavigator.goTo("dashboard.fxml");
    }

    @FXML
    private void onGoToSearch() {
        SceneNavigator.goTo("search.fxml");
    }

    @FXML
    private void onLogout() {
        SceneNavigator.logout();
        SceneNavigator.goTo("login.fxml");
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
        newPasswordField.setVisible(!visible);
        newPasswordField.setManaged(!visible);
        confirmPasswordField.setVisible(!visible);
        confirmPasswordField.setManaged(!visible);

        newPasswordVisibleField.setVisible(visible);
        newPasswordVisibleField.setManaged(visible);
        confirmPasswordVisibleField.setVisible(visible);
        confirmPasswordVisibleField.setManaged(visible);
    }

    private String getNewPassword() {
        return showPasswordCheck.isSelected()
                ? newPasswordVisibleField.getText()
                : newPasswordField.getText();
    }

    private String getConfirmPassword() {
        return showPasswordCheck.isSelected()
                ? confirmPasswordVisibleField.getText()
                : confirmPasswordField.getText();
    }
}
