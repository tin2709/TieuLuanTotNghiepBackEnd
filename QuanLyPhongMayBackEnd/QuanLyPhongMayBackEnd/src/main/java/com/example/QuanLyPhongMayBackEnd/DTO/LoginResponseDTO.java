package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Objects;
import java.util.Date; // Import Date

public class LoginResponseDTO {

    private String token; // Access Token
    private String refreshToken;
    private Long maTK;
    private String username;
    private String email;
    private Long maQuyen;
    private String avatarUrl;
    private Long expiresAtTimestamp; // *** THÊM TRƯỜNG MỚI (Unix timestamp in milliseconds) ***

    public LoginResponseDTO() {
    }

    // *** CẬP NHẬT CONSTRUCTOR ĐỂ NHẬN TIMESTAMP ***
    public LoginResponseDTO(String token, String refreshToken, Long maTK, String username, String email, Long maQuyen, String avatarUrl, Long expiresAtTimestamp) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.maTK = maTK;
        this.username = username;
        this.email = email;
        this.maQuyen = maQuyen;
        this.avatarUrl = avatarUrl;
        this.expiresAtTimestamp = expiresAtTimestamp; // Gán giá trị
    }

    // Getters
    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public Long getMaTK() { return maTK; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getMaQuyen() { return maQuyen; }
    public String getAvatarUrl() { return avatarUrl; }
    public Long getExpiresAtTimestamp() { return expiresAtTimestamp; } // *** THÊM GETTER ***

    // Setters (Giữ nguyên các setter khác)
    public void setToken(String token) { this.token = token; } // Đổi tên tham số cho rõ ràng
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setMaTK(Long maTK) { this.maTK = maTK; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setMaQuyen(Long maQuyen) { this.maQuyen = maQuyen; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setExpiresAtTimestamp(Long expiresAtTimestamp) { this.expiresAtTimestamp = expiresAtTimestamp; } // *** THÊM SETTER ***


    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "token='[PROTECTED]'" +
                ", refreshToken='[PROTECTED]'" +
                ", maTK=" + maTK +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", maQuyen=" + maQuyen +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", expiresAtTimestamp=" + expiresAtTimestamp + // Thêm vào toString
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponseDTO that = (LoginResponseDTO) o;
        return Objects.equals(token, that.token) && Objects.equals(refreshToken, that.refreshToken) && Objects.equals(maTK, that.maTK) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(maQuyen, that.maQuyen) && Objects.equals(avatarUrl, that.avatarUrl) && Objects.equals(expiresAtTimestamp, that.expiresAtTimestamp); // Thêm vào equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, refreshToken, maTK, username, email, maQuyen, avatarUrl, expiresAtTimestamp); // Thêm vào hashCode
    }
}