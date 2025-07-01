package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Database.CustomerDatabase;
import com.phantam.moviedesktopapp.Util.BCryptUtils;
import com.phantam.moviedesktopapp.Util.ValidateUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.phantam.moviedesktopapp.Util.AlertUtils.showAlert;

public class ChangePasswordController {

    private final static ValidateUtils validateUtils = new ValidateUtils();
    private final CustomerDatabase customerDatabase = new CustomerDatabase();
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private TextField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;


    /**
     * Xử lý sự kiện khi người dùng click vào nút "Change Password".
     * Kiểm tra tính hợp lệ của mật khẩu mới và xác nhận mật khẩu.
     * Nếu hợp lệ, thực hiện thay đổi mật khẩu.
     */
    @FXML
    protected void onChangePasswordButtonClicked(ActionEvent event) {

        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("error", "Lỗi", "Vui lòng nhập mật khẩu mới và xác nhận mật khẩu.");
            return;
        }

        // Kiểm tra định dạng mật khẩu
        if (!validateUtils.isValidPassword(newPassword)) {
            showAlert("error", "Lỗi", "Mật khẩu cần ít nhất 8 ký tự, 1 chữ hoa và 1 ký tự đặc biệt.");
            return;
        }

        // So khớp mật khẩu xác nhận
        if (!newPassword.equals(confirmPassword)) {
            showAlert("error", "Lỗi", "Mật khẩu xác nhận không khớp.");
            return;
        }

        // Băm mật khẩu
        String hashedPassword = BCryptUtils.hashPassword(newPassword);

        customerDatabase.CustomerDatabaseConnection();
        String sql = "UPDATE Customers SET password = ? WHERE id = (SELECT id FROM Profile WHERE email = ?)";
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, this.email);// Email được lấy từ bước xác thực OTP
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert("information", "Thành công", "Mật khẩu đã được thay đổi thành công.");
            } else {
                showAlert("error", "Thất bại", "Không tìm thấy tài khoản để cập nhật.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("error", "Lỗi CSDL", "Không thể cập nhật mật khẩu.");
        } finally {
            try {
                customerDatabase.getConn().close();
            } catch (Exception ignored) {
            }
        }
    }

}
