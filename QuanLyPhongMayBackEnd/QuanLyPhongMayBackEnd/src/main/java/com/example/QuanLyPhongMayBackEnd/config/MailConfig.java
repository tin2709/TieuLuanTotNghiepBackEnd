package com.example.QuanLyPhongMayBackEnd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("21130565@st.hcmuaf.edu.vn");
        mailSender.setPassword("lbtz bfwz vknq zivp");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean(name = "emailTaskExecutor") // Định nghĩa Bean với tên "emailTaskExecutor"
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);         // Số lượng thread core (luôn sẵn sàng)
        executor.setMaxPoolSize(10);         // Số lượng thread tối đa
        executor.setQueueCapacity(100);       // Kích thước hàng đợi chờ (nếu vượt quá maxPoolSize)
        executor.setThreadNamePrefix("Email-Async-"); // Prefix cho tên thread (dễ theo dõi log)
        executor.initialize();                // Khởi tạo executor
        return executor;
    }
}