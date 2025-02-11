package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.service.LichTrucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class LichTrucController {

    @Autowired
    private LichTrucService lichTrucService;

    @PostMapping("/LuuLichTruc")
    public LichTruc luu(@RequestBody LichTruc lichTruc){
        return lichTrucService.luu(lichTruc);
    }

    @GetMapping("/DSLichTruc")
    public List<LichTruc> layDSLT(){
        return lichTrucService.layDSLT();
    }

    @GetMapping("/LichTruc/{maLich}")
    public LichTruc layLTTheoMa(@PathVariable Long maLich){
        return lichTrucService.layLTTheoMa(maLich);
    }
    @GetMapping("/LichTrucTheoTang/{maTang}")
    public List<LichTruc> layLichTrucTheoTang(@PathVariable Long maTang) {
        return lichTrucService.layLichTrucTheoMaTang(maTang);
    }
    @DeleteMapping("/XoaLichTruc/{maLich}")
    public String xoa(@PathVariable Long maLich){
        lichTrucService.xoa(maLich);
        return "Đã xoá chức vụ " + maLich;
    }

    @GetMapping("/TangChuaCoNhanVienTrucTrongThang")
    public List<Tang> layTangChuaCoNhanVienTrucTrongThang() {
        return lichTrucService.layTangChuaCoNhanVienTrucTrongThang();
    }

    @PostMapping("/CapNhatLichTruc")
    public LichTruc capNhatLichTruc(@RequestBody LichTruc lichTruc) {
        return lichTrucService.updateLichTruc(lichTruc);
    }
}
