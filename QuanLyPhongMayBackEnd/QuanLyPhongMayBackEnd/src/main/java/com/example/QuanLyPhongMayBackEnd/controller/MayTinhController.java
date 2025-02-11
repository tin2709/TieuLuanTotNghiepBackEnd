package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.service.MayTinhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class MayTinhController {

    @Autowired
    private MayTinhService mayTinhService;




    @PostMapping("/LuuMayTinh")
    public MayTinh luu(@RequestBody MayTinh mayTinh) {
        System.out.println(mayTinh);
        return mayTinhService.luu(mayTinh);
    }
    @GetMapping("/DSMayTinhtheoTrangThai/{trangThai}")
    public List<MayTinh> getMayTinhsByTrangThai(@PathVariable String trangThai) {
        return mayTinhService.findByTrangThai(trangThai);
    }

    @GetMapping("/DSMayTinh")
    public List<MayTinh> layDSMayTinh() {
        return mayTinhService.layDSMayTinh();
    }



    @DeleteMapping("/XoaMayTinh/{maMay}")
    public String xoa(@PathVariable Long maMay) {
        mayTinhService.xoa(maMay);
        return "Đã xoá quyền " + maMay;
    }

    @GetMapping("/MayTinh/{maMay}")
    public MayTinh layMayTinhTheoMa(@PathVariable Long maMay) {
        return mayTinhService.layMayTinhTheoMa(maMay);
    }
}
