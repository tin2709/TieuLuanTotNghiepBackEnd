package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.service.MonHocService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    @PostMapping("/LuuMonHoc")
    public MonHoc luu(@RequestBody MonHoc monHoc){
        return monHocService.luu(monHoc);
    }

    @GetMapping("/DSMonHoc")
    public List<MonHoc> layDSMonHoc(){
        return monHocService.layDSMonHoc();
    }

    @DeleteMapping("/XoaMonHoc/{maMon}")
    public String xoa(@PathVariable Long maMon){
        monHocService.xoa(maMon);
        return "Đã xoá quyền " + maMon;
    }
    @GetMapping("/MonHoc/{maMon}")
    public MonHoc layMonHocTheoMa(@PathVariable Long maMon){
        return monHocService.layMonHocTheoMa(maMon);
    }
}
