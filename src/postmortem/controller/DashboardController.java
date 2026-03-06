package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;


// ── 3. DashboardController.java ──────────────────────────────
public class DashboardController {

    @FXML private Label                    usernameLabel;
    @FXML private Label                    entryCountLabel;
    @FXML private TableView<FakeBugEntry>  bugTable;
    @FXML private TableColumn<FakeBugEntry, String> titleCol;
    @FXML private TableColumn<FakeBugEntry, String> statusCol;
    @FXML private TableColumn<FakeBugEntry, String> tagsCol;
    @FXML private TableColumn<FakeBugEntry, String> createdCol;
    @FXML private TableColumn<FakeBugEntry, String> updatedCol;

    @FXML
    public void initialize() {
        usernameLabel.setText("Sir Hasn");

        // Wire columns
        titleCol  .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().title));
        statusCol .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().status));
        tagsCol   .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().tags));
        createdCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().created));
        updatedCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().updated));

        // Fake data
        bugTable.getItems().addAll(
                new FakeBugEntry("NullPointerException in LoginService",    "OPEN",     "#NullPointer #Auth",    "2025-03-01", "2025-03-01"),
                new FakeBugEntry("SQLite connection drops on idle",          "OPEN",     "#SQLite",               "2025-03-02", "2025-03-02"),
                new FakeBugEntry("TableView not refreshing on update",       "RESOLVED", "#JavaFX #UI",           "2025-03-03", "2025-03-05"),
                new FakeBugEntry("bcrypt hash mismatch on signup",           "RESOLVED", "#Auth #bcrypt",         "2025-03-04", "2025-03-06"),
                new FakeBugEntry("FXML loader throws on missing fx:id",      "OPEN",     "#JavaFX #FXML",         "2025-03-05", "2025-03-05")
        );
        entryCountLabel.setText(bugTable.getItems().size() + " entries");
    }

    @FXML private void onRowClick()        { SceneNavigator.goTo("bug-entry-detail.fxml"); }
    @FXML private void onNewEntry()        { SceneNavigator.goTo("bug-entry-form.fxml");   }
    @FXML private void onGoToSearch()      { SceneNavigator.goTo("search.fxml");            }
    @FXML private void onFilterAll()       { /* PROTOTYPE: no-op */ }
    @FXML private void onFilterOpen()      { /* PROTOTYPE: no-op */ }
    @FXML private void onFilterResolved()  { /* PROTOTYPE: no-op */ }
    @FXML private void onLogout()          { SceneNavigator.goTo("login.fxml");             }
}
