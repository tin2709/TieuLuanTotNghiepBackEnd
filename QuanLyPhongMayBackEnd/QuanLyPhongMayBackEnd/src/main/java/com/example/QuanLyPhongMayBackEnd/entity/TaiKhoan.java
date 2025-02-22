package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Column(name = "email", unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(name = "image")
    private String image;

    public TaiKhoan(String taiKhoanMaTK) {
    }

    // Getter and Setter methods
    public String getMaTK() { return maTK; }
    public void setMaTK(String maTK) { this.maTK = maTK; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public Quyen getQuyen() { return quyen; }
    public void setQuyen(Quyen quyen) { this.quyen = quyen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    // Constructor
    public TaiKhoan(String maTK, String tenDangNhap, String matKhau, Quyen quyen, String email, String image) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyen = quyen;
        this.email = email;
        this.image = image;
    }

    public TaiKhoan() {
        super();
    }

    @Override
    public String toString() {
        return "TaiKhoan [maTK=" + maTK + ", tenDangNhap=" + tenDangNhap + ", matKhau=" + matKhau + ", quyen=" + quyen + ", email=" + email + ", image=" + image + "]";
    }
}


