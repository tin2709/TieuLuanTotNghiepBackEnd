package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.service.TangService;
import com.example.QuanLyPhongMayBackEnd.service.ToaNhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class TangController {

    @Autowired
    private TangService tangService;

    @Autowired
    private ToaNhaService toaNhaService;  // Assuming a service for ToaNha exists

    @PostMapping("/LuuTang")
    public Tang luu(
            @RequestParam String tenTang,
            @RequestParam Long maToaNha,
            @RequestParam String token) {

        // Retrieve the ToaNha entity based on maToaNha
        ToaNha toaNha = toaNhaService.layToaNhaTheoMa(maToaNha, token); // You may need to create this method in the ToaNhaService

        // Create Tang object and set fields
        Tang tang = new Tang();
        tang.setTenTang(tenTang);
        tang.setToaNha(toaNha);

        // Handle token validation if needed

        return tangService.luu(tang, token);
    }

    @GetMapping("/DSTang")
    public List<Tang> layDSTang(@RequestParam String token) {
        // Handle token validation if needed
        return tangService.layDSTang(token);
    }

    @DeleteMapping("/XoaTang/{maTang}")
    public String xoa(@PathVariable Long maTang, @RequestParam String token) {
        // Handle token validation if needed
        tangService.xoa(maTang, token);
        return "Đã xoá tầng " + maTang;
    }

    @GetMapping("/TangTheoToaNha/{maToaNha}")
    public List<Tang> layTangTheoToaNha(@PathVariable Long maToaNha, @RequestParam String token) {
        // Handle token validation if needed
        return tangService.layTangTheoToaNha(maToaNha, token);
    }

    @GetMapping("/Tang/{maTang}")
    public Tang layTangTheoMa(@PathVariable Long maTang, @RequestParam String token) {
        // Handle token validation if needed
        return tangService.layTangTheoMa(maTang, token);
    }
    @PostMapping("/importTang")
    public String importTangsFromCSV(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        if (!tangService.isUserLoggedIn(token)) {
            return "Token không hợp lệ!";
        }
        try {
            tangService.importCSVFile(file);
            return "Import dữ liệu thành công!";
        } catch (IOException e) {
            return "Có lỗi xảy ra khi xử lý file CSV: " + e.getMessage();
        } catch (Exception e) {
            return "Có lỗi xảy ra trong quá trình import: " + e.getMessage();
        }
    }
}
