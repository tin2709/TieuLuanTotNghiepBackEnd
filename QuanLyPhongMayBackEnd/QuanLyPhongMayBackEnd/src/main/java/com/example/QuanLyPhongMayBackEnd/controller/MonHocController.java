package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.service.MonHocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    // API lưu môn học mới
    @PostMapping("/LuuMonHoc")
    public MonHoc luu(@RequestParam String tenMon,
                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                      @RequestParam int soBuoi,
                      @RequestParam String token) {
        // Tạo đối tượng MonHoc từ các tham số
        MonHoc monHoc = new MonHoc();
        monHoc.setTenMon(tenMon);
        monHoc.setNgayBatDau(ngayBatDau);
        monHoc.setSoBuoi(soBuoi);

        // Lưu môn học
        return monHocService.luu(monHoc);
    }

    // API lấy danh sách môn học
    @GetMapping("/DSMonHoc")
    public List<MonHoc> layDSMonHoc(@RequestParam String token) {
        return monHocService.layDSMonHoc();
    }

    // API xóa môn học theo mã môn
    @DeleteMapping("/XoaMonHoc/{maMon}")
    public String xoa(@PathVariable Long maMon, @RequestParam String token) {
        monHocService.xoa(maMon);
        return "Đã xoá môn học với mã " + maMon;
    }

    // API lấy môn học theo mã môn
    @GetMapping("/MonHoc/{maMon}")
    public MonHoc layMonHocTheoMa(@PathVariable Long maMon, @RequestParam String token) {
        return monHocService.layMonHocTheoMa(maMon);
    }
}
