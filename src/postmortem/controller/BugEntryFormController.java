package postmortem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import postmortem.dao.LogDAO;
import postmortem.model.BugEntry;
import postmortem.util.SceneNavigator;
import postmortem.util.ValidationUtil;

public class BugEntryFormController {

    @FXML private TextField titleField;
    @FXML private TextArea errorMessageField;
    @FXML private TextArea contextField;
    @FXML private TextArea solutionField;
    @FXML private TextField tagsField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label titleErrorLabel;
    @FXML private Label errorMessageErrorLabel;
    @FXML private Label contextErrorLabel;
    @FXML private Label solutionErrorLabel;
    @FXML private Label tagsErrorLabel;
    @FXML private Label formErrorLabel;

    private final LogDAO logDAO = new LogDAO();
    private boolean isEditMode = false;
    private int bugId = -1;

    @FXML
    public void initialize() {
        statusCombo.getItems().addAll("OPEN", "RESOLVED");
        statusCombo.setValue("OPEN");
        clearErrors();

        int selectedId = SceneNavigator.getSelectedBugId();
        if (selectedId >= 0) {
            loadBugForEditing(selectedId);
        }
    }

    private void loadBugForEditing(int id) {
        try {
            BugEntry bug = logDAO.getLogById(id);
            if (bug != null) {
                isEditMode = true;
                bugId = id;
                titleField.setText(bug.getTitle());
                errorMessageField.setText(bug.getErrorMessage());
                contextField.setText(bug.getErrorContext());
                solutionField.setText(bug.getSolution());
                tagsField.setText(bug.getTags());
                statusCombo.setValue(bug.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Error loading bug: " + e.getMessage());
            formErrorLabel.setText("Error loading bug entry");
        }
    }

    @FXML
    private void onSave() {
        clearErrors();

        String title = titleField.getText().trim();
        String errorMessage = errorMessageField.getText().trim();
        String errorContext = contextField.getText().trim();
        String solution = solutionField.getText().trim();
        String tags = tagsField.getText().trim();
        String status = statusCombo.getValue();

        ValidationUtil.ValidationResult result = ValidationUtil.validateBugEntry(title, errorMessage, errorContext);
        ValidationUtil.ValidationResult tagResult = ValidationUtil.validateTagFormat(tags);

        if ("RESOLVED".equalsIgnoreCase(status) && solution.isBlank()) {
            solutionErrorLabel.setText("Solution is required when status is RESOLVED");
            return;
        }

        if (!result.isValid || !tagResult.isValid) {
            applyValidationErrors(result, tagResult);
            return;
        }

        try {
            if (isEditMode) {
                BugEntry bug = new BugEntry();
                bug.setId(bugId);
                bug.setUserId(SceneNavigator.getCurrentUserId());
                bug.setTitle(title);
                bug.setErrorMessage(errorMessage);
                bug.setErrorContext(errorContext);
                bug.setSolution(solution);
                bug.setTags(tags);
                bug.setStatus(status);

                if (logDAO.updateLog(bug)) {
                    showSuccessAlert("Bug entry updated successfully");
                    SceneNavigator.setSelectedBugId(-1);
                    SceneNavigator.goTo("dashboard.fxml");
                } else {
                    formErrorLabel.setText("Failed to update bug entry");
                }
            } else {
                BugEntry bug = new BugEntry(
                        SceneNavigator.getCurrentUserId(),
                        title,
                        errorMessage,
                        errorContext,
                        tags
                );
                bug.setSolution(solution);
                bug.setStatus(status);

                int insertedId = logDAO.insertLog(bug);
                if (insertedId > 0) {
                    showSuccessAlert("Bug entry saved successfully");
                    SceneNavigator.goTo("dashboard.fxml");
                } else {
                    formErrorLabel.setText("Failed to save bug entry");
                }
            }
        } catch (IllegalArgumentException e) {
            formErrorLabel.setText(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error saving bug: " + e.getMessage());
            formErrorLabel.setText("Error saving bug entry: " + e.getMessage());
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

    private void applyValidationErrors(ValidationUtil.ValidationResult result,
                                       ValidationUtil.ValidationResult tagResult) {
        titleErrorLabel.setText(result.errors.getOrDefault("title", ""));
        errorMessageErrorLabel.setText(result.errors.getOrDefault("errorMessage", ""));
        contextErrorLabel.setText(result.errors.getOrDefault("errorContext", ""));
        tagsErrorLabel.setText(tagResult.errors.getOrDefault("tags", ""));
    }

    private void clearErrors() {
        titleErrorLabel.setText("");
        errorMessageErrorLabel.setText("");
        contextErrorLabel.setText("");
        solutionErrorLabel.setText("");
        tagsErrorLabel.setText("");
        formErrorLabel.setText("");
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
