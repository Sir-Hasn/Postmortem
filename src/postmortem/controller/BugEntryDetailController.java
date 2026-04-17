package postmortem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import postmortem.dao.LogDAO;
import postmortem.model.BugEntry;
import postmortem.util.SceneNavigator;
import postmortem.util.ValidationUtil;

import java.time.format.DateTimeFormatter;

public class BugEntryDetailController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label createdLabel;
    @FXML private Label updatedLabel;
    @FXML private TextField tagsField;
    @FXML private TextArea errorMessageArea;
    @FXML private TextArea contextArea;
    @FXML private TextArea solutionArea;
    @FXML private Label titleErrorLabel;
    @FXML private Label errorMessageErrorLabel;
    @FXML private Label contextErrorLabel;
    @FXML private Label solutionErrorLabel;
    @FXML private Label tagsErrorLabel;
    @FXML private Button toggleStatusBtn;
    @FXML private Label errorLabel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LogDAO logDAO = new LogDAO();
    private BugEntry currentBug;

    @FXML
    public void initialize() {
        statusCombo.getItems().addAll("OPEN", "RESOLVED");
        clearValidationLabels();
        loadBugDetails();
    }

    private void loadBugDetails() {
        try {
            int bugId = SceneNavigator.getSelectedBugId();
            if (bugId < 0) {
                errorLabel.setText("No bug selected");
                return;
            }

            currentBug = logDAO.getLogById(bugId);
            if (currentBug == null) {
                errorLabel.setText("Bug not found");
                return;
            }

            displayBugData();
            setupStatusButton();
        } catch (Exception e) {
            System.err.println("Error loading bug details: " + e.getMessage());
            errorLabel.setText("Error loading bug details");
        }
    }

    private void displayBugData() {
        titleField.setText(currentBug.getTitle());
        statusCombo.setValue(currentBug.getStatus());

        createdLabel.setText("Created: " +
                (currentBug.getCreatedAt() == null ? "" : currentBug.getCreatedAt().format(DATE_FMT)));
        updatedLabel.setText("Updated: " +
                (currentBug.getUpdatedAt() == null ? "" : currentBug.getUpdatedAt().format(DATE_FMT)));

        tagsField.setText(currentBug.getTags() == null ? "" : currentBug.getTags());
        errorMessageArea.setText(currentBug.getErrorMessage() == null ? "" : currentBug.getErrorMessage());
        contextArea.setText(currentBug.getErrorContext() == null ? "" : currentBug.getErrorContext());
        solutionArea.setText(currentBug.getSolution() == null ? "" : currentBug.getSolution());

        errorMessageArea.setWrapText(true);
        contextArea.setWrapText(true);
        solutionArea.setWrapText(true);
    }

    private void setupStatusButton() {
        if ("RESOLVED".equals(currentBug.getStatus())) {
            toggleStatusBtn.setText("MARK AS OPEN");
        } else {
            toggleStatusBtn.setText("MARK AS RESOLVED");
        }
    }

    @FXML
    private void onToggleStatus() {
        try {
            String newStatus = "RESOLVED".equals(currentBug.getStatus()) ? "OPEN" : "RESOLVED";

            if ("RESOLVED".equals(newStatus) && solutionArea.getText().trim().isEmpty()) {
                solutionErrorLabel.setText("Solution is required before marking RESOLVED");
                return;
            }

            currentBug.setStatus(newStatus);

            if (logDAO.updateLog(currentBug)) {
                statusCombo.setValue(newStatus);
                setupStatusButton();
                loadBugDetails();
                showSuccessAlert("Status updated to " + newStatus);
            } else {
                showErrorAlert("Failed to update status");
            }
        } catch (Exception e) {
            System.err.println("Error updating status: " + e.getMessage());
            showErrorAlert("Error updating status");
        }
    }

    @FXML
    private void onSaveChanges() {
        if (currentBug == null) {
            errorLabel.setText("No bug selected");
            return;
        }

        clearValidationLabels();
        errorLabel.setText("");

        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String message = errorMessageArea.getText() == null ? "" : errorMessageArea.getText().trim();
        String context = contextArea.getText() == null ? "" : contextArea.getText().trim();
        String solution = solutionArea.getText() == null ? "" : solutionArea.getText().trim();
        String tags = tagsField.getText() == null ? "" : tagsField.getText().trim();
        String status = statusCombo.getValue() == null ? "OPEN" : statusCombo.getValue();

        if ("RESOLVED".equalsIgnoreCase(status) && solution.isBlank()) {
            solutionErrorLabel.setText("Solution is required when status is RESOLVED");
            return;
        }

        ValidationUtil.ValidationResult entryValidation = ValidationUtil.validateBugEntry(title, message, context);
        if (!entryValidation.isValid) {
            titleErrorLabel.setText(entryValidation.errors.getOrDefault("title", ""));
            errorMessageErrorLabel.setText(entryValidation.errors.getOrDefault("errorMessage", ""));
            contextErrorLabel.setText(entryValidation.errors.getOrDefault("errorContext", ""));
            return;
        }

        ValidationUtil.ValidationResult tagValidation = ValidationUtil.validateTagFormat(tags);
        if (!tagValidation.isValid) {
            tagsErrorLabel.setText(tagValidation.errors.getOrDefault("tags", "Invalid tags format"));
            return;
        }

        try {
            currentBug.setTitle(title);
            currentBug.setErrorMessage(message);
            currentBug.setErrorContext(context);
            currentBug.setSolution(solution);
            currentBug.setTags(tags);
            currentBug.setStatus(status);

            if (logDAO.updateLog(currentBug)) {
                errorLabel.setText("");
                loadBugDetails();
                showSuccessAlert("Bug entry updated successfully");
            } else {
                errorLabel.setText("Failed to update bug entry");
            }
        } catch (Exception e) {
            System.err.println("Error saving bug: " + e.getMessage());
            errorLabel.setText("Error saving bug entry");
        }
    }

    @FXML
    private void onDelete() {
        if (currentBug == null) {
            showErrorAlert("No bug selected");
            return;
        }

        try {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Bug Entry");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Are you sure you want to delete this bug entry?");

            if (confirmation.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
                if (logDAO.deleteLog(currentBug.getId())) {
                    showSuccessAlert("Bug entry deleted");
                    SceneNavigator.setSelectedBugId(-1);
                    SceneNavigator.goTo("dashboard.fxml");
                } else {
                    showErrorAlert("Failed to delete bug entry");
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting bug: " + e.getMessage());
            showErrorAlert("Error deleting bug entry");
        }
    }

    @FXML
    private void onBack() {
        SceneNavigator.setSelectedBugId(-1);
        SceneNavigator.goTo("dashboard.fxml");
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

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearValidationLabels() {
        titleErrorLabel.setText("");
        errorMessageErrorLabel.setText("");
        contextErrorLabel.setText("");
        solutionErrorLabel.setText("");
        tagsErrorLabel.setText("");
    }

}
