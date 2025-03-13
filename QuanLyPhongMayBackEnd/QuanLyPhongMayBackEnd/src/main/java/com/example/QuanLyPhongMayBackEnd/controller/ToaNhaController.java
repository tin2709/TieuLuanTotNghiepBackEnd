package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.service.ToaNhaService;
import io.sentry.Sentry;
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
    public ToaNha luu(@RequestParam String tenToaNha, @RequestParam String token) {
        try {
            // Handle token validation if needed
            ToaNha toaNha = new ToaNha();
            toaNha.setTenToaNha(tenToaNha);
            return toaNhaService.luu(toaNha, token);
        } catch (Exception e) {
            Sentry.captureException(e);  // Gửi exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lưu tòa nhà: " + e.getMessage());
        }
    }

    @GetMapping("/DSToaNha")
    public List<ToaNha> layDSToaNha(@RequestParam String token) {
        try {
            return toaNhaService.layDSToaNha(token);
        } catch (Exception e) {
            Sentry.captureException(e);  // Gửi exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lấy danh sách tòa nhà: " + e.getMessage());
        }
    }

    @DeleteMapping("/XoaToaNha/{maToaNha}")
    public String xoa(@PathVariable Long maToaNha, @RequestParam String token) {
        try {
            toaNhaService.xoa(maToaNha, token);
            return "Đã xoá tòa nhà " + maToaNha;
        } catch (Exception e) {
            Sentry.captureException(e);  // Gửi exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi xoá tòa nhà: " + e.getMessage());
        }
    }

    @GetMapping("/ToaNha/{maToaNha}")
    public ToaNha layToaNhaTheoMa(@PathVariable Long maToaNha, @RequestParam String token) {
        try {
            return toaNhaService.layToaNhaTheoMa(maToaNha, token);
        } catch (Exception e) {
            Sentry.captureException(e);  // Gửi exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lấy tòa nhà: " + e.getMessage());
        }
    }

    @PostMapping("/ImportToaNha")
    public String importToaNhaFromCSV(@RequestParam("file") MultipartFile file, @RequestParam String token) throws IOException {
        try {
            // Kiểm tra token hợp lệ
            if (!toaNhaService.isUserLoggedIn(token)) {
                throw new RuntimeException("Token không hợp lệ!");
            }

            // Đọc dữ liệu từ file CSV và xử lý
            toaNhaService.importCSVFile(file);
            return "Import thành công!";
        } catch (IOException e) {
            Sentry.captureException(e);  // Gửi exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra trong quá trình import: " + e.getMessage());
        } catch (Exception e) {
            Sentry.captureException(e);  // Gửi exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra trong quá trình import: " + e.getMessage());
        }
    }
}
