package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.Khoa;
import com.example.QuanLyPhongMayBackEnd.service.KhoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class KhoaController {

    @Autowired
    private KhoaService khoaService;

    // API thêm mới khoa
    @PostMapping("/LuuKhoa")
    public Khoa luu(@RequestParam Long maKhoa,
                    @RequestParam String tenKhoa,
                    @RequestParam String token) {
        Khoa khoa = new Khoa(maKhoa, tenKhoa);
        return khoaService.luu(khoa, token);
    }

    // API lấy danh sách khoa
    @GetMapping("/DSKhoa")
    public List<Khoa> layDSKhoa(@RequestParam String token) {
        return khoaService.layDSKhoa(token);
    }

    // API lấy khoa theo mã khoa
    @GetMapping("/Khoa/{maKhoa}")
    public Khoa layKhoaTheoMa(@PathVariable Long maKhoa, @RequestParam String token) {
        return khoaService.layKhoaTheoMa(maKhoa, token);
    }

    // API xóa khoa theo mã khoa
    @DeleteMapping("/XoaKhoa/{maKhoa}")
    public String xoa(@PathVariable Long maKhoa, @RequestParam String token) {
        khoaService.xoa(maKhoa, token);
        return "Đã xoá khoa " + maKhoa;
    }
}
