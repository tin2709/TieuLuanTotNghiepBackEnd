package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    // Phương thức này sẽ được gọi mỗi giờ để xóa các token đã hết hạn
    @Scheduled(fixedRate = 3600000) // 1 giờ = 3600000 milliseconds
    @Transactional
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiresAtBefore(now);
    }
}
