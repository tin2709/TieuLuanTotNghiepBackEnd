package com.example.QuanLyPhongMayBackEnd;

import io.sentry.Sentry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuanLyPhongMayBackEndApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(QuanLyPhongMayBackEndApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Gửi một lỗi thủ công tới Sentry
		try {
			throw new Exception("Test exception to Sentry");
		} catch (Exception e) {
			Sentry.captureException(e);  // Gửi lỗi này đến Sentry
			System.out.println("Exception sent to Sentry");
		}
	}
}
