package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;
import java.util.Date; // Import java.util.Date

@Entity
@Table(name = "ghi_chu_thiet_bi") // Table name for device notes
public class GhiChuThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ghichuTB") // Primary key column name
    private Long maGhiChuTB;

    @Column(name = "noi_dung", columnDefinition = "nvarchar(2500)")
    private String noiDung;

    // Relationship to the specific device
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi") // Foreign key to thiet_bi table
    private ThietBi thietBi; // Changed from MayTinh to ThietBi

    // Relationship to the device type (as requested)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_loai") // Foreign key to loai_thiet_bi table (assuming this table exists)
    private LoaiThietBi loaiThietBi; // Added relationship to LoaiThietBi

    // Relationship to the room the device belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phong", referencedColumnName = "ma_phong") // Foreign key to phong_may table
    private PhongMay phongMay; // Kept relationship to PhongMay

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_bao_loi")
    private Date ngayBaoLoi;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_sua")
    private Date ngaySua;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matk_bao_loi", referencedColumnName = "ma_tk") // Foreign key to tai_khoan table
    private TaiKhoan taiKhoanBaoLoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matk_sua_loi", referencedColumnName = "ma_tk") // Foreign key to tai_khoan table
    private TaiKhoan taiKhoanSuaLoi;

    // Default constructor
    public GhiChuThietBi() {
        super();
    }

    // Updated Constructor with ThietBi, LoaiThietBi, and PhongMay
    public GhiChuThietBi(Long maGhiChuTB, String noiDung, ThietBi thietBi, LoaiThietBi loaiThietBi, PhongMay phongMay,
                         Date ngayBaoLoi, Date ngaySua, TaiKhoan taiKhoanBaoLoi, TaiKhoan taiKhoanSuaLoi) {
        super();
        this.maGhiChuTB = maGhiChuTB;
        this.noiDung = noiDung;
        this.thietBi = thietBi;
        this.loaiThietBi = loaiThietBi; // Assign loaiThietBi
        this.phongMay = phongMay;
        this.ngayBaoLoi = ngayBaoLoi;
        this.ngaySua = ngaySua;
        this.taiKhoanBaoLoi = taiKhoanBaoLoi;
        this.taiKhoanSuaLoi = taiKhoanSuaLoi;
    }

    // Getters and Setters

    public Long getMaGhiChuTB() {
        return maGhiChuTB;
    }

    public void setMaGhiChuTB(Long maGhiChuTB) {
        this.maGhiChuTB = maGhiChuTB;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public LoaiThietBi getLoaiThietBi() {
        return loaiThietBi;
    }

    public void setLoaiThietBi(LoaiThietBi loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    public PhongMay getPhongMay() {
        return phongMay;
    }

    public void setPhongMay(PhongMay phongMay) {
        this.phongMay = phongMay;
    }

    public Date getNgayBaoLoi() {
        return ngayBaoLoi;
    }

    public void setNgayBaoLoi(Date ngayBaoLoi) {
        this.ngayBaoLoi = ngayBaoLoi;
    }

    public Date getNgaySua() {
        return ngaySua;
    }

    public void setNgaySua(Date ngaySua) {
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
        return "GhiChuThietBi ["
                + "maGhiChuTB=" + maGhiChuTB
                + ", noiDung=" + noiDung
                + ", thietBi=" + (thietBi != null ? thietBi.getMaThietBi() : "null") // Changed from mayTinh
                + ", loaiThietBi=" + (loaiThietBi != null ? loaiThietBi.getMaLoai() : "null") // Added LoaiThietBi
                + ", phongMay=" + (phongMay != null ? phongMay.getMaPhong() : "null")
                + ", ngayBaoLoi=" + ngayBaoLoi
                + ", ngaySua=" + ngaySua
                + ", taiKhoanBaoLoi=" + (taiKhoanBaoLoi != null ? taiKhoanBaoLoi.getMaTK() : "null")
                + ", taiKhoanSuaLoi=" + (taiKhoanSuaLoi != null ? taiKhoanSuaLoi.getMaTK() : "null")
                + "]";
    }
}