package com.phantam.moviedesktopapp.Util;

import javafx.scene.control.Alert;

public class AlertUtils {

    /**
     * Hiển thị thông báo lỗi cho người dùng.
     * Hiện tại, phương thức này chỉ in ra console.
     *
     * @param message Thông điệp lỗi cần hiển thị.
     */
    public static void showAlert(String type, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (type == "error") {
            alert.setAlertType(Alert.AlertType.ERROR);
        } else if (type == "warning") {
            alert.setAlertType(Alert.AlertType.WARNING);
        } else if (type == "information") {
            alert.setAlertType(Alert.AlertType.INFORMATION);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
