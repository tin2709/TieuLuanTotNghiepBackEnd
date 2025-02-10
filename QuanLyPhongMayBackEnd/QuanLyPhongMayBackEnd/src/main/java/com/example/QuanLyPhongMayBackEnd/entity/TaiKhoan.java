package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "tai_khoan")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "maTK")
public class TaiKhoan {

    @Id
    @Column(name = "ma_tk")
    private String maTK;

    @Column(name = "ten_dang_nhap", columnDefinition = "nvarchar(255) UNIQUE")
    private String tenDangNhap;

    @Column(name = "mat_khau")
    private String matKhau;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ma_quyen")
    private Quyen quyen;

    public String getMaTK() {
        return maTK;
    }

    public void setMaTK(String maTK) {
        this.maTK = maTK;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public Quyen getQuyen() {
        return quyen;
    }

    public void setQuyen(Quyen quyen) {
        this.quyen = quyen;
    }

    public TaiKhoan(String maTK, String tenDangNhap, String matKhau, Quyen quyen) {
        super();
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyen = quyen;
    }

    public TaiKhoan() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "TaiKhoan [maTK=" + maTK + ", tenDangNhap=" + tenDangNhap + ", matKhau=" + matKhau + ", quyen=" + quyen
                + "]";
    }



}
