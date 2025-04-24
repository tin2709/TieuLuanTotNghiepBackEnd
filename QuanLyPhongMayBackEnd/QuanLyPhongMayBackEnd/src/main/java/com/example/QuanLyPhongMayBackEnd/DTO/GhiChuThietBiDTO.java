package com.example.QuanLyPhongMayBackEnd.DTO; // Or your preferred DTO package

import java.util.Date;

public class GhiChuThietBiDTO {
    private Long maGhiChuTB; // Changed name
    private String noiDung;
    private Long maThietBi;     // ID of the related ThietBi (Changed from maMay)
    private String tenThietBi;   // Name of the related ThietBi (Changed from tenMay)
    private Long maLoai;        // ID of the related LoaiThietBi (Added)
    private String tenLoai;      // Name of the related LoaiThietBi (Added)
    private Long maPhong;       // ID of the related PhongMay
    private String tenPhong;     // Name of the related PhongMay
    private Date ngayBaoLoi;
    private Date ngaySua;
    private Long maTaiKhoanBaoLoi; // ID of the reporting TaiKhoan
    private String tenTaiKhoanBaoLoi; // Username of the reporting TaiKhoan
    private Long maTaiKhoanSuaLoi; // ID of the fixing TaiKhoan (can be null)
    private String tenTaiKhoanSuaLoi; // Username of the fixing TaiKhoan (can be null)

    // Default Constructor
    public GhiChuThietBiDTO() {
    }

    // Full Constructor (Example)
    public GhiChuThietBiDTO(Long maGhiChuTB, String noiDung, Long maThietBi, String tenThietBi, Long maLoai, String tenLoai, Long maPhong, String tenPhong, Date ngayBaoLoi, Date ngaySua, Long maTaiKhoanBaoLoi, String tenTaiKhoanBaoLoi, Long maTaiKhoanSuaLoi, String tenTaiKhoanSuaLoi) {
        this.maGhiChuTB = maGhiChuTB;
        this.noiDung = noiDung;
        this.maThietBi = maThietBi;
        this.tenThietBi = tenThietBi;
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.ngayBaoLoi = ngayBaoLoi;
        this.ngaySua = ngaySua;
        this.maTaiKhoanBaoLoi = maTaiKhoanBaoLoi;
        this.tenTaiKhoanBaoLoi = tenTaiKhoanBaoLoi;
        this.maTaiKhoanSuaLoi = maTaiKhoanSuaLoi;
        this.tenTaiKhoanSuaLoi = tenTaiKhoanSuaLoi;
    }

    // --- Getters ---

    public Long getMaGhiChuTB() {
        return maGhiChuTB;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public Long getMaThietBi() {
        return maThietBi;
    }

    public String getTenThietBi() {
        return tenThietBi;
    }

    public Long getMaLoai() {
        return maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public Long getMaPhong() {
        return maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public Date getNgayBaoLoi() {
        return ngayBaoLoi;
    }

    public Date getNgaySua() {
        return ngaySua;
    }

    public Long getMaTaiKhoanBaoLoi() {
        return maTaiKhoanBaoLoi;
    }

    public String getTenTaiKhoanBaoLoi() {
        return tenTaiKhoanBaoLoi;
    }

    public Long getMaTaiKhoanSuaLoi() {
        return maTaiKhoanSuaLoi;
    }

    public String getTenTaiKhoanSuaLoi() {
        return tenTaiKhoanSuaLoi;
    }

    // --- Setters ---

    public void setMaGhiChuTB(Long maGhiChuTB) {
        this.maGhiChuTB = maGhiChuTB;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
    }

    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

    public void setMaLoai(Long maLoai) {
        this.maLoai = maLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public void setNgayBaoLoi(Date ngayBaoLoi) {
        this.ngayBaoLoi = ngayBaoLoi;
    }

    public void setNgaySua(Date ngaySua) {
        this.ngaySua = ngaySua;
    }

    public void setMaTaiKhoanBaoLoi(Long maTaiKhoanBaoLoi) {
        this.maTaiKhoanBaoLoi = maTaiKhoanBaoLoi;
    }

    public void setTenTaiKhoanBaoLoi(String tenTaiKhoanBaoLoi) {
        this.tenTaiKhoanBaoLoi = tenTaiKhoanBaoLoi;
    }

    public void setMaTaiKhoanSuaLoi(Long maTaiKhoanSuaLoi) {
        this.maTaiKhoanSuaLoi = maTaiKhoanSuaLoi;
    }

    public void setTenTaiKhoanSuaLoi(String tenTaiKhoanSuaLoi) {
        this.tenTaiKhoanSuaLoi = tenTaiKhoanSuaLoi;
    }

    // Optional: toString() method for debugging
    @Override
    public String toString() {
        return "GhiChuThietBiDTO{" +
                "maGhiChuTB=" + maGhiChuTB +
                ", noiDung='" + noiDung + '\'' +
                ", maThietBi=" + maThietBi +
                ", tenThietBi='" + tenThietBi + '\'' +
                ", maLoai=" + maLoai +
                ", tenLoai='" + tenLoai + '\'' +
                ", maPhong=" + maPhong +
                ", tenPhong='" + tenPhong + '\'' +
                ", ngayBaoLoi=" + ngayBaoLoi +
                ", ngaySua=" + ngaySua +
                ", maTaiKhoanBaoLoi=" + maTaiKhoanBaoLoi +
                ", tenTaiKhoanBaoLoi='" + tenTaiKhoanBaoLoi + '\'' +
                ", maTaiKhoanSuaLoi=" + maTaiKhoanSuaLoi +
                ", tenTaiKhoanSuaLoi='" + tenTaiKhoanSuaLoi + '\'' +
                '}';
    }
}