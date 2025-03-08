package com.example.QuanLyPhongMayBackEnd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/logout").permitAll()  // Allow access to /logout
                                .anyRequest().permitAll()  // Allow access to all other endpoints
                )

                .httpBasic(httpBasic -> httpBasic.disable()) // Disable HTTP Basic Authentication
                .sessionManagement(session -> session.disable()) // Disable session management for stateless API
                .logout(logout -> logout
                        .logoutUrl("/logout")  // Define the logout URL
                        .permitAll()            // Allow everyone to access the logout URL
                        .logoutRequestMatcher(request -> request.getMethod().equals("GET"))  // Allow GET method for logout
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }
}
