package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.MonHocDTO;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.service.MonHocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    // API lưu môn học mới
    @PostMapping("/LuuMonHoc")
    public MonHoc luu(@RequestParam String tenMon,
                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
                      @RequestParam int soBuoi,
                      @RequestParam String token) {
        // Tạo đối tượng MonHoc từ các tham số
        MonHoc monHoc = new MonHoc();
        monHoc.setTenMon(tenMon);
        monHoc.setNgayBatDau(ngayBatDau);
        monHoc.setNgayKetThuc(ngayKetThuc);
        monHoc.setSoBuoi(soBuoi);

        // Lưu môn học
        return monHocService.luu(monHoc, token);
    }

    // API lấy danh sách môn học
    @GetMapping("/DSMonHoc")
    public List<MonHoc> layDSMonHoc(@RequestParam String token) {
        return monHocService.layDSMonHoc(token);
    }

    // API xóa môn học theo mã môn
    @DeleteMapping("/XoaMonHoc/{maMon}")
    public String xoa(@PathVariable Long maMon, @RequestParam String token) {
        monHocService.xoa(maMon,token);
        return "Đã xoá môn học với mã " + maMon;
    }

    // API lấy môn học theo mã môn
    @GetMapping("/MonHoc/{maMon}")
    public MonHoc layMonHocTheoMa(@PathVariable Long maMon, @RequestParam String token) {
        return monHocService.layMonHocTheoMa(maMon,token);
    }
    @GetMapping("/searchMonHoc")
    public ResponseEntity<Map<String, Object>> searchMonHoc(@RequestParam String keyword, @RequestParam String token) {
        if (!monHocService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<MonHocDTO> results = monHocService.timKiemMonHoc(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
