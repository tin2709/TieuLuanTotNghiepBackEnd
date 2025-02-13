package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @JsonIgnore // Bỏ qua trong JSON response
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tk", nullable = false) // Liên kết với tài khoản theo ma_tk
    private TaiKhoan taiKhoan;

    // Constructors
    public Token() {
    }

    public Token(String token, LocalDateTime createdAt, LocalDateTime expiresAt, TaiKhoan taiKhoan) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.taiKhoan = taiKhoan;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", taiKhoan=" + (taiKhoan != null ? taiKhoan.getMaTK() : null) +
                '}';
    }
}
