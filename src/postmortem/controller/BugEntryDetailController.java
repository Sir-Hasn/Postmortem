package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// ── 5. BugEntryDetailController.java ─────────────────────────
public class BugEntryDetailController {

    @FXML private Label    titleLabel;
    @FXML private Label    statusBadge;
    @FXML private Label    createdLabel;
    @FXML private Label    resolvedLabel;
    @FXML private Button   toggleStatusBtn;

    private boolean isResolved = false;

    @FXML
    public void initialize() {
        // PROTOTYPE: fake entry pre-loaded in FXML
    }

    @FXML
    private void onToggleStatus() {
        isResolved = !isResolved;
        if (isResolved) {
            statusBadge.setText("RESOLVED");
            statusBadge.getStyleClass().setAll("badge-resolved");
            resolvedLabel.setText("Resolved: " + java.time.LocalDate.now());
            toggleStatusBtn.setText("MARK AS OPEN");
        } else {
            statusBadge.setText("OPEN");
            statusBadge.getStyleClass().setAll("badge-open");
            resolvedLabel.setText("");
            toggleStatusBtn.setText("MARK AS RESOLVED");
        }
    }

    @FXML private void onEdit()   { SceneNavigator.goTo("bug-entry-form.fxml");   }
    @FXML private void onDelete() { SceneNavigator.goTo("dashboard.fxml");         }
    @FXML private void onBack()   { SceneNavigator.goTo("dashboard.fxml");         }
}
