package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.service.ToaNhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ToaNhaController {

    @Autowired
    private ToaNhaService toaNhaService;

    @PostMapping("/LuuToaNha")
    public ToaNha luu(@RequestBody ToaNha toaNha){
        return toaNhaService.luu(toaNha);
    }

    @GetMapping("/DSToaNha")
    public List<ToaNha> layDSToaNha(){
        return toaNhaService.layDSToaNha();
    }

    @DeleteMapping("/XoaToaNha/{maToaNha}")
    public String xoa(@PathVariable Long maToaNha){
        toaNhaService.xoa(maToaNha);
        return "Đã xoá quyền " + maToaNha;
    }
    @GetMapping("/ToaNha/{maToaNha}")
    public ToaNha layThietBiMayTheoMa(@PathVariable Long maToaNha){
        return toaNhaService.layToaNhaTheoMa(maToaNha);
    }

}
