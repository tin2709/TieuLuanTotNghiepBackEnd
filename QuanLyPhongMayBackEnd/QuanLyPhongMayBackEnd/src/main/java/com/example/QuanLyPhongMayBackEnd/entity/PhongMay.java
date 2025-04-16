package com.example.QuanLyPhongMayBackEnd.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name="phong_may",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"tenPhong", "maTang"})}) // Sửa đổi UniqueConstraint ở đây
public class PhongMay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phong")
    private Long maPhong;
    @Column(name="ten_phong", nullable = false)
    private String tenPhong;
    @Column(name = "so_may",columnDefinition = "int CHECK (so_may >= 0)", nullable = false)
    private int soMay;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai",  columnDefinition = "nvarchar(50) DEFAULT N'Trống' CHECK (trang_thai IN (N'Trống', N'Đang có tiết', N'Không thể dùng'))")
    private String trangThai= "Trống";

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_tang", nullable = false)
    private Tang tang;

    public PhongMay(Long maPhong) {
    }

    public List<MayTinh> getMayTinhs() {
        return mayTinhs;
    }

    public void setMayTinhs(List<MayTinh> mayTinhs) {
        this.mayTinhs = mayTinhs;
    }

    @OneToMany(mappedBy = "phongMay", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<MayTinh> mayTinhs;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "int DEFAULT 0")
    private Integer version = 0;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public Long getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Long maPhong) {
        this.maPhong = maPhong;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public int getSoMay() {
        return soMay;
    }

    public void setSoMay(int soMay) {
        this.soMay = soMay;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Tang getTang() {
        return tang;
    }

    public void setTang(Tang tang) {
        this.tang = tang;
    }

    public PhongMay(Long maPhong, String tenPhong, int soMay, String moTa, String trangThai, Tang tang,
                    List<MayTinh> mayTinhs, Integer version) {
        super();
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.soMay = soMay;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.tang = tang;
        this.mayTinhs = mayTinhs;
        this.version = version;
    }

    public PhongMay() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "PhongMay [maPhong=" + maPhong + ", tenPhong=" + tenPhong + ", soMay=" + soMay + ", Mo ta=" + moTa
                + ", trangThai=" + trangThai + ", tang=" + tang + ", mayTinhs=" + mayTinhs + ", version=" + version +"]";
    }

}