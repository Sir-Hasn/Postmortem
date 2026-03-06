package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;


// ── 4. BugEntryFormController.java ───────────────────────────
public class BugEntryFormController {

    @FXML private TextField  titleField;
    @FXML private TextArea   errorMessageField;
    @FXML private TextArea   contextField;
    @FXML private TextArea   solutionField;
    @FXML private TextField  tagsField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label      errorLabel;

    @FXML
    public void initialize() {
        statusCombo.getItems().addAll("OPEN", "RESOLVED");
        statusCombo.setValue("OPEN");
    }

    @FXML
    private void onSave() {
        if (titleField.getText().trim().isEmpty()) {
            errorLabel.setText("Title is required.");
            return;
        }
        // PROTOTYPE: just go back to dashboard
        SceneNavigator.goTo("dashboard.fxml");
    }

    @FXML
    private void onBack() {
        SceneNavigator.goTo("dashboard.fxml");
    }
}