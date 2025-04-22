package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;
import java.util.Date; // Import java.util.Date

@Entity
@Table(name = "ghi_chu_phong_may")
public class GhiChuPhongMay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ghichuPM")
    private Long maGhiChuPM;

    @Column(name = "noi_dung", columnDefinition = "nvarchar(2500)")
    private String noiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong")
    private PhongMay phongMay;

    @Temporal(TemporalType.TIMESTAMP) // Specify TIMESTAMP mapping for java.util.Date
    @Column(name = "ngay_bao_loi")
    private Date ngayBaoLoi; // Changed back to Date

    @Temporal(TemporalType.TIMESTAMP) // Specify TIMESTAMP mapping for java.util.Date
    @Column(name = "ngay_sua")
    private Date ngaySua; // Changed back to Date

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matk_bao_loi", referencedColumnName = "ma_tk")
    private TaiKhoan taiKhoanBaoLoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matk_sua_loi", referencedColumnName = "ma_tk")
    private TaiKhoan taiKhoanSuaLoi;

    // Default constructor
    public GhiChuPhongMay() {
        super();
    }

    // Constructor with fields (using Date)
    public GhiChuPhongMay(Long maGhiChuPM, String noiDung, PhongMay phongMay, Date ngayBaoLoi, Date ngaySua, TaiKhoan taiKhoanBaoLoi, TaiKhoan taiKhoanSuaLoi) {
        this.maGhiChuPM = maGhiChuPM;
        this.noiDung = noiDung;
        this.phongMay = phongMay;
        this.ngayBaoLoi = ngayBaoLoi;
        this.ngaySua = ngaySua;
        this.taiKhoanBaoLoi = taiKhoanBaoLoi;
        this.taiKhoanSuaLoi = taiKhoanSuaLoi;
    }

    // Getters and Setters (using Date)

    public Long getMaGhiChuPM() {
        return maGhiChuPM;
    }

    public void setMaGhiChuPM(Long maGhiChuPM) {
        this.maGhiChuPM = maGhiChuPM;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public PhongMay getPhongMay() {
        return phongMay;
    }

    public void setPhongMay(PhongMay phongMay) {
        this.phongMay = phongMay;
    }

    public Date getNgayBaoLoi() { // Return type is Date
        return ngayBaoLoi;
    }

    public void setNgayBaoLoi(Date ngayBaoLoi) { // Parameter type is Date
        this.ngayBaoLoi = ngayBaoLoi;
    }

    public Date getNgaySua() { // Return type is Date
        return ngaySua;
    }

    public void setNgaySua(Date ngaySua) { // Parameter type is Date
        this.ngaySua = ngaySua;
    }

    public TaiKhoan getTaiKhoanBaoLoi() {
        return taiKhoanBaoLoi;
    }

    public void setTaiKhoanBaoLoi(TaiKhoan taiKhoanBaoLoi) {
        this.taiKhoanBaoLoi = taiKhoanBaoLoi;
    }

    public TaiKhoan getTaiKhoanSuaLoi() {
        return taiKhoanSuaLoi;
    }

    public void setTaiKhoanSuaLoi(TaiKhoan taiKhoanSuaLoi) {
        this.taiKhoanSuaLoi = taiKhoanSuaLoi;
    }

    @Override
    public String toString() {
        // Using default Date.toString() format
        return "GhiChuPhongMay [maGhiChuPM=" + maGhiChuPM + ", noiDung=" + noiDung
                + ", phongMay=" + (phongMay != null ? phongMay.getMaPhong() : "null")
                + ", ngayBaoLoi=" + ngayBaoLoi + ", ngaySua=" + ngaySua
                + ", taiKhoanBaoLoi=" + (taiKhoanBaoLoi != null ? taiKhoanBaoLoi.getMaTK() : "null")
                + ", taiKhoanSuaLoi=" + (taiKhoanSuaLoi != null ? taiKhoanSuaLoi.getMaTK() : "null")
                + "]";
    }
}