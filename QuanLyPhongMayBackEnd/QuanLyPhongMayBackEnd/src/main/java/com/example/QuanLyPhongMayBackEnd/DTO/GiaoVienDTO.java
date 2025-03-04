package com.example.QuanLyPhongMayBackEnd.DTO;

public class GiaoVienDTO {

    private Long maGiaoVien;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String hocVi;

    public GiaoVienDTO(Long maGiaoVien, String hoTen, String soDienThoai, String email, String hocVi) {
        this.maGiaoVien = maGiaoVien;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.hocVi = hocVi;
    }

    // Getters and Setters
    public Long getMaGiaoVien() {
        return maGiaoVien;
    }

    public void setMaGiaoVien(Long maGiaoVien) {
        this.maGiaoVien = maGiaoVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }
}

