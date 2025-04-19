package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.NhanVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.ChucVu;
import com.example.QuanLyPhongMayBackEnd.entity.NhanVien;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    // API phân trang lấy danh sách nhân viên
    @GetMapping("/DSNhanVienPhanTrang")
    public Page<NhanVien> layDSNVPhanTrang(@RequestParam int pageNumber, @RequestParam String token) {
        return nhanVienService.layDSNVPhanTrang(pageNumber,token);
    }

    // API lấy danh sách nhân viên
    @GetMapping("/DSNhanVien")
    public List<NhanVien> layDSNV(@RequestParam String token) {
        return nhanVienService.layDSNV(token);
    }

    // API lấy nhân viên theo mã
    @GetMapping("/NhanVien/{maNV}")
    public NhanVien layNVTheoMa(@PathVariable String maNV, @RequestParam String token) {
        return nhanVienService.layNVTheoMa(maNV,token);
    }

    // API lưu nhân viên
    @PostMapping("/LuuNhanVien")
    public NhanVien luu(
                        @RequestParam String tenNV,
                        @RequestParam String email,
                        @RequestParam String sDT,
                        @RequestParam Long maCV,
                        @RequestParam Long taiKhoanMaTK) {
        // Tạo đối tượng NhanVien từ các tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);  // Giả sử TaiKhoan có constructor nhận maTK
        ChucVu chucVu = new ChucVu(maCV);
        NhanVien nhanVien = new NhanVien();
        nhanVien.setTenNV(tenNV);
        nhanVien.setEmail(email);
        nhanVien.setsDT(sDT);
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVien.setChucVu(chucVu);



        return nhanVienService.luu(nhanVien);
    }

    // API xóa nhân viên theo mã nhân viên
    @DeleteMapping("/XoaNhanVien/{maNV}")
    public String xoa(@PathVariable String maNV, @RequestParam String token) {
        nhanVienService.xoa(maNV,token);
        return "Đã xoá nhân viên " + maNV;
    }
    @GetMapping("/searchNhanVien")
    public ResponseEntity<Map<String, Object>> searchNhanVien(@RequestParam String keyword, @RequestParam String token) {
        if (!nhanVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<NhanVienDTO> results = nhanVienService.timKiemNhanVien(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/searchNhanVienByAdmin")
    public ResponseEntity<Map<String, Object>> searchNhanVienByAdmin(@RequestParam String keyword, @RequestParam String token) {
        // Assuming nhanVienService.isUserLoggedIn(token) exists, or adjust authentication logic as needed
        if (!nhanVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<NhanVienDTO> results = nhanVienService.timKiemNhanVienByAdmin(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
