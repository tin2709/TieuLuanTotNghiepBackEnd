package com.example.QuanLyPhongMayBackEnd;

import io.sentry.Sentry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
	@Bean // VERY IMPORTANT: Global CORS configuration
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // Apply to all endpoints
						.allowedOrigins("http://localhost:3000") // Your React app's origin
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
						.allowedHeaders("*") // Allowed headers
						.allowCredentials(true); // If you need to send cookies
			}
		};
	}
}
