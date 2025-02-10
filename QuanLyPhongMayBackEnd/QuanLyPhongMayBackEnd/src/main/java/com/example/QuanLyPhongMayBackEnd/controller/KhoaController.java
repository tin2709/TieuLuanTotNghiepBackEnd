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

    @PostMapping("/LuuKhoa")
    public Khoa luu(@RequestBody Khoa khoa){
        return khoaService.luu(khoa);
    }

    @GetMapping("/DSKhoa")
    public List<Khoa> layDSDV(){
        return khoaService.layDSKhoa();
    }

    @GetMapping("/Khoa/{maKhoa}")
    public Khoa layKhoaTheoMa(@PathVariable Long maKhoa){
        return khoaService.layKhoaTheoMa(maKhoa);
    }

    @DeleteMapping("/XoaKhoa/{maKhoa}")
    public String xoa(@PathVariable Long maKhoa){
        khoaService.xoa(maKhoa);
        return "Đã xoá khoa " + maKhoa;
    }
}
