package com.example.QuanLyPhongMayBackEnd.DTO; // Or your preferred DTO package

import java.util.Date;

public class GhiChuMayTinhDTO {
    private Long maGhiChuMT;
    private String noiDung;
    private Long maMay;         // ID of the related MayTinh
    private String tenMay;       // Name of the related MayTinh (optional, if needed)
    private Long maPhong;       // ID of the related PhongMay
    private String tenPhong;     // Name of the related PhongMay (optional, if needed)
    private Date ngayBaoLoi;
    private Date ngaySua;
    private Long maTaiKhoanBaoLoi; // ID of the reporting TaiKhoan
    private String tenTaiKhoanBaoLoi; // Username of the reporting TaiKhoan (optional, if needed)
    private Long maTaiKhoanSuaLoi; // ID of the fixing TaiKhoan (can be null)
    private String tenTaiKhoanSuaLoi; // Username of the fixing TaiKhoan (can be null, optional, if needed)

    // Default Constructor
    public GhiChuMayTinhDTO() {
    }

    // Full Constructor (Example)
    public GhiChuMayTinhDTO(Long maGhiChuMT, String noiDung, Long maMay, String tenMay, Long maPhong, String tenPhong, Date ngayBaoLoi, Date ngaySua, Long maTaiKhoanBaoLoi, String tenTaiKhoanBaoLoi, Long maTaiKhoanSuaLoi, String tenTaiKhoanSuaLoi) {
        this.maGhiChuMT = maGhiChuMT;
        this.noiDung = noiDung;
        this.maMay = maMay;
        this.tenMay = tenMay;
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

    public Long getMaGhiChuMT() {
        return maGhiChuMT;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public Long getMaMay() {
        return maMay;
    }

    public String getTenMay() {
        return tenMay;
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

    public void setMaGhiChuMT(Long maGhiChuMT) {
        this.maGhiChuMT = maGhiChuMT;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public void setMaMay(Long maMay) {
        this.maMay = maMay;
    }

    public void setTenMay(String tenMay) {
        this.tenMay = tenMay;
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
        return "GhiChuMayTinhDTO{" +
                "maGhiChuMT=" + maGhiChuMT +
                ", noiDung='" + noiDung + '\'' +
                ", maMay=" + maMay +
                ", tenMay='" + tenMay + '\'' +
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