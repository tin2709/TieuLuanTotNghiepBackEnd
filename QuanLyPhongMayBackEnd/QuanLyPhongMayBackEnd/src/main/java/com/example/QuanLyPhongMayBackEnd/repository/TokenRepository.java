package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository   extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    void deleteByTaiKhoan(TaiKhoan taiKhoan);

    //    void deleteByUser(User user);
    void deleteByExpiresAtBefore(LocalDateTime now);// Phương thức xóa token đã hết hạn
    void deleteByToken(String token);
}
