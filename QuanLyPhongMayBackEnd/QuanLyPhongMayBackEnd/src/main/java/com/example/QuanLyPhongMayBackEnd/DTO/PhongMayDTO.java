package com.example.QuanLyPhongMayBackEnd.DTO;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;

import java.util.List;

public class PhongMayDTO {

    private Long maPhong;
    private String tenPhong;
    private int soMay;
    private String moTa;
    private String trangThai;
    private int soMayDangHoatDong;
    private int soMayDaHong;
    private List<MayTinh> mayDangHoatDong;
    private List<MayTinh> mayDaHong;

    public PhongMayDTO() {}
    public PhongMayDTO(Long maPhong, String tenPhong, int soMay, String moTa, String trangThai) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.soMay = soMay;
        this.moTa = moTa;
        this.trangThai = trangThai;

    }

    // Constructor mới với 9 tham số
    public PhongMayDTO(Long maPhong, String tenPhong, int soMay, String moTa, String trangThai,
                       int soMayDangHoatDong, int soMayDaHong, List<MayTinh> mayDangHoatDong, List<MayTinh> mayDaHong) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.soMay = soMay;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.soMayDangHoatDong = soMayDangHoatDong;
        this.soMayDaHong = soMayDaHong;
        this.mayDangHoatDong = mayDangHoatDong;
        this.mayDaHong = mayDaHong;
    }

    // Getters and Setters (bao gồm cả các trường mới)
    // ... (các getter/setter cho các trường đã có) ...

    public int getSoMayDangHoatDong() {
        return soMayDangHoatDong;
    }

    public void setSoMayDangHoatDong(int soMayDangHoatDong) {
        this.soMayDangHoatDong = soMayDangHoatDong;
    }

    public int getSoMayDaHong() {
        return soMayDaHong;
    }

    public void setSoMayDaHong(int soMayDaHong) {
        this.soMayDaHong = soMayDaHong;
    }

    public List<MayTinh> getMayDangHoatDong() {
        return mayDangHoatDong;
    }

    public void setMayDangHoatDong(List<MayTinh> mayDangHoatDong) {
        this.mayDangHoatDong = mayDangHoatDong;
    }

    public List<MayTinh> getMayDaHong() {
        return mayDaHong;
    }

    public void setMayDaHong(List<MayTinh> mayDaHong) {
        this.mayDaHong = mayDaHong;
    }
}