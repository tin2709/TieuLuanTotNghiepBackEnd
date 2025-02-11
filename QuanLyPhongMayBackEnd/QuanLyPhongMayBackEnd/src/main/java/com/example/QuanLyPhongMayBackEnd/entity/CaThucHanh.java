package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ca_thuc_hanh")
public class CaThucHanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ca")
    private Long maCa;

    @Column(name = "ngay_thuc_hanh")
    @Temporal(TemporalType.DATE)
    private Date ngayThucHanh;

    @Column(name = "ten_ca")
    private String tenCa;

    @Column(name = "tiet_bat_dau")
    private int tietBatDau;

    @Column(name = "tiet_ket_thuc")
    private int tietKetThuc;
    @Column(name = "buoi_so")
    private int buoiSo;
    public CaThucHanh(Long maCa, Date ngayThucHanh, String tenCa, int tietBatDau, int tietKetThuc, int buoiSo,
                      GiaoVien giaoVien, PhongMay phongMay, MonHoc monHoc) {
        super();
        this.maCa = maCa;
        this.ngayThucHanh = ngayThucHanh;
        this.tenCa = tenCa;
        this.tietBatDau = tietBatDau;
        this.tietKetThuc = tietKetThuc;
        this.buoiSo = buoiSo;
        this.giaoVien = giaoVien;
        this.phongMay = phongMay;
        this.monHoc = monHoc;
    }
    public int getBuoiSo() {
        return buoiSo;
    }
    public void setBuoiSo(int buoiSo) {
        this.buoiSo = buoiSo;
    }
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_giao_vien")
    private GiaoVien giaoVien;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phong")
    private PhongMay phongMay;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_mon", referencedColumnName = "ma_mon")
    private MonHoc monHoc;


    public Long getMaCa() {
        return maCa;
    }
    public void setMaCa(Long maCa) {
        this.maCa = maCa;
    }
    public Date getNgayThucHanh() {
        return ngayThucHanh;
    }
    public void setNgayThucHanh(Date ngayThucHanh) {
        this.ngayThucHanh = ngayThucHanh;
    }
    public String getTenCa() {
        return tenCa;
    }
    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }
    public int getTietBatDau() {
        return tietBatDau;
    }
    public void setTietBatDau(int tietBatDau) {
        this.tietBatDau = tietBatDau;
    }
    public int getTietKetThuc() {
        return tietKetThuc;
    }
    public void setTietKetThuc(int tietKetThuc) {
        this.tietKetThuc = tietKetThuc;
    }
    public GiaoVien getGiaoVien() {
        return giaoVien;
    }
    public void setGiaoVien(GiaoVien giaoVien) {
        this.giaoVien = giaoVien;
    }
    public PhongMay getPhongMay() {
        return phongMay;
    }
    public void setPhongMay(PhongMay phongMay) {
        this.phongMay = phongMay;
    }
    public MonHoc getMonHoc() {
        return monHoc;
    }
    public void setMonHoc(MonHoc monHoc) {
        this.monHoc = monHoc;
    }

    public CaThucHanh() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "CaThucHanh [maCa=" + maCa + ", ngayThucHanh=" + ngayThucHanh + ", tenCa=" + tenCa + ", tietBatDau="
                + tietBatDau + ", tietKetThuc=" + tietKetThuc + ", buoiSo=" + buoiSo + ", giaoVien=" + giaoVien
                + ", phongMay=" + phongMay + ", monHoc=" + monHoc + "]";
    }


}
