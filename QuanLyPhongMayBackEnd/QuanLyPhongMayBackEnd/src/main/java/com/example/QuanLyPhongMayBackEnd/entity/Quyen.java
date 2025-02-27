package com.example.QuanLyPhongMayBackEnd.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quyen")
public class Quyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maQuyen;

    @Column(name = "ten_quyen", columnDefinition = "nvarchar(50)" , nullable = false)
    private String tenQuyen;

    public Long getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(Long maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getTenQuyen() {
        return tenQuyen;
    }

    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    public Quyen(Long maQuyen, String tenQuyen) {
        super();
        this.maQuyen = maQuyen;
        this.tenQuyen = tenQuyen;
    }

    public Quyen() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "Quyen [maQuyen=" + maQuyen + ", tenQuyen=" + tenQuyen + "]";
    }


}
