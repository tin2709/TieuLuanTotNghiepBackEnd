package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tai_khoan")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "maTK")
public class TaiKhoan {

    @Id
    @Column(name = "ma_tk")
    private String maTK;

    @Column(name = "ten_dang_nhap", columnDefinition = "nvarchar(255) UNIQUE")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải có ít nhất 3 ký tự")
    private String tenDangNhap;

    @Column(name = "mat_khau")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ma_quyen")
    private Quyen quyen;

    // Getter và Setter
    public String getMaTK() { return maTK; }
    public void setMaTK(String maTK) { this.maTK = maTK; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public Quyen getQuyen() { return quyen; }
    public void setQuyen(Quyen quyen) { this.quyen = quyen; }

    // Constructor
    public TaiKhoan(String maTK, String tenDangNhap, String matKhau, Quyen quyen) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyen = quyen;
    }

    public TaiKhoan() {
        super();
    }

    @Override
    public String toString() {
        return "TaiKhoan [maTK=" + maTK + ", tenDangNhap=" + tenDangNhap + ", matKhau=" + matKhau + ", quyen=" + quyen + "]";
    }
}
