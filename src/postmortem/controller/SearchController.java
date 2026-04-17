package postmortem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import postmortem.dao.LogDAO;
import postmortem.model.BugEntry;
import postmortem.util.SceneNavigator;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SearchController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> tagFilterCombo;
    @FXML private Label resultsLabel;
    @FXML private TableView<BugEntry> resultsTable;
    @FXML private TableColumn<BugEntry, String> titleCol;
    @FXML private TableColumn<BugEntry, String> statusCol;
    @FXML private TableColumn<BugEntry, String> tagsCol;
    @FXML private TableColumn<BugEntry, String> createdCol;
    @FXML private TableColumn<BugEntry, String> updatedCol;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final LogDAO logDAO = new LogDAO();

    @FXML
    public void initialize() {
        statusFilterCombo.setItems(FXCollections.observableArrayList("ALL", "OPEN", "RESOLVED"));
        statusFilterCombo.setValue("ALL");
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        titleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        tagsCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTags() == null ? "" : d.getValue().getTags()));
        createdCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() == null ? "" : d.getValue().getCreatedAt().format(DATE_FMT)
        ));
        updatedCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUpdatedAt() == null ? "" : d.getValue().getUpdatedAt().format(DATE_FMT)
        ));

        loadAvailableTags();
        onSearch();
    }

    private void loadAvailableTags() {
        try {
            int userId = SceneNavigator.getCurrentUserId();
            List<BugEntry> allLogs = logDAO.getAllLogsByUser(userId);
            Set<String> uniqueTags = new TreeSet<>();
            uniqueTags.add("ALL");

            for (BugEntry log : allLogs) {
                if (log.getTags() != null && !log.getTags().isBlank()) {
                    String[] tags = log.getTags().split(",");
                    for (String tag : tags) {
                        String clean = tag.trim();
                        if (!clean.isEmpty()) {
                            uniqueTags.add(clean);
                        }
                    }
                }
            }

            String existing = tagFilterCombo.getValue();
            tagFilterCombo.setItems(FXCollections.observableArrayList(uniqueTags));
            if (existing != null && uniqueTags.contains(existing)) {
                tagFilterCombo.setValue(existing);
            } else {
                tagFilterCombo.setValue("ALL");
            }
        } catch (Exception e) {
            System.err.println("Error loading tags: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        int userId = SceneNavigator.getCurrentUserId();
        String query = searchField.getText() == null ? "" : searchField.getText().trim();
        String statusFilter = statusFilterCombo.getValue() == null ? "ALL" : statusFilterCombo.getValue();
        String tagFilter = tagFilterCombo.getValue() == null ? "ALL" : tagFilterCombo.getValue();

        List<BugEntry> filtered = logDAO.searchLogs(userId, query, statusFilter, tagFilter);
        resultsTable.getItems().setAll(filtered);
        resultsLabel.setText("Showing " + filtered.size() + " results");
    }

    @FXML
    private void onClear() {
        searchField.clear();
        statusFilterCombo.setValue("ALL");
        loadAvailableTags();
        tagFilterCombo.setValue("ALL");
        onSearch();
    }

    @FXML
    private void onFiltersChanged() {
        onSearch();
    }

    @FXML
    private void onRowClick() {
        BugEntry selected = resultsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SceneNavigator.goTo("bug-entry-detail.fxml", selected.getId());
        }
    }

    @FXML
    private void onBack() {
        SceneNavigator.goTo("dashboard.fxml");
    }

    @FXML
    private void onGoToProfile() {
        SceneNavigator.goTo("profile.fxml");
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
}
