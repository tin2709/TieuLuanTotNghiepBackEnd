package com.example.QuanLyPhongMayBackEnd.DTO;

public class PhongMayDTO {

    private Long maPhong;
    private String tenPhong;
    private int soMay;
    private String moTa;
    private String trangThai;

    public PhongMayDTO(Long maPhong, String tenPhong, int soMay, String moTa, String trangThai) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.soMay = soMay;
        this.moTa = moTa;
        this.trangThai = trangThai;

    }

    // Getters and Setters

    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public int getSoMay() {
        return soMay;
    }

    public void setSoMay(int soMay) {
        this.soMay = soMay;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }


}

