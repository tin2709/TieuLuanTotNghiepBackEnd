package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Objects;

public class LoginResponseDTO {

    private String token;
    private String refreshToken;
    private Long maTK;
    private String username;
    private String email;
    private Long maQuyen;       // *** THAY ĐỔI: Đổi từ Integer sang Long ***
    private String avatarUrl;

    public LoginResponseDTO() {
    }

    // *** THAY ĐỔI: Cập nhật kiểu dữ liệu trong constructor ***
    public LoginResponseDTO(String token, String refreshToken, Long maTK, String username, String email, Long maQuyen, String avatarUrl) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.maTK = maTK;
        this.username = username;
        this.email = email;
        this.maQuyen = maQuyen; // Giờ đây nhận Long
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public Long getMaTK() { return maTK; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getMaQuyen() { return maQuyen; } // *** THAY ĐỔI: Trả về Long ***
    public String getAvatarUrl() { return avatarUrl; }

    // Setters
    public void setAccessToken(String accessToken) { this.token = token; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setMaTK(Long maTK) { this.maTK = maTK; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setMaQuyen(Long maQuyen) { this.maQuyen = maQuyen; } // *** THAY ĐỔI: Nhận Long ***
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    // toString, equals, hashCode (cập nhật nếu cần)
    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken='[PROTECTED]', refreshToken='[PROTECTED]', maTK=" + maTK +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", maQuyen=" + maQuyen + // Kiểu Long
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponseDTO that = (LoginResponseDTO) o;
        return Objects.equals(token, that.token) && Objects.equals(refreshToken, that.refreshToken) && Objects.equals(maTK, that.maTK) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(maQuyen, that.maQuyen) && Objects.equals(avatarUrl, that.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, refreshToken, maTK, username, email, maQuyen, avatarUrl);
    }
}