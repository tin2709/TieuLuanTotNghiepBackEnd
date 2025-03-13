package com.example.QuanLyPhongMayBackEnd.DTO;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;

import java.util.List;

public class QRDTO {
    private String tenPhong;
    private int soMayDangHoatDong;
    private int soMayDaHong;
    private List<MayTinhDTO> mayDangHoatDong;  // Sử dụng MayTinhDTO
    private List<MayTinhDTO> mayDaHong;       // Sử dụng MayTinhDTO

    // Constructor (constructor  tham số, nhưng thay đổi kiểu List)
    public QRDTO() {}
    public QRDTO(String tenPhong, int soMayDangHoatDong, int soMayDaHong,
                       List<MayTinhDTO> mayDangHoatDong, List<MayTinhDTO> mayDaHong) {
        this.tenPhong = tenPhong;
        this.soMayDangHoatDong = soMayDangHoatDong;
        this.soMayDaHong = soMayDaHong;
        this.mayDangHoatDong = mayDangHoatDong;
        this.mayDaHong = mayDaHong;
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

    public List<MayTinhDTO> getMayDangHoatDong() {
        return mayDangHoatDong;
    }

    public void setMayDangHoatDong(List<MayTinhDTO> mayDangHoatDong) {
        this.mayDangHoatDong = mayDangHoatDong;
    }

    public List<MayTinhDTO> getMayDaHong() {
        return mayDaHong;
    }

    public void setMayDaHong(List<MayTinhDTO> mayDaHong) {
        this.mayDaHong = mayDaHong;
    }
}