package postmortem.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneNavigator {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void goTo(String fxmlFile) {
        try {
            URL fxmlUrl = SceneNavigator.class.getResource("/postmortem/resources/fxml/" + fxmlFile);

            // Debug: print the path so you can see what's being looked up
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
            primaryStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}