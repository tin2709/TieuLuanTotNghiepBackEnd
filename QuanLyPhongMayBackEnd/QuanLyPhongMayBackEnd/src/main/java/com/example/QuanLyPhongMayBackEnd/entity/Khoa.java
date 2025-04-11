package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "khoa")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "maKhoa")

public class Khoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khoa")
    private Long maKhoa;

    @Column(name = "ten_khoa")
    private String tenKhoa;

    public Khoa(Long khoaMaKhoa) {
        this.maKhoa = khoaMaKhoa;
    }


    public Long getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(Long maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public Khoa(Long maKhoa, String tenKhoa) {
        super();
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public Khoa() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return String.format("Khoa [maKhoa=%d, tenKhoa=%s]", maKhoa, tenKhoa);
    }

}
