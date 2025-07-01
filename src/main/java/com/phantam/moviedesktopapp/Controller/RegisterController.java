package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Database.CustomerDatabase;
import com.phantam.moviedesktopapp.Util.BCryptUtils;
import com.phantam.moviedesktopapp.Util.SwitchStage;
import com.phantam.moviedesktopapp.Util.ValidateUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.phantam.moviedesktopapp.Util.AlertUtils.showAlert;

public class RegisterController {

    private final CustomerDatabase customerDatabase = new CustomerDatabase();
    private final ValidateUtils validateUtils = new ValidateUtils();
    private final SwitchStage switchStage = new SwitchStage();

    @FXML
    private ComboBox<String> countryList;
    @FXML
    private TextField nameField, lastNameField, emailField, phoneNumberField, zipCodeField;
    @FXML
    private PasswordField passwordField, repeatPasswordField;
    @FXML
    private DatePicker dobPicker;

    @FXML
    public void initialize() {
        ObservableList<String> countries = Stream.of(Locale.getISOCountries())
                .map(code -> new Locale("", code))
                .map(Locale::getDisplayCountry)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        countryList.setItems(countries);
    }

    @FXML
    protected void onClickRegisterButton(ActionEvent event) {
        if (!checkRegisterInputs()) return;

        customerDatabase.CustomerDatabaseConnection();
        try {
            registerCustomer();
            showAlert("information", "Thành công", "Đăng ký thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("error", "Lỗi", "Không thể đăng ký: " + e.getMessage());
        } finally {
            try {
                customerDatabase.getConn().close();
            } catch (SQLException ignored) {
            }
        }
    }

    private void registerCustomer() throws SQLException {
        Connection conn = customerDatabase.getConn();
        conn.setAutoCommit(false);

        // 1. Insert Location
        String locationSql = "INSERT INTO Location (address, country, zipcode) VALUES (?, ?, ?)";
        try (PreparedStatement stmtLoc = conn.prepareStatement(locationSql, Statement.RETURN_GENERATED_KEYS)) {
            stmtLoc.setString(1, null);
            stmtLoc.setString(2, countryList.getValue());
            stmtLoc.setString(3, zipCodeField.getText());
            stmtLoc.executeUpdate();

            ResultSet rs = stmtLoc.getGeneratedKeys();
            if (!rs.next()) throw new SQLException("Không thể lấy locationID");
            int locationID = rs.getInt(1);

            // 2. Insert Profile
            String profileSql = "INSERT INTO Profile (name, lastname, date_of_birth, phone, email, cccd) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtProf = conn.prepareStatement(profileSql, Statement.RETURN_GENERATED_KEYS)) {
                stmtProf.setString(1, nameField.getText().toUpperCase());
                stmtProf.setString(2, lastNameField.getText().toUpperCase());
                stmtProf.setNull(3, Types.DATE);       // date_of_birth
                stmtProf.setString(4, phoneNumberField.getText());
                stmtProf.setString(5, emailField.getText());
                stmtProf.setNull(6, Types.VARCHAR);    // cccd
                stmtProf.executeUpdate();

                ResultSet rs2 = stmtProf.getGeneratedKeys();
                if (!rs2.next()) throw new SQLException("Không thể lấy profileID");
                int profileID = rs2.getInt(1);

                // 3. Insert Customer
                String customerSql = "INSERT INTO Customers (password, balance, locationID, profileID) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtCus = conn.prepareStatement(customerSql)) {
                    stmtCus.setString(1, BCryptUtils.hashPassword(passwordField.getText()));
                    stmtCus.setBigDecimal(2, new java.math.BigDecimal("0.00"));
                    stmtCus.setInt(3, locationID);
                    stmtCus.setInt(4, profileID);
                    stmtCus.executeUpdate();
                }
            }
        }

        conn.commit();
    }

    private boolean checkRegisterInputs() {
        if (isEmpty(nameField) || isEmpty(lastNameField) || isEmpty(emailField) ||
                isEmpty(phoneNumberField) || isEmpty(passwordField) || isEmpty(repeatPasswordField)) {
            showAlert("warning", "Cảnh báo", "Vui lòng điền đầy đủ thông tin!");
            return false;
        }

        if (!ValidateUtils.isValidName(nameField.getText()) || !ValidateUtils.isValidName(lastNameField.getText())) {
            showAlert("error", "Lỗi", "Tên hoặc họ không hợp lệ!");
            return false;
        }

        if (!validateUtils.isValidPassword(passwordField.getText())) {
            showAlert("error", "Lỗi", "Mật khẩu cần ít nhất 8 ký tự, 1 chữ hoa và 1 ký tự đặc biệt.");
            return false;
        }

        if (!passwordField.getText().equals(repeatPasswordField.getText())) {
            showAlert("error", "Lỗi", "Mật khẩu không khớp!");
            return false;
        }

        if (countryList.getValue() == null || countryList.getValue().isEmpty()) {
            showAlert("warning", "Cảnh báo", "Vui lòng chọn quốc gia!");
            return false;
        }

        if (isEmpty(zipCodeField) || !ValidateUtils.isValidZipCode(zipCodeField.getText())) {
            showAlert("error", "Lỗi", "Mã bưu điện không hợp lệ!");
            return false;
        }

        if (!phoneNumberField.getText().matches("\\d{10,15}") ||
                !ValidateUtils.isValidPhoneNumber(phoneNumberField.getText())) {
            showAlert("error", "Lỗi", "Số điện thoại không hợp lệ!");
            return false;
        }

        if (validateUtils.isPhoneExists(phoneNumberField.getText())) {
            showAlert("error", "Lỗi", "Số điện thoại đã tồn tại!");
            return false;
        }

        if (!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") ||
                !ValidateUtils.isValidEmail(emailField.getText())) {
            showAlert("error", "Lỗi", "Email không hợp lệ!");
            return false;
        }

        if (validateUtils.isEmailExists(emailField.getText())) {
            showAlert("error", "Lỗi", "Email đã được sử dụng!");
            return false;
        }

        return true;
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    @FXML
    protected void switchToLoginStage(ActionEvent event) {
        switchStage.switchToStage(event, "/com/phantam/moviedesktopapp/fxml/login-view.fxml", "Movie Application | Login", true);
    }
}
