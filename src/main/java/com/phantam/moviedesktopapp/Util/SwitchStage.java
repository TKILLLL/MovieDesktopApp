package com.phantam.moviedesktopapp.Util;

import com.phantam.moviedesktopapp.App.Main;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SwitchStage {

    private FXMLLoader loader;

    /**
     * Chuyển sang Stage mới với FXML tương ứng.
     *
     * @param event       Sự kiện (ActionEvent, MouseEvent...) để lấy Stage hiện tại.
     * @param fxmlPath    Đường dẫn FXML.
     * @param title       Tiêu đề Stage mới.
     * @param undecorated Có bỏ viền cửa sổ hay không.
     */
    public void switchToStage(Event event, String fxmlPath, String title, boolean undecorated) {
        try {
            // Load FXML
            loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Lấy Stage hiện tại và đóng nó
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Tạo Stage mới
            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);

            if (undecorated) {
                newStage.initStyle(StageStyle.UNDECORATED);
            }

            newStage.show();

        } catch (IOException e) {
            System.err.println("Lỗi khi chuyển Stage: " + e.getMessage());
            e.printStackTrace();
            // Có thể hiện Alert cho user nếu muốn
        }
    }

    /**
     * Chuyển sang Stage mới với FXML tương ứng.
     *
     * @param fxmlPath    Đường dẫn FXML.
     * @param title       Tiêu đề Stage mới.
     * @param undecorated Có bỏ viền cửa sổ hay không.
     */
    public <T> T switchToStage(String fxmlPath, String title, boolean undecorated) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);

            if (undecorated) {
                newStage.initStyle(StageStyle.UNDECORATED);
            }

            newStage.show();

            return loader.getController(); // Trả về controller để gọi setMovieId

        } catch (IOException e) {
            System.err.println("Lỗi khi chuyển Stage: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Đóng Stage hiện tại.
     *
     * @param event Sự kiện (ActionEvent, MouseEvent...) để lấy Stage hiện tại.
     */
    public void closeCurrentStage(Event event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public FXMLLoader getLoader() {
        return loader;
    }
}
