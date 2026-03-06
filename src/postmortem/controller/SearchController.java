package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// ── 6. SearchController.java ──────────────────────────────────
public class SearchController {

    @FXML
    private TextField searchField;
    @FXML private Label resultsLabel;
    @FXML private TableView<FakeBugEntry> resultsTable;
    @FXML private TableColumn<FakeBugEntry, String> titleCol;
    @FXML private TableColumn<FakeBugEntry, String> statusCol;
    @FXML private TableColumn<FakeBugEntry, String> tagsCol;
    @FXML private TableColumn<FakeBugEntry, String> createdCol;
    @FXML private TableColumn<FakeBugEntry, String> updatedCol;

    private final java.util.List<FakeBugEntry> allEntries = java.util.List.of(
            new FakeBugEntry("NullPointerException in LoginService",    "OPEN",     "#NullPointer #Auth",    "2025-03-01", "2025-03-01"),
            new FakeBugEntry("SQLite connection drops on idle",          "OPEN",     "#SQLite",               "2025-03-02", "2025-03-02"),
            new FakeBugEntry("TableView not refreshing on update",       "RESOLVED", "#JavaFX #UI",           "2025-03-03", "2025-03-05"),
            new FakeBugEntry("bcrypt hash mismatch on signup",           "RESOLVED", "#Auth #bcrypt",         "2025-03-04", "2025-03-06"),
            new FakeBugEntry("FXML loader throws on missing fx:id",      "OPEN",     "#JavaFX #FXML",         "2025-03-05", "2025-03-05")
    );

    @FXML
    public void initialize() {
        titleCol  .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().title));
        statusCol .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().status));
        tagsCol   .setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().tags));
        createdCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().created));
        updatedCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().updated));

        resultsTable.getItems().addAll(allEntries);
        resultsLabel.setText("Showing " + allEntries.size() + " results");
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().trim().toLowerCase();
        resultsTable.getItems().clear();

        var filtered = query.isEmpty()
                ? allEntries
                : allEntries.stream()
                .filter(e -> e.title.toLowerCase().contains(query)
                        || e.tags.toLowerCase().contains(query))
                .toList();

        resultsTable.getItems().addAll(filtered);
        resultsLabel.setText("Showing " + filtered.size() + " results");
    }

    @FXML
    private void onClear() {
        searchField.clear();
        resultsTable.getItems().setAll(allEntries);
        resultsLabel.setText("Showing " + allEntries.size() + " results");
    }

    @FXML private void onRowClick() { postmortem.util.SceneNavigator.goTo("bug-entry-detail.fxml"); }
    @FXML private void onBack()     { postmortem.util.SceneNavigator.goTo("dashboard.fxml");         }
}