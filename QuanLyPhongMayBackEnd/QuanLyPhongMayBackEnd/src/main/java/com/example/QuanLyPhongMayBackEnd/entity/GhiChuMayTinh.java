package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;
import java.util.Date; // Import java.util.Date

@Entity
@Table(name = "ghi_chu_may_tinh")
public class GhiChuMayTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ghichuMT")
    private Long maGhiChuMT;

    @Column(name = "noi_dung", columnDefinition = "nvarchar(2500)")
    private String noiDung;

    // Relationship to the specific computer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_may") // Foreign key to may_tinh table
    private MayTinh mayTinh;

    // *** New Relationship to the room the computer belongs to ***
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_phong", referencedColumnName = "ma_phong") // Foreign key to phong_may table
    private PhongMay phongMay;
    // ************************************************************

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_bao_loi")
    private Date ngayBaoLoi;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_sua")
    private Date ngaySua;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "matk_bao_loi", referencedColumnName = "ma_tk") // Foreign key to tai_khoan table
    private TaiKhoan taiKhoanBaoLoi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "matk_sua_loi", referencedColumnName = "ma_tk") // Foreign key to tai_khoan table
    private TaiKhoan taiKhoanSuaLoi;

    // Default constructor
    public GhiChuMayTinh() {
        super();
    }

    // Updated Constructor with PhongMay
    public GhiChuMayTinh(Long maGhiChuMT, String noiDung, MayTinh mayTinh, PhongMay phongMay,
                         Date ngayBaoLoi, Date ngaySua, TaiKhoan taiKhoanBaoLoi, TaiKhoan taiKhoanSuaLoi) {
        super();
        this.maGhiChuMT = maGhiChuMT;
        this.noiDung = noiDung;
        this.mayTinh = mayTinh;
        this.phongMay = phongMay; // Assign phongMay
        this.ngayBaoLoi = ngayBaoLoi;
        this.ngaySua = ngaySua;
        this.taiKhoanBaoLoi = taiKhoanBaoLoi;
        this.taiKhoanSuaLoi = taiKhoanSuaLoi;
    }

    // Getters and Setters

    public Long getMaGhiChuMT() {
        return maGhiChuMT;
    }

    public void setMaGhiChuMT(Long maGhiChuMT) {
        this.maGhiChuMT = maGhiChuMT;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public MayTinh getMayTinh() {
        return mayTinh;
    }

    public void setMayTinh(MayTinh mayTinh) {
        this.mayTinh = mayTinh;
    }

    // Getter and Setter for PhongMay
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
        return "GhiChuMayTinh [maGhiChuMT=" + maGhiChuMT
                + ", noiDung=" + noiDung
                + ", mayTinh=" + (mayTinh != null ? mayTinh.getMaMay() : "null")
                + ", phongMay=" + (phongMay != null ? phongMay.getMaPhong() : "null") // Added phongMay
                + ", ngayBaoLoi=" + ngayBaoLoi
                + ", ngaySua=" + ngaySua
                + ", taiKhoanBaoLoi=" + (taiKhoanBaoLoi != null ? taiKhoanBaoLoi.getMaTK() : "null")
                + ", taiKhoanSuaLoi=" + (taiKhoanSuaLoi != null ? taiKhoanSuaLoi.getMaTK() : "null")
                + "]";
    }
}