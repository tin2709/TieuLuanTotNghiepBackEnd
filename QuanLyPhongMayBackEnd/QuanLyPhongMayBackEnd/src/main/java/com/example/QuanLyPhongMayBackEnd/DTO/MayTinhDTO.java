package com.example.QuanLyPhongMayBackEnd.DTO; // Or your preferred DTO package

import java.util.Date;

public class MayTinhDTO {
    private Long maMay;
    private String tenMay;
    private String trangThai;
    private String moTa;
    private Date ngayLapDat;
    private Date ngayCapNhat;
    private Long maPhong;
    private String tenPhong;
    private String noiDungGhiChu; // <--- RENAMED field

    // Default Constructor
    public MayTinhDTO() {
    }

    // Updated Constructor for easy mapping (optional)
    public MayTinhDTO(Long maMay, String tenMay, String trangThai, String moTa,
                      Date ngayLapDat, Date ngayCapNhat, Long maPhong, String tenPhong,
                      String noiDungGhiChu) { // <--- RENAMED parameter
        this.maMay = maMay;
        this.tenMay = tenMay;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.ngayLapDat = ngayLapDat;
        this.ngayCapNhat = ngayCapNhat;
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.noiDungGhiChu = noiDungGhiChu; // <--- RENAMED assignment
    }

    public MayTinhDTO(Long maMay, String tenMay, String trangThai, String moTa, Date ngayLapDat, Date ngayCapNhat, Long maPhong, String tenPhong) {
    }

    // --- Getters ---
    public Long getMaMay() {
        return maMay;
    }

    public String getTenMay() {
        return tenMay;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public Date getNgayLapDat() {
        return ngayLapDat;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public Long getMaPhong() {
        return maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public String getNoiDungGhiChu() {
        return noiDungGhiChu;
    } // <--- RENAMED Getter

    // --- Setters ---
    public void setMaMay(Long maMay) {
        this.maMay = maMay;
    }

    public void setTenMay(String tenMay) {
        this.tenMay = tenMay;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setNgayLapDat(Date ngayLapDat) {
        this.ngayLapDat = ngayLapDat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public void setNoiDungGhiChu(String noiDungGhiChu) {
        this.noiDungGhiChu = noiDungGhiChu;
    }
}