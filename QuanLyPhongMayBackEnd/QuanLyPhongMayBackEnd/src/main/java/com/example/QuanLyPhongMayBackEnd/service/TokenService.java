package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    // Lưu token cho người dùng
    @Transactional
    public Token saveToken(String token, TaiKhoan taiKhoan) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(1); // Token sẽ hết hạn sau 1 giờ

        Token newToken = new Token(token, now, expiresAt, taiKhoan);
        return tokenRepository.save(newToken);
    }




    // Xóa các token đã hết hạn
    @Scheduled(fixedRate = 3600000) // 1 giờ = 3600000 milliseconds
    @Transactional
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiresAtBefore(now);
    }
}
