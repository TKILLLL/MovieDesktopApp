package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Database.CustomerDatabase;
import com.phantam.moviedesktopapp.Service.OTPService;
import com.phantam.moviedesktopapp.Util.EmailSenderUtils;
import com.phantam.moviedesktopapp.Util.SwitchStage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.phantam.moviedesktopapp.Util.AlertUtils.showAlert;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

public class ForgotPasswordController {

    private final SwitchStage switchStage = new SwitchStage();
    private final CustomerDatabase customerDatabase = new CustomerDatabase();
    private ChangePasswordController changePasswordController;

    @FXML
    private HBox verifyHBox;
    @FXML
    private Button sendCodeButton;

    @FXML
    private Label notifyLabel;
    @FXML
    private TextField emailField, verifyCode;

    private final Label reVerifyTime = new Label();
    private Timeline timeLine;
    private int countdownTime = 30; // Thời gian đếm ngược 30 giây


    private String email;

    @FXML
    public void initialize() {
        // Căn giữa Label thông báo
        notifyLabel.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút xác nhận mã OTP
     *
     * @param event Sự kiện nhấn nút
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn cơ sở dữ liệu
     */
    @FXML
    protected void onConfirmButtonClicked(ActionEvent event) throws SQLException {
        email = emailField.getText().trim();
        String code = verifyCode.getText().trim();

        if (email.isEmpty() || code.isEmpty()) {
            notifyLabel.textFillProperty().setValue(RED);
            notifyLabel.setText("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // Kiểm tra mã OTP
        customerDatabase.CustomerDatabaseConnection();
        String sql = "SELECT * FROM otp_codes WHERE email = ? AND code = ? AND expires_at > NOW()";
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                notifyLabel.textFillProperty().setValue(GREEN);
                notifyLabel.setText("Xác thực thành công! Bạn có thể đặt lại mật khẩu.");
                showAlert("information", "Xác thực thành công", "Bạn có thể đặt lại mật khẩu mới.");
                // Tiến hành chuyển
                switchChangePasswordController(event);
            } else {
                notifyLabel.textFillProperty().setValue(RED);
                notifyLabel.setText("Mã xác thực không hợp lệ hoặc đã hết hạn.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void switchChangePasswordController(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/phantam/moviedesktopapp/fxml/change-password-view.fxml"));
        Parent root = loader.load();

        // Truyền email vào controller sau khi load xong
        ChangePasswordController changePasswordController = loader.getController();
        changePasswordController.setEmail(email);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Movie Application | Change Password");
        stage.show();
    }

    /**
     * Button trở về login-view.fxml
     */
    @FXML
    protected void switchToLoginStage(ActionEvent event) {
        switchStage.switchToStage(event, "/com/phantam/moviedesktopapp/fxml/login-view.fxml", "Movie Application | Login", true);
    }

    /**
     * Button xác thực mã OTP
     */
    @FXML
    protected void onSendCodeButtonClicked(ActionEvent event) {
        otpSystemDisplay();
    }

    /**
     * Hiển thị hệ thống xác thực OTP, thời gian đếm ngược và kiểm tra mã OTP
     */
    private void otpSystemDisplay() {
        String email = emailField.getText().trim();

        if (!emailCheck(email)) return; // Nếu email không hợp lệ thì dừng lại


        // Gửi mã OTP đến email
        String otp = OTPService.generateOTP(); // giả định bạn có generateOTP()
        EmailSenderUtils.sendOTP(email, otp);

        OTPService.saveOTPToDB(email, otp); // Lưu mã OTP vào cơ sở dữ liệu

        notifyLabel.textFillProperty().setValue(GREEN);
        notifyLabel.setText("Mã xác thực đã được gửi tới email của bạn.");

        // Bắt đầu hiển thị thời gian đếm ngược
        verifyHBox.getChildren().remove(sendCodeButton);
        verifyHBox.getChildren().add(0, reVerifyTime);
        reVerifyTime.setVisible(true);

        countdownTime = 30;
        reVerifyTime.setText(countdownTime + "s");

        if (timeLine != null) timeLine.stop();

        timeLine = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            countdownTime--;
            reVerifyTime.setText(countdownTime + "s");

            if (countdownTime <= 0) {
                timeLine.stop();
                verifyHBox.getChildren().remove(reVerifyTime);
                verifyHBox.getChildren().add(0, sendCodeButton);
            }
        }));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }


    private boolean emailCheck(String email) {
        if (email.isEmpty()) {
            notifyLabel.textFillProperty().setValue(RED);
            notifyLabel.setText("Vui lòng nhập email của bạn.");
            return false;
        }

        customerDatabase.CustomerDatabaseConnection();
        String sql = "SELECT email FROM Profile WHERE email = ?";
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                notifyLabel.textFillProperty().setValue(RED);
                notifyLabel.setText("Email không tồn tại trong hệ thống.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("error", "Lỗi database", "Không thể kết nối đến cơ sở dữ liệu.");
            return false;
        } finally {
            try {
                customerDatabase.getConn().close();
            } catch (Exception ignored) {
            }
        }
    }
}
