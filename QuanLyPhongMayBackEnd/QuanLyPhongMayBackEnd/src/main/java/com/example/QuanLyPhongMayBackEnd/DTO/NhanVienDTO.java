package com.example.QuanLyPhongMayBackEnd.DTO;

public class NhanVienDTO {

    private Long maNV;
    private String tenNV;
    private String email;
    private String sDT;

    public NhanVienDTO(Long maNV, String tenNV, String email, String sDT) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.email = email;
        this.sDT = sDT;
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
}

