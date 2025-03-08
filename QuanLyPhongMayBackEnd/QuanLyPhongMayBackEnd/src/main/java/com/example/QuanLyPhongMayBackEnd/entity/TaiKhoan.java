package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tai_khoan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tk")
    private Long maTK;

    @Column(name = "ten_dang_nhap", columnDefinition = "nvarchar(255) UNIQUE")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải có ít nhất 3 ký tự")
    private String tenDangNhap;

    @Column(name = "mat_khau")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_quyen")
    private Quyen quyen;

    @Column(name = "email", unique = true)
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Column(name = "image")
    private String image;

    // New field for banning status
    @Column(name = "is_banned", nullable = false)
    private boolean isBanned = false;  // Default value set to false (not banned)

    // Constructor for initializing all fields
    public TaiKhoan(Long maTK, String tenDangNhap, String matKhau, Quyen quyen, String email, String image, boolean isBanned) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyen = quyen;
        this.email = email;
        this.image = image;
        this.isBanned = isBanned;
    }

    // Constructor with only maTK
    public TaiKhoan(Long taiKhoanMaTK) {
        this.maTK = taiKhoanMaTK;
    }

    // Getter and Setter methods
    public Long getMaTK() { return maTK; }
    public void setMaTK(Long maTK) { this.maTK = maTK; }

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

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    // Default constructor
    public TaiKhoan() {
        super();
    }

    @Override
    public String toString() {
        return "TaiKhoan [maTK=" + maTK + ", tenDangNhap=" + tenDangNhap + ", matKhau=" + matKhau + ", quyen=" + quyen + ", email=" + email + ", image=" + image + ", isBanned=" + isBanned + "]";
    }
}
