package com.phantam.moviedesktopapp.Util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSenderUtils {

    private static final String senderEmail = "32contam10a3@gmail.com";
    private static final String appPassword = "fzkz oplb vdqs mfhq";

    public static void sendOTP(String toEmail, String otpCode) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã xác nhận tài khoản");
            message.setText("Mã xác thực của bạn là: " + otpCode + "\nHiệu lực trong 5 phút.");

            Transport.send(message);
            System.out.println("OTP đã được gửi tới email " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
