package com.phantam.moviedesktopapp.Util;

import com.phantam.moviedesktopapp.Database.CustomerDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidateUtils {

    private final CustomerDatabase customerDatabase = new CustomerDatabase();

    /**
     * Kiểm tra xem một chuỗi có phải là email hợp lệ hay không.
     *
     * @param email Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là email hợp lệ, false nếu không.
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Kiểm tra xem email đã tồn tại trong cơ sở dữ liệu hay chưa.
     *
     * @param text Email cần kiểm tra.
     * @return true nếu email đã tồn tại, false nếu không.
     */
    public boolean isEmailExists(String text) {
        customerDatabase.CustomerDatabaseConnection();
        String sql = "SELECT COUNT(*) FROM Customers WHERE email = ?";
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(sql)) {
            stmt.setString(1, text);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra xem một chuỗi có phải là số điện thoại hợp lệ hay không.
     *
     * @param phoneNumber Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là số điện thoại hợp lệ, false nếu không.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?[0-9]{10,15}$"; // Ví dụ: +1234567890 hoặc 1234567890
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }

    /**
     * Kiểm tra xem phone đã tồn tại trong cơ sở dữ liệu hay chưa.
     *
     * @param text Phone cần kiểm tra.
     * @return true nếu phone đã tồn tại, false nếu không.
     */
    public boolean isPhoneExists(String text) {
        customerDatabase.CustomerDatabaseConnection();
        String sql = "SELECT COUNT(*) FROM Customers WHERE phone = ?";
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(sql)) {
            stmt.setString(1, text);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra xem một chuỗi có phải là mã bưu điện hợp lệ hay không.
     *
     * @param zipCode Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là mã bưu điện hợp lệ, false nếu không.
     */
    public static boolean isValidZipCode(String zipCode) {
        String zipCodeRegex = "^[0-9]{5}(?:-[0-9]{4})?$"; // Ví dụ: 12345 hoặc 12345-6789
        return zipCode != null && zipCode.matches(zipCodeRegex);
    }

    /**
     * Kiểm tra xem một chuỗi có phải là tên hợp lệ hay không.
     *
     * @param name Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là tên hợp lệ, false nếu không.
     */
    public static boolean isValidName(String name) {
        String nameRegex = "^[a-zA-ZÀ-ÿ\\s'-]+$"; // Hỗ trợ các ký tự tiếng Việt và khoảng trắng
        return name != null && name.matches(nameRegex);
    }

    /**
     * Kiểm tra xem một chuỗi có phải là mật khẩu hợp lệ hay không.
     * Mật khẩu hợp lệ phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.
     *
     * @param password Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là mật khẩu hợp lệ, false nếu không.
     */
    public boolean isValidPassword(String password) {
        // Mật khẩu phải có: ít nhất 1 chữ thường, 1 chữ hoa, 1 số, 1 ký tự đặc biệt và ≥8 ký tự
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    /**
     * Kiểm tra xem một chuỗi có phải là ngày sinh hợp lệ hay không.
     * Ngày sinh hợp lệ phải ở định dạng YYYY-MM-DD.
     *
     * @param dateOfBirth Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là ngày sinh hợp lệ
     * , false nếu không.
     */
    public static boolean isValidDateOfBirth(String dateOfBirth) {
        String dateRegex = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"; // YYYY-MM-DD
        return dateOfBirth != null && dateOfBirth.matches(dateRegex);
    }

    /**
     * Kiểm tra xem một chuỗi có phải là địa chỉ hợp lệ hay không.
     * Địa chỉ hợp lệ có thể chứa chữ cái, số, khoảng trắng và các ký tự đặc biệt.
     *
     * @param address Chuỗi cần kiểm tra.
     * @return true nếu chuỗi là địa chỉ hợp lệ, false nếu không.
     */
    public static boolean isValidAddress(String address) {
        String addressRegex = "^[a-zA-Z0-9À-ÿ\\s,.'-]+$"; // Hỗ trợ các ký tự tiếng Việt, khoảng trắng và dấu câu
        return address != null && address.matches(addressRegex);
    }
}
