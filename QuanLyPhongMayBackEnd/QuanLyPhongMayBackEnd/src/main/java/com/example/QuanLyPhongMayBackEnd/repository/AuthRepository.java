package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.Auth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByTaiKhoanEmailAndOtpAndPurpose(String email, String otp, String purpose);

    Optional<Auth> findByTaiKhoanEmailAndPurpose(String email, String purpose);

    Optional<Auth> findByTaiKhoanEmailAndPurposeAndIsVerified(String email, String purpose, boolean isVerified);

    @Transactional
    @Modifying
    @Query("DELETE FROM Auth a WHERE a.isVerified = false AND a.expiryTime < :expiryTime")
    int deleteExpiredOTPs(LocalDateTime expiryTime);
}
