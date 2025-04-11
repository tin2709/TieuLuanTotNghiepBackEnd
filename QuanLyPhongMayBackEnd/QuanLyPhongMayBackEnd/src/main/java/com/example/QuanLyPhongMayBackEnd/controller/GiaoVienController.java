package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.GiaoVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.Khoa;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.service.GiaoVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
public class GiaoVienController {

    @Autowired
    private GiaoVienService giaoVienService;

    // API lấy danh sách giáo viên
    @GetMapping("/DSGiaoVien")
    public List<GiaoVien> layDSGV(@RequestParam String token) {
        List<GiaoVien> giaoVienList = giaoVienService.layDSGV(token);
        if (giaoVienList == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }
        return giaoVienList;
    }

    // API lấy giáo viên theo mã giáo viên
    @GetMapping("/GiaoVien/{maGiaoVien}")
    public GiaoVien layGVTheoMa(@PathVariable String maGiaoVien, @RequestParam String token) {
        GiaoVien giaoVien = giaoVienService.layGVTheoMa(maGiaoVien, token);
        if (giaoVien == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc giáo viên không tồn tại");
        }
        return giaoVien;
    }

    // API thêm mới giáo viên
    @PostMapping("/LuuGiaoVien")
    public GiaoVien luu(
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String email,
            @RequestParam String hocVi,
            @RequestParam Long taiKhoanMaTK,
            @RequestParam Long khoaMaKhoa
           ) {



        // Tạo đối tượng TaiKhoan và Khoa từ các tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);  // Giả sử TaiKhoan có constructor nhận maTK
        Khoa khoa = new Khoa(khoaMaKhoa);  // Giả sử Khoa có constructor nhận maKhoa

        // Tạo giáo viên từ các tham số
        GiaoVien giaoVien = new GiaoVien();
        giaoVien.setHoTen(hoTen);
        giaoVien.setSoDienThoai(soDienThoai);
        giaoVien.setEmail(email);
        giaoVien.setHocVi(hocVi);
        giaoVien.setKhoa(khoa);
        giaoVien.setTaiKhoan(taiKhoan);



        return giaoVienService.luu(giaoVien);
    }


    // API xóa giáo viên theo mã giáo viên
    @DeleteMapping("/XoaGiaoVien/{maGiaoVien}")
    public String xoa(@PathVariable String maGiaoVien, @RequestParam String token) {
        if (!giaoVienService.isUserLoggedIn(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }
        giaoVienService.xoa(Long.valueOf(maGiaoVien), token);
        return "Đã xoá giáo viên " + maGiaoVien;
    }

    // API phân trang danh sách giáo viên
    @GetMapping("/DSGiaoVien/phantang")
    public Page<GiaoVien> layDSGVPhanTrang(@RequestParam int pageNumber, @RequestParam String token) {
        Page<GiaoVien> giaoVienPage = giaoVienService.layDSGVPhanTrang(pageNumber, token);
        if (giaoVienPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc không có dữ liệu");
        }
        return giaoVienPage;
    }
    @GetMapping("/searchGiaoVien")
    public ResponseEntity<Map<String, Object>> searchGiaoVien(@RequestParam String keyword, @RequestParam String token) {
        if (!giaoVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<GiaoVienDTO> results = giaoVienService.timKiemGiaoVien(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
