package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    void deleteByTaiKhoan(TaiKhoan taiKhoan); // Xóa token theo tài khoản

    void deleteByExpiresAtBefore(LocalDateTime now); // Xóa các token đã hết hạn

    void deleteByToken(String token); // Xóa token theo giá trị token
}
