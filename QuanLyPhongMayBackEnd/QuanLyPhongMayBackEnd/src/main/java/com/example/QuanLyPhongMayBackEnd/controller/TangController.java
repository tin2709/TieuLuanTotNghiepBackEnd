package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.service.TangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class TangController {

    @Autowired
    private TangService tangService;
  ;
    @PostMapping("/LuuTang")
    public Tang luu(@RequestBody Tang tang){
        return tangService.luu(tang);
    }

    @GetMapping("/DSTang")
    public List<Tang> layDSTang(){
        return tangService.layDSTang();
    }
    @DeleteMapping("/XoaTang/{maTang}")
    public String xoa(@PathVariable Long maTang){
        tangService.xoa(maTang);
        return "Đã xoá quyền " + maTang;
    }

    @GetMapping("/TangTheoToaNha/{maToaNha}")
    public List<Tang> layTangTheoToaNha(@PathVariable Long maToaNha) {
        return tangService.layTangTheoToaNha(maToaNha);
    }

    @GetMapping("/Tang/{maTang}")
    public Tang layTangTheoMa(@PathVariable Long maTang) {
        return tangService.layTangTheoMa(maTang);
    }
}
