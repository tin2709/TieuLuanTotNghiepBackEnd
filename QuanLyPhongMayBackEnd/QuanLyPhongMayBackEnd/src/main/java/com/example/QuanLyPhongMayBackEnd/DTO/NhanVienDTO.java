package com.example.QuanLyPhongMayBackEnd.DTO;

public class NhanVienDTO {

    private Long maNV;
    private String tenNV;
    private String email;
    private String sDT;
    private String tenCV; // Added tenCV field

    public NhanVienDTO(Long maNV, String tenNV, String email, String sDT, String tenCV) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.email = email;
        this.sDT = sDT;
        this.tenCV = tenCV; // Initialize tenCV
    }

    // Getters and Setters
    public Long getMaNV() {
        return maNV;
    }

    public void setMaNV(Long maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getsDT() {
        return sDT;
    }

    public void setsDT(String sDT) {
        this.sDT = sDT;
    }

    public String getTenCV() {
        return tenCV;
    }

    public void setTenCV(String tenCV) {
        this.tenCV = tenCV;
    }
}