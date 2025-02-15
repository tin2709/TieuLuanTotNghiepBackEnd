package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth")
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp; // Mã OTP

    @Column(nullable = false)
    private Boolean isVerified = false; // Trạng thái xác minh (true nếu OTP đã được sử dụng)

    @Column(nullable = false)
    private LocalDateTime expiryTime; // Thời gian hết hạn của OTP

    @Column(nullable = false)
    private String purpose; // Mục đích sử dụng OTP (FORGOT_PASSWORD, CHANGE_PASSWORD, etc.)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_tk", nullable = false)
    private TaiKhoan taiKhoan; // Liên kết với người dùng

    public Auth() {
    }

    public Auth(String otp, LocalDateTime expiryTime, String purpose, TaiKhoan taiKhoan) {
        this.otp = otp;
        this.expiryTime = expiryTime;
        this.purpose = purpose;
        this.taiKhoan = taiKhoan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan T) {
        this.taiKhoan = taiKhoan;
    }
}

