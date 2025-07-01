package com.phantam.moviedesktopapp.Util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtils {

    /**
     * Mã hóa mật khẩu người dùng nhập vào.
     *
     * @param plainPassword Mật khẩu người dùng nhập.
     * @return Chuỗi đã mã hóa bằng BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Kiểm tra mật khẩu đã mã hóa với mật khẩu người dùng nhập vào.
     *
     * @param hashedPassword Mật khẩu đã mã hóa.
     * @param plainPassword  Mật khẩu người dùng nhập vào.
     * @return true nếu mật khẩu khớp, false nếu không khớp.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$") && !hashedPassword.startsWith("$2b$")) {
            System.err.println("Hash không hợp lệ: " + hashedPassword);
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }


}
