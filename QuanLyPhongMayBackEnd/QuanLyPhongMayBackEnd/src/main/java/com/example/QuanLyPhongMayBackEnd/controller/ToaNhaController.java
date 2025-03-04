package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.service.ToaNhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class ToaNhaController {

    @Autowired
    private ToaNhaService toaNhaService;

    @PostMapping("/LuuToaNha")
    public ToaNha luu(
            @RequestParam String tenToaNha,
            @RequestParam String token) {

        // Handle token validation if needed

        // Create a new ToaNha entity
        ToaNha toaNha = new ToaNha();
        toaNha.setTenToaNha(tenToaNha);

        // Save the ToaNha entity using the service
        return toaNhaService.luu(toaNha, token);
    }

    @GetMapping("/DSToaNha")
    public List<ToaNha> layDSToaNha(@RequestParam String token) {
        // Handle token validation if needed
        return toaNhaService.layDSToaNha(token);
    }

    @DeleteMapping("/XoaToaNha/{maToaNha}")
    public String xoa(@PathVariable Long maToaNha, @RequestParam String token) {
        // Handle token validation if needed
        toaNhaService.xoa(maToaNha, token);
        return "Đã xoá tòa nhà " + maToaNha;
    }

    @GetMapping("/ToaNha/{maToaNha}")
    public ToaNha layToaNhaTheoMa(@PathVariable Long maToaNha, @RequestParam String token) {
        // Handle token validation if needed
        return toaNhaService.layToaNhaTheoMa(maToaNha, token);
    }
    @PostMapping("/ImportToaNha")
    public String importToaNhaFromCSV(@RequestParam("file") MultipartFile file, @RequestParam String token) throws IOException {
        // Kiểm tra token hợp lệ
        if (!toaNhaService.isUserLoggedIn(token)) {
            return "Token không hợp lệ!";
        }

        // Đọc dữ liệu từ file CSV và xử lý
        try {
            // Chuyển file CSV thành danh sách đối tượng ToaNha và lưu vào cơ sở dữ liệu
            toaNhaService.importCSVFile(file);
            return "Import thành công!";
        } catch (Exception e) {
            return "Có lỗi xảy ra trong quá trình import: " + e.getMessage();
        }
    }
}
