
package com.example.QuanLyPhongMayBackEnd.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;  // Inject JavaMailSender
    @Async("emailTaskExecutor") // Chỉ định sử dụng Executor có tên "emailTaskExecutor" cho phương thức bất đồng bộ
    public void sendConfirmationEmailAsync(String recipientEmail) { // Giữ nguyên tên phương thức bất đồng bộ
        sendConfirmationEmail(recipientEmail); // Gọi phương thức gửi email đồng bộ bên dưới
        System.out.println("Confirmation email sending task submitted for: " + recipientEmail + " using emailTaskExecutor");
    }
    // Method to send confirmation email
    public void sendConfirmationEmail(String recipientEmail) {
        try {
            // Create a MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set email properties
            helper.setTo(recipientEmail);
            helper.setSubject("Đăng ký tài khoản thành công");
            helper.setText("Chúc mừng bạn đã đăng ký tài khoản thành công tại hệ thống chúng tôi!");

            // Send email
            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    public void sendOtp(String email, String otp) {
        String subject = "Xác thực tài khoản QuanLiPhongMay";
        String message = "Mã Otp của bạn: " + otp + ". Mã Otp sẽ hết hạn trong vòng 2 phút" ;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }
}
