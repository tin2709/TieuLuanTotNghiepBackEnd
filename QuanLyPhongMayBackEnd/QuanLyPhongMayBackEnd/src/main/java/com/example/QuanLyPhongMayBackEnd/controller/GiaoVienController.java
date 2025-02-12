package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
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

    @GetMapping("/DSGiaoVien")
    public List<GiaoVien> layDSGV(){
        return giaoVienService.layDSGV();
    }

    @GetMapping("/GiaoVien/{maGiaoVien}")
    public GiaoVien layGVTheoMa(@PathVariable String maGiaoVien){
        return giaoVienService.layGVTheoMa(maGiaoVien);
    }

    @PostMapping("/LuuGiaoVien")
    public GiaoVien luu(@RequestBody GiaoVien giaoVien) {
        return giaoVienService.luu(giaoVien);
    }

    @DeleteMapping("/XoaGiaoVien/{maGiaoVien}")
    public String xoa(@PathVariable String maGiaoVien) {
        giaoVienService.xoa(maGiaoVien);
        return "Đã xoá giáo viên " + maGiaoVien;
    }

    // API phân trang danh sách giáo viên
    @GetMapping("/DSGiaoVien/phantang")
    public Page<GiaoVien> layDSGVPhanTrang(@RequestParam int pageNumber) {
        return giaoVienService.layDSGVPhanTrang(pageNumber);
    }
}
