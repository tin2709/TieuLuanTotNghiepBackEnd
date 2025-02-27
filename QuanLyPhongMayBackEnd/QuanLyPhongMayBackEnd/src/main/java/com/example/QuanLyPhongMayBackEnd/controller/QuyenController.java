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
    public Quyen luu(
            @RequestParam String tenQuyen,
            @RequestParam String token) {

        // Create a Quyen object from the request parameters
        Quyen quyen = new Quyen();
        quyen.setTenQuyen(tenQuyen);

        // Assuming token validation is handled before calling the service
        return quyenService.luu(quyen, token);
    }

    @GetMapping("/DSQuyen")
    public List<Quyen> layDSQuyen(@RequestParam String token) {
        // Handle token validation if necessary
        return quyenService.layDSQuyen(token);
    }

    @DeleteMapping("/XoaQuyen/{maQuyen}")
    public String xoa(@PathVariable Long maQuyen, @RequestParam String token) {
        // Handle token validation if necessary
        quyenService.xoa(maQuyen, token);
        return "Đã xoá quyền " + maQuyen;
    }
}
