package postmortem;
import javafx.application.Application;
import javafx.scene.image.Image;
import postmortem.dao.LogDAO;
import postmortem.util.SceneNavigator;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneNavigator.setStage(stage);
        stage.setTitle("Postmortem");
        applyAppIcons(stage);
        stage.setResizable(true);
        SceneNavigator.goTo("login.fxml");
        stage.show();
        SceneNavigator.enforceFullscreenWindow();
    }

    private void applyAppIcons(Stage stage) {
        String[] iconPaths = {
                "/postmortem/resources/images/postmortem-icon-16.png",
                "/postmortem/resources/images/postmortem-icon-24.png",
                "/postmortem/resources/images/postmortem-icon-32.png",
                "/postmortem/resources/images/postmortem-icon-48.png",
                "/postmortem/resources/images/postmortem-icon-64.png",
                "/postmortem/resources/images/postmortem-icon-128.png",
                "/postmortem/resources/images/postmortem-icon-256.png"
        };

        for (String path : iconPaths) {
            URL resource = Main.class.getResource(path);
            if (resource != null) {
                stage.getIcons().add(new Image(resource.toExternalForm()));
            }
        }
    }

    public static void main(String[] args) {

        String url = "jdbc:sqlite:postmortem.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "    Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    UserName TEXT UNIQUE NOT NULL," +
                "    PasswordHash TEXT NOT NULL," +
                "    Salt TEXT NOT NULL," +
                "    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();

                stmt.execute(sql);
                new LogDAO();
                System.out.println("Table is created.");
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }


        launch(args);
    }
}

 /*
 **TODOS:Search, Filter in search, Add new entry, View by tags, View by status, Delete log, change log status, edit log
 * - Implement user authentication
 * - Create a database schema for storing user info, logs and other relevant data
 * - Design the UI for the main dashboard after login
 * - Implement functionality for viewing and analyzing logs
 * - Add error handling and input validation
 * - Write unit tests for critical components
 * - Optimize database queries for better performance
 * - Implement a feature for exporting logs to a file
 * - Add support for multiple users and roles (e.g., admin, user)
 * - Implement a search functionality for logs
 * - Add a feature for real-time log monitoring
 * - Implement a feature for categorizing and tagging logs
 * - Design a settings page for user preferences
 * - Implement a feature for generating reports based on logs
  */