// ThietBi.java
package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "thiet_bi")
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thiet_bi")
    private Long maThietBi; // Changed name

    @Column(name = "ten_thiet_bi", columnDefinition = "nvarchar(100)") // Changed name
    private String tenThietBi;

    @Column(name = "trang_thai", columnDefinition = "nvarchar(50) DEFAULT N'Đang hoạt động' CHECK (trang_thai IN (N'Đã hỏng', N'Đang hoạt động'))")
    private String trangThai = "Đang hoạt động"; // Same as MayTinh

    @Column(name = "mo_ta", columnDefinition = "TEXT") // Same as MayTinh
    private String moTa;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_lap_dat", updatable = false) // Same as MayTinh
    private Date ngayLapDat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_cap_nhat") // Same as MayTinh
    private Date ngayCapNhat;

    // Relationship to LoaiThietBi (Many Devices belong to One Type)
    @ManyToOne(fetch = FetchType.LAZY) // Consider Lazy fetching
    @JoinColumn(name = "ma_loai", nullable = false) // Foreign key column, added not null constraint
    @JsonBackReference // To handle bidirectional relationship serialization with LoaiThietBi
    private LoaiThietBi loaiThietBi;

    // --- Getters ---
    public Long getMaThietBi() {
        return maThietBi;
    }

    public String getTenThietBi() {
        return tenThietBi;
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

    public LoaiThietBi getLoaiThietBi() {
        return loaiThietBi;
    }

    // --- Setters ---
    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
    }

    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Setter for ngayLapDat (Optional, @PrePersist handles it)
    public void setNgayLapDat(Date ngayLapDat) {
        if (this.ngayLapDat == null) {
            this.ngayLapDat = ngayLapDat;
        }
    }

    // Setter for ngayCapNhat (Optional, @PreUpdate handles it)
    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public void setLoaiThietBi(LoaiThietBi loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    // --- Constructors ---
    public ThietBi(Long maThietBi, String tenThietBi, String trangThai, String moTa, Date ngayLapDat, Date ngayCapNhat, LoaiThietBi loaiThietBi) {
        super();
        this.maThietBi = maThietBi;
        this.tenThietBi = tenThietBi;
        this.trangThai = trangThai;
        this.moTa = moTa;
        this.ngayLapDat = ngayLapDat;
        this.ngayCapNhat = ngayCapNhat;
        this.loaiThietBi = loaiThietBi;
    }

    public ThietBi() {
        super();
    }

    // --- Lifecycle Callbacks (Identical to MayTinh) ---

    @PrePersist // Called before initial save (INSERT)
    protected void onCreate() {
        if (this.ngayLapDat == null) {
            this.ngayLapDat = new Date(); // Set creation timestamp
        }
    }

    @PreUpdate // Called before an existing entity is updated (UPDATE)
    protected void onUpdate() {
        this.ngayCapNhat = new Date(); // Set update timestamp
    }

    // --- toString ---
    @Override
    public String toString() {
        return String.format("ThietBi [maThietBi=%d, tenThietBi=%s, trangThai=%s, moTa=%s, ngayLapDat=%s, ngayCapNhat=%s, loaiThietBi=%s]",
                maThietBi, tenThietBi, trangThai, moTa, ngayLapDat, ngayCapNhat, loaiThietBi != null ? loaiThietBi.getTenLoai() : "null");
    }
}