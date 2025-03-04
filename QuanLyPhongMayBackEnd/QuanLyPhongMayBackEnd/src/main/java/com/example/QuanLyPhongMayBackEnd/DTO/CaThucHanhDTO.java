package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Date;

public class CaThucHanhDTO {
    private Long maCa;
    private Date ngayThucHanh;
    private String tenCa;
    private Integer tietBatDau;
    private Integer tietKetThuc;
    private Integer buoiSo;

    // Constructors, Getters and Setters
    public CaThucHanhDTO(Long maCa, Date ngayThucHanh, String tenCa, Integer tietBatDau, Integer tietKetThuc, Integer buoiSo) {
        this.maCa = maCa;
        this.ngayThucHanh = ngayThucHanh;
        this.tenCa = tenCa;
        this.tietBatDau = tietBatDau;
        this.tietKetThuc = tietKetThuc;
        this.buoiSo = buoiSo;
    }

    public Long getMaCa() {
        return maCa;
    }

    public Date getNgayThucHanh() {
        return ngayThucHanh;
    }

    public String getTenCa() {
        return tenCa;
    }

    public Integer getTietBatDau() {
        return tietBatDau;
    }

    public Integer getTietKetThuc() {
        return tietKetThuc;
    }

    public Integer getBuoiSo() {
        return buoiSo;
    }
}

