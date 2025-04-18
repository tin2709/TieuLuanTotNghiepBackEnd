// QRDTO.java
package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.List;

public class QRDTO {
    private String tenPhong;
    private int soMayDangHoatDong;
    private int soMayDaHong;
    private List<String> loaiThietBiList; // Danh sách tên loại thiết bị
    private int soThietBiDangHoatDong; // Số lượng thiết bị đang hoạt động
    private int soThietBiDaHong; // Số lượng thiết bị đã hỏng


    // Constructor
    public QRDTO() {}

    public QRDTO(String tenPhong, int soMayDangHoatDong, int soMayDaHong, List<String> loaiThietBiList, int soThietBiDangHoatDong, int soThietBiDaHong) {
        this.tenPhong = tenPhong;
        this.soMayDangHoatDong = soMayDangHoatDong;
        this.soMayDaHong = soMayDaHong;
        this.loaiThietBiList = loaiThietBiList;
        this.soThietBiDangHoatDong = soThietBiDangHoatDong;
        this.soThietBiDaHong = soThietBiDaHong;
    }

    // Getters and Setters
    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

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

    public List<String> getLoaiThietBiList() {
        return loaiThietBiList;
    }

    public void setLoaiThietBiList(List<String> loaiThietBiList) {
        this.loaiThietBiList = loaiThietBiList;
    }

    public int getSoThietBiDangHoatDong() {
        return soThietBiDangHoatDong;
    }

    public void setSoThietBiDangHoatDong(int soThietBiDangHoatDong) {
        this.soThietBiDangHoatDong = soThietBiDangHoatDong;
    }

    public int getSoThietBiDaHong() {
        return soThietBiDaHong;
    }

    public void setSoThietBiDaHong(int soThietBiDaHong) {
        this.soThietBiDaHong = soThietBiDaHong;
    }
}