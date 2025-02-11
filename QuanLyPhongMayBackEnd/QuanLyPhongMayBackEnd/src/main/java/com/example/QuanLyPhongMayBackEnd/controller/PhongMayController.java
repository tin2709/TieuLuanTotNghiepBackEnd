package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.PhongMayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class PhongMayController {


    @Autowired
    private PhongMayService phongMayService;


    @PostMapping("/LuuPhongMay")
    public PhongMay luu(@RequestBody PhongMay phongMay){
        return phongMayService.luu(phongMay);
    }

    @GetMapping("/DSPhongMay")
    public List<PhongMay> layDSPhongMay(){
        return phongMayService.layDSPhongMay();
    }
    @GetMapping("/DSPhongMaytheoTrangThai/{trangThai}")
    public List<PhongMay> getPhongMaysByTrangThai(@PathVariable String trangThai) {
        return phongMayService.findByTrangThai(trangThai);
    }
    //

    //

    @GetMapping("/PhongMay/{maPhong}")
    public PhongMay layPhongMayTheoMa(@PathVariable Long maPhong){
        return phongMayService.layPhongMayTheoMa(maPhong);
    }

    @DeleteMapping("/XoaPhongMay/{maPhong}")
    @Transactional
    public String xoa(@PathVariable Long maPhong) {
        phongMayService.xoa(maPhong);

        return "Đã xoá " + maPhong;

    }


    @PutMapping("/CapNhatPhongMay/{maPhong}")
    public PhongMay capNhatTheoMa(@PathVariable Long maPhong, @RequestBody PhongMay phongMay){
        return phongMayService.capNhatTheoMa(maPhong, phongMay);
    }
}
