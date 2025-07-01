package com.phantam.moviedesktopapp.App;

import com.phantam.moviedesktopapp.Controller.MainViewController;
import com.phantam.moviedesktopapp.Database.CustomerDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {

    private final CustomerDatabase customerDatabase = new CustomerDatabase();
    public static Stage primaryStage;

    /**
     * Lấy Stage chính của ứng dụng.
     * @return Stage chính.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Kết nối đến database
        customerDatabase.CustomerDatabaseConnection();
        if (CustomerDatabase.isIsConnected()) {
            System.out.println("✅ Kết nối database thành công!");
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/phantam/moviedesktopapp/fxml/main-menu-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Movie Application | phantam");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } else {
            System.out.println("❌ Kết nối với database thất bại!");
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        launch();
    }

}
