package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Date;

public class MonHocDTO {

    private Long maMon;
    private String tenMon;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private int soBuoi;

    public MonHocDTO(Long maMon, String tenMon, Date ngayBatDau, Date ngayKetThuc, int soBuoi) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.soBuoi = soBuoi;
    }

    // Getters and Setters
    public Long getMaMon() {
        return maMon;
    }

    public void setMaMon(Long maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public int getSoBuoi() {
        return soBuoi;
    }

    public void setSoBuoi(int soBuoi) {
        this.soBuoi = soBuoi;
    }
}

