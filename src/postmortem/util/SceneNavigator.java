package postmortem.util;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneNavigator {

    private static Stage primaryStage;
    private static String currentUser = null;
    private static int currentUserId = -1;
    private static int selectedBugId = -1;
    private static double dragOffsetX;
    private static double dragOffsetY;
    private static double restoreX = Double.NaN;
    private static double restoreY = Double.NaN;
    private static double restoreWidth = 1100;
    private static double restoreHeight = 680;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setSelectedBugId(int bugId) {
        selectedBugId = bugId;
    }

    public static int getSelectedBugId() {
        return selectedBugId;
    }

    public static void logout() {
        currentUser = null;
        currentUserId = -1;
        selectedBugId = -1;
    }

    public static void goTo(String fxmlFile) {
        goTo(fxmlFile, -1);
    }

    public static void goTo(String fxmlFile, int bugId) {
        try {
            if (bugId >= 0) {
                selectedBugId = bugId;
            }

            URL fxmlUrl = SceneNavigator.class.getResource("/postmortem/resources/fxml/" + fxmlFile);

            System.out.println("Loading FXML from: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.err.println("ERROR: Cannot find FXML file: " + fxmlFile);
                System.err.println("Check that your resources folder is marked as a Resources Root in IntelliJ.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());

            URL cssUrl = SceneNavigator.class.getResource("/postmortem/resources/css/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setScene(scene);
            updateRestoreSizeFromScene(scene);
            enforceFullscreenWindow();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void minimizeWindow() {
        if (primaryStage != null) {
            primaryStage.setIconified(true);
        }
    }

    public static void toggleMaximizeWindow() {
        if (primaryStage != null) {
            if (primaryStage.isMaximized()) {
                primaryStage.setMaximized(false);

                if (!Double.isNaN(restoreX) && !Double.isNaN(restoreY)) {
                    primaryStage.setX(restoreX);
                    primaryStage.setY(restoreY);
                }

                primaryStage.setWidth(Math.max(restoreWidth, 900));
                primaryStage.setHeight(Math.max(restoreHeight, 600));
                primaryStage.centerOnScreen();
            } else {
                rememberCurrentBounds();
                primaryStage.setMaximized(true);
            }
        }
    }

    public static void closeWindow() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

    public static void enforceFullscreenWindow() {
        if (primaryStage == null) {
            return;
        }

        primaryStage.setIconified(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        // Use explicit bounds instead of maximize state to avoid Windows maximize glitches.
        primaryStage.setMaximized(false);
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
    }

    private static void wireWindowDrag(Scene scene) {
        scene.setOnMousePressed(event -> {
            if (primaryStage == null || primaryStage.isMaximized()) {
                return;
            }
            dragOffsetX = event.getSceneX();
            dragOffsetY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            if (primaryStage == null || primaryStage.isMaximized()) {
                return;
            }
            primaryStage.setX(event.getScreenX() - dragOffsetX);
            primaryStage.setY(event.getScreenY() - dragOffsetY);
        });
    }

    private static void rememberCurrentBounds() {
        if (primaryStage == null) {
            return;
        }

        if (!primaryStage.isMaximized()) {
            restoreX = primaryStage.getX();
            restoreY = primaryStage.getY();
            if (primaryStage.getWidth() > 0) {
                restoreWidth = primaryStage.getWidth();
            }
            if (primaryStage.getHeight() > 0) {
                restoreHeight = primaryStage.getHeight();
            }
        }
    }

    private static void updateRestoreSizeFromScene(Scene scene) {
        if (scene == null || scene.getRoot() == null) {
            return;
        }

        double prefWidth = scene.getRoot().prefWidth(-1);
        double prefHeight = scene.getRoot().prefHeight(-1);

        if (prefWidth > 0) {
            restoreWidth = prefWidth;
        }
        if (prefHeight > 0) {
            restoreHeight = prefHeight;
        }
    }
}