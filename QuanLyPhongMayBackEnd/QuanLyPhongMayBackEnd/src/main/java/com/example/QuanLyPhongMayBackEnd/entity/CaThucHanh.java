package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "ca_thuc_hanh")
public class CaThucHanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maCa;

    @Column(name = "ngay_thuc_hanh")
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Ngày thực hành không được để trống")
    private Date ngayThucHanh;

    @Column(name = "ten_ca")
    @NotBlank(message = "Tên ca không được để trống")
    @Size(max = 50, message = "Tên ca không được vượt quá 50 ký tự")
    private String tenCa;

    @Column(name = "tiet_bat_dau")
    @Min(value = 1, message = "Tiết bắt đầu phải lớn hơn hoặc bằng 1")
    @Max(value = 12, message = "Tiết bắt đầu phải nhỏ hơn hoặc bằng 12")
    private int tietBatDau;

    @Column(name = "tiet_ket_thuc")
    @Min(value = 1, message = "Tiết kết thúc phải lớn hơn hoặc bằng 1")
    @Max(value = 12, message = "Tiết kết thúc phải nhỏ hơn hoặc bằng 12")
    private int tietKetThuc;

    @Column(name = "buoi_so")
    @Min(value = 1, message = "Buổi số phải lớn hơn hoặc bằng 1")
    @Max(value = 15, message = "Buổi số phải nhỏ hơn hoặc bằng 15")
    private int buoiSo;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_giao_vien")
    @NotNull(message = "Giáo viên không được để trống")
    private GiaoVien giaoVien;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phong")
    @NotNull(message = "Phòng máy không được để trống")
    private PhongMay phongMay;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_mon", referencedColumnName = "ma_mon")
    @NotNull(message = "Môn học không được để trống")
    private MonHoc monHoc;
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
