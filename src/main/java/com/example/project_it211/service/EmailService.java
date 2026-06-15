package com.example.project_it211.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gửi OTP về email người dùng.
     * Nếu chưa cấu hình Gmail → in OTP ra console (chế độ DEMO).
     * Nếu đã cấu hình Gmail → gửi email thật.
     */
    public void sendOtpEmail(String toEmail, String otp, long expirationMinutes) {
        // Chế độ DEMO: chưa cấu hình Gmail → in ra console
        if (isEmailNotConfigured()) {
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║           [DEMO] MÃ OTP                 ║");
            System.out.println("║  Email : " + toEmail);
            System.out.println("║  OTP   : " + otp);
            System.out.println("║  Hạn   : " + expirationMinutes + " phút");
            System.out.println("╚══════════════════════════════════════════╝");
            return;
        }

        // Gửi email thật qua Gmail SMTP
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[IT211] Mã OTP đặt lại mật khẩu");
            message.setText(
                "Xin chào,\n\n" +
                "Bạn vừa yêu cầu đặt lại mật khẩu.\n\n" +
                "Mã OTP của bạn là: " + otp + "\n" +
                "Mã có hiệu lực trong " + expirationMinutes + " phút.\n\n" +
                "Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này.\n\n" +
                "Trân trọng."
            );
            mailSender.send(message);
            System.out.println("[INFO] Đã gửi OTP tới email: " + toEmail);
        } catch (Exception e) {
            System.err.println("[ERROR] Gửi email thất bại: " + e.getMessage());
            throw new IllegalArgumentException(
                "Không thể gửi email OTP. Vui lòng kiểm tra cấu hình Gmail.");
        }
    }

    private boolean isEmailNotConfigured() {
        return fromEmail == null
            || fromEmail.isBlank()
            || fromEmail.equals("your_email@gmail.com");
    }
}
