package postmortem.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import postmortem.dao.LogDAO;
import postmortem.model.BugEntry;
import postmortem.util.SceneNavigator;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label entryCountLabel;
    @FXML private Label tagSummaryLabel;
    @FXML private ComboBox<String> tagFilterCombo;
    @FXML private TableView<BugEntry> bugTable;
    @FXML private TableColumn<BugEntry, String> titleCol;
    @FXML private TableColumn<BugEntry, String> statusCol;
    @FXML private TableColumn<BugEntry, String> tagsCol;
    @FXML private TableColumn<BugEntry, String> createdCol;
    @FXML private TableColumn<BugEntry, String> updatedCol;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final LogDAO logDAO = new LogDAO();
    private String activeStatusFilter = "ALL";
    private boolean refreshingTagFilter = false;

    @FXML
    public void initialize() {
        setupTableColumns();
        bugTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loadUserData();
    }

    private void setupTableColumns() {
        titleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        tagsCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTags() == null ? "" : d.getValue().getTags()
        ));
        createdCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() == null ? "" : d.getValue().getCreatedAt().format(DATE_FMT)
        ));
        updatedCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getUpdatedAt() == null ? "" : d.getValue().getUpdatedAt().format(DATE_FMT)
        ));
    }

    private void loadUserData() {
        String currentUser = SceneNavigator.getCurrentUser();
        int userId = SceneNavigator.getCurrentUserId();

        usernameLabel.setText(currentUser != null ? currentUser : "User");

        if (userId < 0) {
            bugTable.setItems(FXCollections.observableArrayList());
            entryCountLabel.setText("0 entries");
            tagFilterCombo.setItems(FXCollections.observableArrayList("ALL"));
            tagFilterCombo.setValue("ALL");
            tagSummaryLabel.setText("No tags");
            return;
        }

        applyFilters();
    }

    private void applyFilters() {
        int userId = SceneNavigator.getCurrentUserId();
        String selectedTag = tagFilterCombo.getValue() == null ? "ALL" : tagFilterCombo.getValue();

        List<BugEntry> bugs = logDAO.searchLogs(userId, "", activeStatusFilter, selectedTag);
        bugTable.setItems(FXCollections.observableArrayList(bugs));

        if ("ALL".equals(activeStatusFilter)) {
            entryCountLabel.setText(bugs.size() + " entries");
        } else {
            entryCountLabel.setText(bugs.size() + " " + activeStatusFilter + " entries");
        }

        loadTagData(logDAO.getAllLogsByUser(userId));
    }

    private void loadTagData(List<BugEntry> allEntries) {
        refreshingTagFilter = true;
        Map<String, Integer> tagCount = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (BugEntry bug : allEntries) {
            if (bug.getTags() == null || bug.getTags().isBlank()) {
                continue;
            }
            String[] tags = bug.getTags().split(",");
            for (String raw : tags) {
                String tag = raw.trim();
                if (tag.isEmpty()) {
                    continue;
                }
                tagCount.put(tag, tagCount.getOrDefault(tag, 0) + 1);
            }
        }

        List<String> comboItems = new ArrayList<>();
        comboItems.add("ALL");
        comboItems.addAll(tagCount.keySet());

        String prevSelection = tagFilterCombo.getValue();
        tagFilterCombo.setItems(FXCollections.observableArrayList(comboItems));
        if (prevSelection != null && comboItems.contains(prevSelection)) {
            tagFilterCombo.setValue(prevSelection);
        } else {
            tagFilterCombo.setValue("ALL");
        }

        if (tagCount.isEmpty()) {
            tagSummaryLabel.setText("No tags");
            refreshingTagFilter = false;
            return;
        }

        StringBuilder summary = new StringBuilder("Tags:\n");
        for (Map.Entry<String, Integer> entry : tagCount.entrySet()) {
            summary.append(entry.getKey()).append("(").append(entry.getValue()).append(")\n");
        }
        tagSummaryLabel.setText(summary.toString().trim());
        refreshingTagFilter = false;
    }

    @FXML
    private void onRowClick() {
        BugEntry selected = bugTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            SceneNavigator.goTo("bug-entry-detail.fxml", selected.getId());
        }
    }

    @FXML
    private void onTagFilterChanged() {
        if (refreshingTagFilter) {
            return;
        }
        applyFilters();
    }

    @FXML
    private void onNewEntry() {
        SceneNavigator.setSelectedBugId(-1);
        SceneNavigator.goTo("bug-entry-form.fxml");
    }

    @FXML
    private void onGoToSearch() {
        SceneNavigator.goTo("search.fxml");
    }

    @FXML
    private void onGoToProfile() {
        SceneNavigator.goTo("profile.fxml");
    }

    @FXML
    private void onFilterAll() {
        activeStatusFilter = "ALL";
        applyFilters();
    }

    @FXML
    private void onFilterOpen() {
        activeStatusFilter = "OPEN";
        applyFilters();
    }

    @FXML
    private void onFilterResolved() {
        activeStatusFilter = "RESOLVED";
        applyFilters();
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

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
