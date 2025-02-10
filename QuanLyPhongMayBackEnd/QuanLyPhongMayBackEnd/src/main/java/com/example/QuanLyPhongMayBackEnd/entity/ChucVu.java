package com.example.QuanLyPhongMayBackEnd.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
@Entity
@Table(name = "chuc_vu")
public class ChucVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_cv")
    private Long maCV;

    @Column(name = "ten_cv", columnDefinition = "nvarchar(50)", nullable = false)
    private String tenCV;

    public Long getMaCV() {
        return maCV;
    }

    public void setMaCV(Long maCV) {
        this.maCV = maCV;
    }

    public String getTenCV() {
        return tenCV;
    }

    public void setTenCV(String tenCV) {
        this.tenCV = tenCV;
    }

    public ChucVu() {
    }

    public ChucVu(Long maCV, String tenCV) {
        this.maCV = maCV;
        this.tenCV = tenCV;
    }

    @Override
    public String toString() {
        return "ChucVu [maCV=" + maCV + ", tenCV=" + tenCV + "]";
    }

}
