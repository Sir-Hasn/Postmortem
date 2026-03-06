package postmortem;
import javafx.application.Application;
import postmortem.util.SceneNavigator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneNavigator.setStage(stage);
        stage.setTitle("Postmortem");
        stage.setResizable(false);
        SceneNavigator.goTo("login.fxml");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

    /*public void start(Stage primaryStage) {
        // Create button and set action
        Button btn = new Button("Say 'Hello World'");
        btn.setOnAction(e -> System.out.println("Hello World!"));

        // Set up scene and stage
        StackPane root = new StackPane(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.setTitle("Hello World!");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }*/
/*        String url = "jdbc:sqlite:test.db"; // This will create the file if it doesn't exist
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
 */
