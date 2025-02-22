package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.ChucVu;
import com.example.QuanLyPhongMayBackEnd.service.ChucVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ChucVuController {

    @Autowired
    private ChucVuService chucVuService;

    // API lưu ChucVu
    @PostMapping("/LuuChucVu")
    public ChucVu luu(
            @RequestParam String tenCV,
            @RequestParam String token) {
        // Tạo đối tượng ChucVu từ tham số tenCV
        ChucVu chucVu = new ChucVu(null, tenCV);
        return chucVuService.luu(chucVu);
    }

    // API lấy danh sách ChucVu
    @GetMapping("/DSChucVu")
    public List<ChucVu> layDSDV(@RequestParam String token) {
        return chucVuService.layDSCV();
    }

    // API lấy ChucVu theo mã
    @GetMapping("/ChucVu/{maCV}")
    public ChucVu layCVTheoMa(@PathVariable Long maCV, @RequestParam String token) {
        return chucVuService.layCVTheoMa(maCV);
    }

    // API xóa ChucVu
    @DeleteMapping("/XoaChucVu/{maCV}")
    public String xoa(@PathVariable Long maCV, @RequestParam String token) {
        chucVuService.xoa(maCV);
        return "Đã xoá chức vụ " + maCV;
    }
}
