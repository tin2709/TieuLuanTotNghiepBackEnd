// LoaiThietBi.java
package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Use if you want devices listed under type
import jakarta.persistence.*;

import java.util.List; // Import if using @OneToMany

@Entity
@Table(name = "loai_thiet_bi")
public class LoaiThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loai")
    private Long maLoai;

    @Column(name = "ten_loai", columnDefinition = "nvarchar(100)", nullable = false, unique = true) // Added constraints
    private String tenLoai;

    // Optional: If you want to easily access all devices of this type
    // Be mindful of performance with large datasets if not using LAZY fetching
    @OneToMany(mappedBy = "loaiThietBi", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference // To handle bidirectional relationship serialization with ThietBi
    private List<ThietBi> thietBis;

    // --- Getters ---
    public Long getMaLoai() {
        return maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public List<ThietBi> getThietBis() {
        return thietBis;
    }

    // --- Setters ---
    public void setMaLoai(Long maLoai) {
        this.maLoai = maLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public void setThietBis(List<ThietBi> thietBis) {
        this.thietBis = thietBis;
    }

    // --- Constructors ---
    public LoaiThietBi(Long maLoai, String tenLoai) {
        super();
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }

    public LoaiThietBi() {
        super();
    }

    // --- toString ---
    @Override
    public String toString() {
        return String.format("LoaiThietBi [maLoai=%d, tenLoai=%s]", maLoai, tenLoai);
    }
}