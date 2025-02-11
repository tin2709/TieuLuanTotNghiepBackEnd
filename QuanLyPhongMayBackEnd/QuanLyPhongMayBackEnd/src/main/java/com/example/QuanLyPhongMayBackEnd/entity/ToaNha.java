package com.example.QuanLyPhongMayBackEnd.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "toa_nha")
public class ToaNha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_toanha")
    private Long maToaNha;

    @Column(name = "ten_toanha", columnDefinition = "nvarchar(50)", nullable = false)
    private String tenToaNha;

    public Long getMaToaNha() {
        return maToaNha;
    }

    public void setMaToaNha(Long maToaNha) {
        this.maToaNha = maToaNha;
    }

    public String getTenToaNha() {
        return tenToaNha;
    }

    public void setTenToaNha(String tenToaNha) {
        this.tenToaNha = tenToaNha;
    }


    public ToaNha(Long maToaNha, String tenToaNha ) {
        super();
        this.maToaNha = maToaNha;
        this.tenToaNha = tenToaNha;

    }

    public ToaNha() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return String.format("ToaNha [maToaNha=%d, tenToaNha=%s]", maToaNha, tenToaNha);
    }

}
