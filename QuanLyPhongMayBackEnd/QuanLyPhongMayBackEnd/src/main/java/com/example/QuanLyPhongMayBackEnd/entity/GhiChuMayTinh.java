package com.example.QuanLyPhongMayBackEnd.entity;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ghi_chu_may_tinh")
public class GhiChuMayTinh {
    public GhiChuMayTinh() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_ghichuMT")
    private Long maGhiChuMT;

    @Column(name = "noi_dung", columnDefinition = "nvarchar(2500)")
    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "ma_may")
    private MayTinh mayTinh;

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

    public GhiChuMayTinh(Long maGhiChuMT, String noiDung, MayTinh mayTinh, Date ngayBaoLoi, Date ngaySua,
                         String maTKBaoLoi, String nguoiSuaLoi) {
        super();
        this.maGhiChuMT = maGhiChuMT;
        this.noiDung = noiDung;
        this.mayTinh = mayTinh;
        this.ngayBaoLoi = ngayBaoLoi;
        this.ngaySua = ngaySua;
        this.maTKBaoLoi = maTKBaoLoi;
        this.nguoiSuaLoi = nguoiSuaLoi;

    }

    @Override
    public String toString() {
        return "GhiChuMayTinh [maGhiChuMT=" + maGhiChuMT + ", noiDung=" + noiDung + ", mayTinh=" + mayTinh
                + ", ngayBaoLoi=" + ngayBaoLoi + ", ngaySua=" + ngaySua + ", maTKBaoLoi=" + maTKBaoLoi
                + ", nguoiSuaLoi=" + nguoiSuaLoi +  "]";
    }



}
