package com.phantam.moviedesktopapp.Service;

import com.phantam.moviedesktopapp.Database.CustomerDatabase;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class OTPService {

    private final static CustomerDatabase customerDatabase = new CustomerDatabase();

    public static String generateOTP() {
        int otp = new Random().nextInt(900000) + 100000; // 6 chữ số
        return String.valueOf(otp);
    }

    public static void saveOTPToDB(String email, String otp) {
        customerDatabase.CustomerDatabaseConnection();
        try (PreparedStatement stmt = customerDatabase.getConn().prepareStatement(
                "INSERT INTO otp_codes (email, code, expires_at) VALUES (?, ?, NOW() + INTERVAL 5 MINUTE)")) {
            stmt.setString(1, email);
            stmt.setString(2, otp);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            customerDatabase.closeConnection();
        }
    }
}