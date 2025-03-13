package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.service.TangService;
import com.example.QuanLyPhongMayBackEnd.service.ToaNhaService;
import io.sentry.Sentry; // Import Sentry để gửi lỗi
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private ToaNhaService toaNhaService;

    @PostMapping("/LuuTang")
    public Tang luu(@RequestParam String tenTang, @RequestParam Long maToaNha, @RequestParam String token) {
        try {
            ToaNha toaNha = toaNhaService.layToaNhaTheoMa(maToaNha, token);
            Tang tang = new Tang();
            tang.setTenTang(tenTang);
            tang.setToaNha(toaNha);
            return tangService.luu(tang, token);
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lưu tầng: " + e.getMessage(), e);
        }
    }

    @GetMapping("/DSTang")
    public List<Tang> layDSTang(@RequestParam String token) {
        try {
            return tangService.layDSTang(token);
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lấy danh sách tầng: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/XoaTang")
    public String xoa(@RequestParam Long maTang, @RequestParam String token) {
        try {
            tangService.xoa(maTang, token);
            return "Đã xoá tầng " + maTang;
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi xoá tầng: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/XoaNhieuTang")
    @Transactional
    public String xoaNhieuPhongMay(@RequestParam List<Long> maTangList, @RequestParam String token) {
        try {
            if (!tangService.isUserLoggedIn(token)) {
                throw new RuntimeException("Token không hợp lệ");
            }

            for (Long maTang : maTangList) {
                tangService.xoa(maTang, token);
            }

            return "Đã xoá " + maTangList.size() + " tầng";
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi xoá các tầng: " + e.getMessage(), e);
        }
    }

    @GetMapping("/TangTheoToaNha/{maToaNha}")
    public List<Tang> layTangTheoToaNha(@PathVariable Long maToaNha, @RequestParam String token) {
        try {
            return tangService.layTangTheoToaNha(maToaNha, token);
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lấy tầng theo tòa nhà: " + e.getMessage(), e);
        }
    }

    @GetMapping("/Tang")
    public Tang layTangTheoMa(@RequestParam Long maTang, @RequestParam String token) {
        try {
            return tangService.layTangTheoMa(maTang, token);
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            throw new RuntimeException("Có lỗi xảy ra khi lấy tầng theo mã: " + e.getMessage(), e);
        }
    }

    @PostMapping("/importTang")
    public String importTangsFromCSV(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        try {
            if (!tangService.isUserLoggedIn(token)) {
                return "Token không hợp lệ!";
            }

            tangService.importCSVFile(file);
            return "Import dữ liệu thành công!";
        } catch (IOException e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            return "Có lỗi xảy ra khi xử lý file CSV: " + e.getMessage();
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            return "Có lỗi xảy ra trong quá trình import: " + e.getMessage();
        }
    }

    @PostMapping("/CapNhatTang")
    public ResponseEntity<Tang> capNhatTang(@RequestParam Long maTang, @RequestParam String tenTang, @RequestParam Long maToaNha, @RequestParam String token) {
        try {
            Tang updatedTang = tangService.capNhatTang(maTang, tenTang, maToaNha, token);

            if (updatedTang == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(updatedTang, HttpStatus.OK);
        } catch (Exception e) {
            Sentry.captureException(e); // Gửi tất cả exception tới Sentry
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
