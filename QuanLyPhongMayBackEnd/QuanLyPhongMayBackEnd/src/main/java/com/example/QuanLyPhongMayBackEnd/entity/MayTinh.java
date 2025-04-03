// MayTinh.java
package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
// import org.hibernate.annotations.UpdateTimestamp; // REMOVE this import

import java.util.Date;

@Entity
@Table(name = "may_tinh")
public class MayTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_may")
    private Long maMay;

    @Column(name = "ten_may", columnDefinition = "nvarchar(100)")
    private String tenMay;

    @Column(name = "trang_thai", columnDefinition = "nvarchar(50) DEFAULT N'Đang hoạt động' CHECK (trang_thai IN (N'Đã hỏng', N'Đang hoạt động'))")
    private String trangThai = "Đang hoạt động";

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_lap_dat", updatable = false) // Correct: Set only on creation, never update
    private Date ngayLapDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_cap_nhat")
    // REMOVED: @UpdateTimestamp // Do not use this as it updates on create too
    private Date ngayCapNhat; // Will be null initially, set by @PreUpdate

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "ma_phong")
    private PhongMay phongMay;

    // --- Getters ---
    public Long getMaMay() {
        return maMay;
    }

    public String getTenMay() {
        return tenMay;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public Date getNgayLapDat() {
        return ngayLapDat;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public PhongMay getPhongMay() {
        return phongMay;
    }

    // --- Setters ---
    public void setMaMay(Long maMay) {
        this.maMay = maMay;
    }

    public void setTenMay(String tenMay) {
        this.tenMay = tenMay;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Setter for ngayLapDat (Optional, primarily for non-JPA setup/testing)
    public void setNgayLapDat(Date ngayLapDat) {
        if (this.ngayLapDat == null) {
            this.ngayLapDat = ngayLapDat;
        }
    }

    // Setter for ngayCapNhat (needed if setting manually, but @PreUpdate handles it)
    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }


    public void setPhongMay(PhongMay phongMay) {
        this.phongMay = phongMay;
    }

    // --- Constructors ---
    public MayTinh(Long maMay, String tenMay, String trangThai, String moTa, Date ngayLapDat, Date ngayCapNhat, PhongMay phongMay) {
        super();
        this.maMay = maMay;
        this.tenMay = tenMay;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.ngayLapDat = ngayLapDat; // Set via constructor if needed
        this.ngayCapNhat = ngayCapNhat; // Set via constructor if needed (will be overwritten on update)
        this.phongMay = phongMay;
    }

    public MayTinh() {
        super();
    }

    // --- Lifecycle Callbacks ---

    @PrePersist // Called before initial save (INSERT)
    protected void onCreate() {
        if (this.ngayLapDat == null) {
            this.ngayLapDat = new Date(); // Set creation timestamp
        }
        // Do NOT set ngayCapNhat here
    }

    @PreUpdate // Called before an existing entity is updated (UPDATE)
    protected void onUpdate() {
        this.ngayCapNhat = new Date(); // Set update timestamp
        // Do NOT touch ngayLapDat here (and updatable=false prevents it anyway)
    }

    @Override
    public String toString() {
        return String.format("MayTinh [maMay=%d, tenMay=%s, trangThai=%s, moTa=%s, ngayLapDat=%s, ngayCapNhat=%s, phongMay=%s]",
                maMay, tenMay, trangThai, moTa, ngayLapDat, ngayCapNhat, phongMay != null ? phongMay.getTenPhong() : "null");
    }
}