package com.example.QuanLyPhongMayBackEnd.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "may_tinh")
public class MayTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_may")
    private Long maMay;

    @Column(name = "trang_thai",columnDefinition = "nvarchar(50) DEFAULT N'Đang hoạt động ' CHECK (trang_thai IN (N'Đã hỏng', N'Đang hoạt động'))")
    private String trangThai= "Đang hoạt động";

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_lap_dat")
    private Date ngayLapDat;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "ma_phong")

    private PhongMay phongMay;

    public Long getMaMay() {
        return maMay;
    }

    public void setMaMay(Long maMay) {
        this.maMay = maMay;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Date getNgayLapDat() {
        return ngayLapDat;
    }

    public void setNgayLapDat(Date ngayLapDat) {
        this.ngayLapDat = ngayLapDat;
    }

    public PhongMay getPhongMay() {
        return phongMay;
    }

    public void setPhongMay(PhongMay phongMay) {
        this.phongMay = phongMay;
    }


    public MayTinh(Long maMay, String trangThai, String moTa, Date ngayLapDat, PhongMay phongMay) {
        super();
        this.maMay = maMay;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.ngayLapDat = ngayLapDat;
        this.phongMay = phongMay;
    }

    public MayTinh() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return String.format("MayTinh [maMay=%d, trangThai=%s, moTa=%s, ngayLapDat=%s, phongMay=%s]",
                maMay, trangThai, moTa, ngayLapDat, phongMay != null ? phongMay.getTenPhong() : "null");
    }
}
