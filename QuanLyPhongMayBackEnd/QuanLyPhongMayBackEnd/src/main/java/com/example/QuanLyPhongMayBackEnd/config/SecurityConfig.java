package com.example.QuanLyPhongMayBackEnd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Tắt CSRF
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/**").permitAll()  // Cho phép truy cập tất cả API
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Tắt HTTP Basic Auth
                .sessionManagement(session -> session.disable()); // Vô hiệu hóa session (stateless API)

        return http.build();
    }
}
