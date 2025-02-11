package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.ChucVu;
import com.example.QuanLyPhongMayBackEnd.service.ChucVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@RestController
@CrossOrigin
public class ChucVuController {

    @Autowired
    private ChucVuService chucVuService;

    @PostMapping("/LuuChucVu")
    public ChucVu luu(@RequestBody ChucVu chucVu){
        return chucVuService.luu(chucVu);
    }

    @GetMapping("/DSChucVu")
    public List<ChucVu> layDSDV(){
        return chucVuService.layDSCV();
    }

    @GetMapping("/ChucVu/{maCV}")
    public ChucVu layCVTheoMa(@PathVariable Long maCV){
        return chucVuService.layCVTheoMa(maCV);
    }

    @DeleteMapping("/XoaChucVu/{maCV}")
    public String xoa(@PathVariable Long maCV){
        chucVuService.xoa(maCV);
        return "Đã xoá chức vụ " + maCV;
    }
}
