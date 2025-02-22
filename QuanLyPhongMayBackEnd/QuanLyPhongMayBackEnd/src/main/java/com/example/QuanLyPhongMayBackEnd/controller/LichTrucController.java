package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.service.LichTrucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class LichTrucController {

    @Autowired
    private LichTrucService lichTrucService;

    // Thêm lịch trực
    @PostMapping("/LuuLichTruc")
    public LichTruc luu(@RequestParam Long maLich,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTruc,
                        @RequestParam String thoiGianBatDau,
                        @RequestParam String thoiGianKetThuc,
                        @RequestParam Long maNhanVien,
                        @RequestParam Long maTang,
                        @RequestParam String token) {
        // Tạo đối tượng LichTruc từ tham số truyền vào
        LichTruc lichTruc = new LichTruc(maLich, ngayTruc, thoiGianBatDau, thoiGianKetThuc, null, null);
        return lichTrucService.luu(lichTruc);
    }

    // Lấy danh sách lịch trực
    @GetMapping("/DSLichTruc")
    public List<LichTruc> layDSLT(@RequestParam String token) {
        return lichTrucService.layDSLT();
    }

    // Lấy lịch trực theo mã lịch
    @GetMapping("/LichTruc/{maLich}")
    public LichTruc layLTTheoMa(@PathVariable Long maLich, @RequestParam String token) {
        return lichTrucService.layLTTheoMa(maLich);
    }

    // Lấy lịch trực theo mã tầng
    @GetMapping("/LichTrucTheoTang/{maTang}")
    public List<LichTruc> layLichTrucTheoTang(@PathVariable Long maTang, @RequestParam String token) {
        return lichTrucService.layLichTrucTheoMaTang(maTang);
    }

    // Xóa lịch trực
    @DeleteMapping("/XoaLichTruc/{maLich}")
    public String xoa(@PathVariable Long maLich, @RequestParam String token) {
        lichTrucService.xoa(maLich);
        return "Đã xoá lịch trực " + maLich;
    }

    // Lấy các tầng chưa có nhân viên trực trong tháng
    @GetMapping("/TangChuaCoNhanVienTrucTrongThang")
    public List<Tang> layTangChuaCoNhanVienTrucTrongThang(@RequestParam String token) {
        return lichTrucService.layTangChuaCoNhanVienTrucTrongThang();
    }

    // Cập nhật lịch trực
    @PostMapping("/CapNhatLichTruc")
    public LichTruc capNhatLichTruc(@RequestParam Long maLich,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTruc,
                                    @RequestParam String thoiGianBatDau,
                                    @RequestParam String thoiGianKetThuc,
                                    @RequestParam Long maNhanVien,
                                    @RequestParam Long maTang,
                                    @RequestParam String token) {
        // Tạo đối tượng LichTruc từ tham số truyền vào và cập nhật
        LichTruc lichTruc = new LichTruc(maLich, ngayTruc, thoiGianBatDau, thoiGianKetThuc, null, null);
        return lichTrucService.updateLichTruc(lichTruc);
    }
}
