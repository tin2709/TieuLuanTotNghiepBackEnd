package com.example.QuanLyPhongMayBackEnd.DTO;

public class MayTinhDTO {
    private Long maMay;

    private String tenMay; // Add this line
    private String trangThai;
    private String moTa; // Bạn có thể bỏ bớt trường nếu muốn


    public MayTinhDTO(Long maMay,String tenMay, String trangThai, String moTa) {
        this.maMay = maMay;
        this.tenMay = tenMay; // Add this line
        this.trangThai = trangThai;
        this.moTa = moTa;
    }

    // Getters and Setters
    public Long getMaMay() {
        return maMay;
    }

    public void setMaMay(Long maMay) {
        this.maMay = maMay;
    }
    public String getTenMay() {  // Add this getter
        return tenMay;
    }

    public void setTenMay(String tenMay) {  // Add this setter
        this.tenMay = tenMay;
    }
    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
