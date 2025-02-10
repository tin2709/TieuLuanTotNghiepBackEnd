package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "giao_vien")
public class GiaoVien {

    @Id
    @Column(name = "ma_gv")
    private String maGiaoVien;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "email")
    private String email;

    @Column(name = "hoc_vi")
    private String hocVi;

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_gv", referencedColumnName = "ma_tk")
    private TaiKhoan taiKhoan;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "maKhoa")
    private Khoa khoa;

    public GiaoVien(String maGiaoVien, String hoTen, String soDienThoai, String email, String hocVi, TaiKhoan taiKhoan,
                    Khoa khoa) {
        super();
        this.maGiaoVien = maGiaoVien;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.hocVi = hocVi;
        this.taiKhoan = taiKhoan;
        this.khoa = khoa;
    }

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }


    public String getMaGiaoVien() {
        return maGiaoVien;
    }

    public void setMaGiaoVien(String maGiaoVien) {
        this.maGiaoVien = maGiaoVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }


    public GiaoVien() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "GiaoVien [maGiaoVien=" + maGiaoVien + ", hoTen=" + hoTen + ", soDienThoai=" + soDienThoai + ", email="
                + email + ", hocVi=" + hocVi + ", taiKhoan=" + taiKhoan + ", khoa=" + khoa + "]";
    }
}
