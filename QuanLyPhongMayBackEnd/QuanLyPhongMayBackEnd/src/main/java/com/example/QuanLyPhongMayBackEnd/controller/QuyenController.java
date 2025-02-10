package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.Quyen;
import com.example.QuanLyPhongMayBackEnd.service.QuyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class QuyenController {

    @Autowired
    private QuyenService quyenService;

    @PostMapping("/LuuQuyen")
    public Quyen luu(@RequestBody Quyen quyen){
        return quyenService.luu(quyen);
    }

    @GetMapping("/DSQuyen")
    public List<Quyen> layDSQuyen(){
        return quyenService.layDSQuyen();
    }

    @DeleteMapping("/XoaQuyen/{maQuyen}")
    public String xoa(@PathVariable Long maQuyen){
        quyenService.xoa(maQuyen);
        return "Đã xoá quyền " + maQuyen;
    }

}
