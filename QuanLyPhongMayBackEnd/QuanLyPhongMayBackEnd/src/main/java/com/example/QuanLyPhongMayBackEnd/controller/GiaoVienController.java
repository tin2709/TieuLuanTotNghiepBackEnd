package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.Khoa;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.service.GiaoVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class GiaoVienController {

    @Autowired
    private GiaoVienService giaoVienService;

    // API lấy danh sách giáo viên
    @GetMapping("/DSGiaoVien")
    public List<GiaoVien> layDSGV(@RequestParam String token){
        return giaoVienService.layDSGV();
    }

    // API lấy giáo viên theo mã giáo viên
    @GetMapping("/GiaoVien/{maGiaoVien}")
    public GiaoVien layGVTheoMa(@PathVariable String maGiaoVien, @RequestParam String token){
        return giaoVienService.layGVTheoMa(maGiaoVien);
    }

    // API thêm mới giáo viên
    @PostMapping("/LuuGiaoVien")
    public GiaoVien luu(@RequestParam String maGiaoVien,
                        @RequestParam String hoTen,
                        @RequestParam String soDienThoai,
                        @RequestParam String email,
                        @RequestParam String hocVi,
                        @RequestParam String taiKhoanMaTK,
                        @RequestParam Long khoaMaKhoa,
                        @RequestParam String token) {

        // Tạo đối tượng TaiKhoan và Khoa từ các tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);  // Giả sử TaiKhoan có constructor nhận maTK
        Khoa khoa = new Khoa(khoaMaKhoa);  // Giả sử Khoa có constructor nhận maKhoa

        // Tạo giáo viên từ các tham số
        GiaoVien giaoVien = new GiaoVien(maGiaoVien, hoTen, soDienThoai, email, hocVi, taiKhoan, khoa);

        return giaoVienService.luu(giaoVien);
    }

    // API xóa giáo viên theo mã giáo viên
    @DeleteMapping("/XoaGiaoVien/{maGiaoVien}")
    public String xoa(@PathVariable String maGiaoVien, @RequestParam String token) {
        giaoVienService.xoa(maGiaoVien);
        return "Đã xoá giáo viên " + maGiaoVien;
    }

    // API phân trang danh sách giáo viên
    @GetMapping("/DSGiaoVien/phantang")
    public Page<GiaoVien> layDSGVPhanTrang(@RequestParam int pageNumber, @RequestParam String token) {
        return giaoVienService.layDSGVPhanTrang(pageNumber);
    }
}
