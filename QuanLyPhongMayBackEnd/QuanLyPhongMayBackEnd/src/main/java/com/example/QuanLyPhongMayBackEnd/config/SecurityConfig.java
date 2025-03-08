package com.example.QuanLyPhongMayBackEnd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Tắt CSRF cho API stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/logout", "/banUser", "/unbanUser").permitAll()  // Allow access to /logout, /banUser, and /unbanUser

                        .anyRequest().permitAll()  // Cho phép tất cả request
                )
                .anonymous(withDefaults())  // Bật Anonymous Authentication
                .formLogin(login -> login.disable())  // Tắt form login để tránh redirect loop
                .httpBasic(httpBasic -> httpBasic.disable()) // Tắt HTTP Basic Authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .logout(logout -> logout
                        .logoutUrl("/logout")  // Cấu hình URL logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))  // Cho phép logout bằng GET
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Dùng BCrypt để mã hóa mật khẩu
    }
}

