package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.NhanVien;
import com.example.QuanLyPhongMayBackEnd.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    // API phân trang lấy danh sách nhân viên
    @GetMapping("/DSNhanVienPhanTrang")
    public Page<NhanVien> layDSNVPhanTrang(@RequestParam int pageNumber, @RequestParam String token) {
        return nhanVienService.layDSNVPhanTrang(pageNumber);
    }

    // API lấy danh sách nhân viên
    @GetMapping("/DSNhanVien")
    public List<NhanVien> layDSNV(@RequestParam String token) {
        return nhanVienService.layDSNV();
    }

    // API lấy nhân viên theo mã
    @GetMapping("/NhanVien/{maNV}")
    public NhanVien layNVTheoMa(@PathVariable String maNV, @RequestParam String token) {
        return nhanVienService.layNVTheoMa(maNV);
    }

    // API lưu nhân viên
    @PostMapping("/LuuNhanVien")
    public NhanVien luu(@RequestParam String maNV,
                        @RequestParam String tenNV,
                        @RequestParam String email,
                        @RequestParam String sDT,
                        @RequestParam String maCV,
                        @RequestParam String token) {
        // Tạo đối tượng NhanVien từ các tham số
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNV(maNV);
        nhanVien.setTenNV(tenNV);
        nhanVien.setEmail(email);
        nhanVien.setsDT(sDT);

        // Bạn sẽ cần xử lý để lấy thông tin ChucVu từ maCV nếu cần
        // ChucVu chucVu = chucVuService.layChucVuTheoMa(maCV);
        // nhanVien.setChucVu(chucVu);

        return nhanVienService.luu(nhanVien);
    }

    // API xóa nhân viên theo mã nhân viên
    @DeleteMapping("/XoaNhanVien/{maNV}")
    public String xoa(@PathVariable String maNV, @RequestParam String token) {
        nhanVienService.xoa(maNV);
        return "Đã xoá nhân viên " + maNV;
    }
}
