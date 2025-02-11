package com.example.QuanLyPhongMayBackEnd.entity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "lich_truc")
public class LichTruc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lich")
    private Long maLich;

    @Column(name = "ngay_truc")
    private Date ngayTruc;

    @Column(name = "thoi_gian_bat_dau")
    private String thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private String thoiGianKetThuc;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nv")
    private NhanVien nhanVien;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_tang")
    private Tang tang;

    public Tang getTang() {
        return tang;
    }

    public void setTang(Tang tang) {
        this.tang = tang;
    }

    public Long getMaLich() {
        return maLich;
    }

    public void setMaLich(Long maLich) {
        this.maLich = maLich;
    }

    public Date getNgayTruc() {
        return ngayTruc;
    }

    public void setNgayTruc(Date ngayTruc) {
        this.ngayTruc = ngayTruc;
    }

    public String getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(String thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public String getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(String thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LichTruc(Long maLich, Date ngayTruc, String thoiGianBatDau, String thoiGianKetThuc, NhanVien nhanVien,
                    Tang tang) {
        super();
        this.maLich = maLich;
        this.ngayTruc = ngayTruc;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.nhanVien = nhanVien;
        this.tang = tang;
    }

    public LichTruc() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "LichTruc [maLich=" + maLich + ", ngayTruc=" + ngayTruc + ", thoiGianBatDau=" + thoiGianBatDau
                + ", thoiGianKetThuc=" + thoiGianKetThuc + ", nhanVien=" + nhanVien + ", tang=" + tang + "]";
    }



}
