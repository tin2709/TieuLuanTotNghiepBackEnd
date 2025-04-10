package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "mon_hoc")
public class MonHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_mon")
    private Long maMon;

    @Column(name = "ten_mon")
    private String tenMon;

    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")  // New column
    private Date ngayKetThuc;  // New property

    @Column(name = "so_buoi")
    private int soBuoi;

    @OneToMany(mappedBy = "monHoc", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CaThucHanh> caThucHanhs;

    public MonHoc(Long maMon) {
    }

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

    public List<CaThucHanh> getCaThucHanhs() {
        return caThucHanhs;
    }

    public void setCaThucHanhs(List<CaThucHanh> caThucHanhs) {
        this.caThucHanhs = caThucHanhs;
    }

    public MonHoc(Long maMon, String tenMon, Date ngayBatDau, int soBuoi, Date ngayKetThuc, List<CaThucHanh> caThucHanhs) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.ngayBatDau = ngayBatDau;
        this.soBuoi = soBuoi;
        this.ngayKetThuc = ngayKetThuc;
        this.caThucHanhs = caThucHanhs;
    }

    public MonHoc() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "MonHoc{" +
                "maMon=" + maMon +
                ", tenMon='" + tenMon + '\'' +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", soBuoi=" + soBuoi +
                ", caThucHanhs=" + caThucHanhs +
                '}';
    }
}
