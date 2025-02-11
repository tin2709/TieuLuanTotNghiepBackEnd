package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.NhanVien;
import com.example.QuanLyPhongMayBackEnd.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@CrossOrigin
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping("/DSNhanVien")
    public List<NhanVien> layDSNV(){
        return nhanVienService.layDSNV();
    }

    @GetMapping("/NhanVien/{maNV}")
    public NhanVien layNVTheoMa(@PathVariable String maNV){
        return nhanVienService.layNVTheoMa(maNV);
    }

    @PostMapping("/LuuNhanVien")
    public NhanVien luu(@RequestBody NhanVien nhanVien) {
        return nhanVienService.luu(nhanVien);
    }

    @DeleteMapping("/XoaNhanVien/{maNV}")
    public String xoa(@PathVariable String maNV) {
        nhanVienService.xoa(maNV);
        return "Đã xoá nhân viên " + maNV;
    }
}
