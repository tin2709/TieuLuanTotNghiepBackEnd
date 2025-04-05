package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Hoặc @OneToOne tùy thuộc mối quan hệ của bạn
    @JoinColumn(
            name = "ma_tk", // Tên cột khóa ngoại trong bảng này (ví dụ: refresh_token hoặc token)
            referencedColumnName = "ma_tk" // *** SỬA Ở ĐÂY: Phải khớp với @Column(name) của khóa chính trong TaiKhoan ***
    )
    private TaiKhoan taiKhoan;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    // Constructors, Getters, Setters (Như đã tạo trước)
    public RefreshToken() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }
}