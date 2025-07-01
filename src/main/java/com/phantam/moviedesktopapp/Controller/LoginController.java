package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Database.CustomerDatabase;
import com.phantam.moviedesktopapp.Model.Session;
import com.phantam.moviedesktopapp.Util.BCryptUtils;
import com.phantam.moviedesktopapp.Util.SwitchStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.phantam.moviedesktopapp.Util.AlertUtils.showAlert;

public class LoginController {

    private final SwitchStage switchStage = new SwitchStage();
    private final CustomerDatabase customerDatabase = new CustomerDatabase();
    private final Session session = new Session();

    @FXML
    private CheckBox remenberMeCheck;

    // button xử lý sự kiện login
    @FXML
    private Button loginButton;

    // hyperlink để chuyển đến trang đăng ký
    @FXML
    private Hyperlink registerButton;

    // text fields để nhập username và password
    @FXML
    private TextField emailInput;
    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button exitButton, minimizeButton;

    @FXML
    public void initialize() {
        exitButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        minimizeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
    }

    // Animation của button login
    @FXML
    protected void onLoginEnterd() {
        loginButton.setOpacity(0.7);
    }

    @FXML
    protected void onLoginExited() {
        loginButton.setOpacity(1.0);
    }

    /**
     * Chuyển đến trang đăng ký khi người dùng click vào hyperlink.
     *
     * @param event Sự kiện click vào hyperlink.
     */
    @FXML
    public void switchToRegisterStage(ActionEvent event) {
        switchStage.switchToStage(event, "/com/phantam/moviedesktopapp/fxml/register-view.fxml", "Movie Application | Register", true);
    }

    @FXML
    public void switchToForgotPasswordStage(ActionEvent event) {
        switchStage.switchToStage(event, "/com/phantam/moviedesktopapp/fxml/forgot-password-view.fxml", "Movie Application | Forgot Password", true);
    }

    /**
     * Xử lý sự kiện click vào nút đăng nhập.
     * Kiểm tra thông tin đăng nhập và thực hiện đăng nhập nếu thông tin hợp lệ.
     *
     * @param event Sự kiện click vào nút đăng nhập.
     */
    @FXML
    protected void onLoginClicked(ActionEvent event) {
        String email = emailInput.getText();
        String password = passwordInput.getText();
        emailInput.setAccessibleHelp("Vui lòng nhập email của bạn");

        if (LoginCheck(email, password)) {
            showAlert("information", "Đăng nhập thành công", "Chào mừng bạn!");
            switchStage.switchToStage(event, "/com/phantam/moviedesktopapp/fxml/main-menu-view.fxml", "Movie Application | Main", true);
        }
    }

    private boolean LoginCheck(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("warning", "Thiếu thông tin", "Vui lòng nhập email và mật khẩu.");
            return false;
        }

        String sql = "SELECT c.id, p.email, c.password FROM Customers c JOIN Profile p ON c.id = p.id WHERE p.email = ?";

        customerDatabase.CustomerDatabaseConnection();
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String passwordFromDB = rs.getString("password");

                if (BCryptUtils.checkPassword(password, passwordFromDB)) {
                    if (remenberMeCheck.isSelected()) {
                        // Lưu thông tin đăng nhập nếu checkbox được chọn
                        Session.login(email, rs.getInt("id"));
                    } else {
                        // Không lưu thông tin đăng nhập
                        Session.logout();
                    }
                    return true;
                }
            }

            showAlert("error", "Đăng nhập thất bại", "Email hoặc mật khẩu không đúng.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("error", "Lỗi kết nối", "Không thể kết nối đến cơ sở dữ liệu.");
        }

        return false;
    }


    @FXML
    protected void onExitButtonClicked() {
        System.exit(0);
    }
    @FXML
    protected void onMiniButtonClicked() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
}