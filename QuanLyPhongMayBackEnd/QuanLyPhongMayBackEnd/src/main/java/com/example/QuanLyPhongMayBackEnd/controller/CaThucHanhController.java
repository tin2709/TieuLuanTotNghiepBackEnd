package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.CaThucHanhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.CaThucHanhService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
public class CaThucHanhController {

    @Autowired
    private CaThucHanhService caThucHanhService;

    // API lưu CaThucHanh
    @PostMapping("/LuuCaThucHanh")
    public ResponseEntity<CaThucHanh> luu(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String tenCa,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam int buoiSo,
            @RequestParam Long maGiaoVien,
            @RequestParam Long maPhong,
            @RequestParam Long maMon,
            @RequestParam String token) {

        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        CaThucHanh caThucHanh = caThucHanhService.luu(new CaThucHanh(null, ngayThucHanh, tenCa, tietBatDau, tietKetThuc, buoiSo,
                new GiaoVien(maGiaoVien), new PhongMay(maPhong), new MonHoc(maMon)), token);

        return new ResponseEntity<>(caThucHanh, HttpStatus.CREATED);
    }

    // API lấy danh sách CaThucHanh
    @GetMapping("/DSCaThucHanh")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanh(@RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanh(token);
        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API lấy danh sách CaThucHanh theo ngày
    @GetMapping("/DSCaThucHanhTheoNgay/{ngayThucHanh}")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoNgay(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String token) {

        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoNgay(ngayThucHanh, token);
        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API lấy CaThucHanh theo mã
    @GetMapping("/CaThucHanh/{maCaThucHanh}")
    public ResponseEntity<CaThucHanh> layCaThucHanhTheoMa(@PathVariable Long maCaThucHanh, @RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        CaThucHanh caThucHanh = caThucHanhService.layCaThucHanhTheoMa(maCaThucHanh, token);
        return new ResponseEntity<>(caThucHanh, HttpStatus.OK);
    }

    // API lấy danh sách CaThucHanh theo môn học
    @GetMapping("/DSCaThucHanhTheoMonHoc/{maMon}")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoMonHoc(@PathVariable Long maMon, @RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMonHoc(maMon, token);

        if (dsCaThucHanh.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API cập nhật CaThucHanh theo mã
    @PutMapping("/CapNhatCaThucHanh/{maCaThucHanh}")
    public ResponseEntity<CaThucHanh> capNhat(
            @PathVariable Long maCaThucHanh,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String tenCa,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam int buoiSo,
            @RequestParam Long maGiaoVien,
            @RequestParam Long maPhong,
            @RequestParam Long maMon,
            @RequestParam String token) {

        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        CaThucHanh existingCaThucHanh = caThucHanhService.layCaThucHanhTheoMa(maCaThucHanh, token);
        if (existingCaThucHanh != null) {
            existingCaThucHanh.setNgayThucHanh(ngayThucHanh);
            existingCaThucHanh.setTenCa(tenCa);
            existingCaThucHanh.setTietBatDau(tietBatDau);
            existingCaThucHanh.setTietKetThuc(tietKetThuc);
            existingCaThucHanh.setBuoiSo(buoiSo);
            existingCaThucHanh.setGiaoVien(new GiaoVien(maGiaoVien));
            existingCaThucHanh.setPhongMay(new PhongMay(maPhong));
            existingCaThucHanh.setMonHoc(new MonHoc(maMon));

            return new ResponseEntity<>(caThucHanhService.capNhat(existingCaThucHanh, token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // API xóa CaThucHanh
    @DeleteMapping("/XoaCaThucHanh/{maCaThucHanh}")
    public ResponseEntity<String> xoa(@PathVariable Long maCaThucHanh, @RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        caThucHanhService.xoa(maCaThucHanh, token);
        return new ResponseEntity<>("Đã xoá ca thực hành với mã " + maCaThucHanh, HttpStatus.OK);
    }
    @GetMapping("/searchCaThucHanh")
    public ResponseEntity<Map<String, Object>> timKiemCaThucHanh(@RequestParam String keyword, @RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Kiểm tra nếu keyword hợp lệ
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);  // Trả về lỗi nếu keyword trống
        }

        // Tìm kiếm và lấy kết quả
        List<CaThucHanhDTO> dsCaThucHanh = caThucHanhService.timKiemCaThucHanh(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", dsCaThucHanh);
        response.put("size", dsCaThucHanh.size());  // Thêm kích thước vào kết quả

        if (dsCaThucHanh == null || dsCaThucHanh.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}