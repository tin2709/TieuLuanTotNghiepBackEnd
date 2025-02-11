package com.example.QuanLyPhongMayBackEnd.entity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ghi_chu_phong_may")
public class GhiChuPhongMay {
    public GhiChuPhongMay() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ghichu")
    private Long maGhiChu;

    @Column(name = "noi_dung", columnDefinition = "nvarchar(2500)")
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "ma_phong")
    private PhongMay phongMay;

    @Column(name = "ngay_bao_loi")
    @Temporal(TemporalType.DATE)
    private Date ngayBaoLoi;

    @Column(name = "ngay_sua")
    @Temporal(TemporalType.DATE)
    private Date ngaySua;

    @Column(name = "matk_bao_loi")
    private String maTKBaoLoi;

    @Column(name = "matk_sua_loi")
    private String nguoiSuaLoi;

    public Long getMaGhiChu() {
        return maGhiChu;
    }

    public String getMaTKBaoLoi() {
        return maTKBaoLoi;
    }

    public void setMaTKBaoLoi(String maTKBaoLoi) {
        this.maTKBaoLoi = maTKBaoLoi;
    }

    public String getNguoiSuaLoi() {
        return nguoiSuaLoi;
    }

    public void setNguoiSuaLoi(String nguoiSuaLoi) {
        this.nguoiSuaLoi = nguoiSuaLoi;
    }

    public void setMaGhiChu(Long maGhiChu) {
        this.maGhiChu = maGhiChu;
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

    @Override
    public String toString() {
        return "GhiChuPhongMay [maGhiChu=" + maGhiChu + ", noiDung=" + noiDung + ", phongMay=" + phongMay
                + ", ngayBaoLoi=" + ngayBaoLoi + ", ngaySua=" + ngaySua + ", maTKBaoLoi=" + maTKBaoLoi
                + ", nguoiSuaLoi=" + nguoiSuaLoi + "]";
    }

    public GhiChuPhongMay(Long maGhiChu, String noiDung, PhongMay phongMay, Date ngayBaoLoi, Date ngaySua,
                          String maTKBaoLoi, String nguoiSuaLoi) {
        super();
        this.maGhiChu = maGhiChu;
        this.noiDung = noiDung;
        this.phongMay = phongMay;
        this.ngayBaoLoi = ngayBaoLoi;
        this.ngaySua = ngaySua;
        this.maTKBaoLoi = maTKBaoLoi;
        this.nguoiSuaLoi = nguoiSuaLoi;

    }

}
