package com.example.QuanLyPhongMayBackEnd.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;  // Inject JavaMailSender

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
}
