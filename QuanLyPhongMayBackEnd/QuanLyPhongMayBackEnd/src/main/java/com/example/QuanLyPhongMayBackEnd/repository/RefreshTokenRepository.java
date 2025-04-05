package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.RefreshToken;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByTaiKhoan(TaiKhoan taiKhoan);
    @Modifying
    int deleteByExpiryDateBefore(Instant now); // Để xóa token hết hạn định kỳ
}