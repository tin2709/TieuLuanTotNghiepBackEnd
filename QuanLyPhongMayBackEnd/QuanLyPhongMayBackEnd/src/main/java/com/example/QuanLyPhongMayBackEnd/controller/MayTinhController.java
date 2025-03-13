package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.MayTinhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class MayTinhController {

    @Autowired
    private MayTinhService mayTinhService;

    // Thêm mới máy tính
    @PostMapping("/LuuMayTinh")
    public ResponseEntity<MayTinh> luu(@RequestParam Long maMay,
                                       @RequestParam String tenMay,  // Added tenMay to match the new field in MayTinh
                                       @RequestParam String trangThai,
                                       @RequestParam String moTa,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayLapDat,
                                       @RequestParam Long maPhong,
                                       @RequestParam String token) {

        // In ra token để kiểm tra (có thể thay thế bằng việc xác thực token trong thực tế)
        System.out.println("Token: " + token);

        // Tạo đối tượng MayTinh từ các tham số nhận được
        MayTinh mayTinh = new MayTinh(maMay, tenMay, trangThai, moTa, ngayLapDat, null);  // Added tenMay

        // Lấy đối tượng PhongMay từ service dựa trên maPhong và token
        PhongMay phongMay = mayTinhService.getPhongMayById(maPhong, token);

        if (phongMay == null) {
            // Nếu không tìm thấy PhongMay, trả về lỗi
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Optional: Return an error message if needed
        }

        // Gán PhongMay vào đối tượng MayTinh
        mayTinh.setPhongMay(phongMay);

        // Lưu máy tính vào hệ thống
        MayTinh savedMayTinh = mayTinhService.luu(mayTinh, token);

        // Trả về phản hồi với đối tượng MayTinh đã lưu và mã trạng thái CREATED (201)
        return new ResponseEntity<>(savedMayTinh, HttpStatus.CREATED);
    }


    // Lấy danh sách máy tính theo trạng thái
    @GetMapping("/DSMayTinhtheoTrangThai")
    public List<MayTinh> getMayTinhsByTrangThai(@RequestParam String trangThai,
                                                @RequestParam String token) {

        // In ra token để kiểm tra (có thể thay thế bằng việc xác thực token trong thực tế)
        System.out.println("Token: " + token);

        return mayTinhService.findByTrangThai(trangThai, token);
    }

    // Lấy danh sách máy tính
    @GetMapping("/DSMayTinh")
    public List<MayTinh> layDSMayTinh(@RequestParam String token) {

        // In ra token để kiểm tra (có thể thay thế bằng việc xác thực token trong thực tế)
        System.out.println("Token: " + token);

        return mayTinhService.layDSMayTinh(token);
    }

    // Xóa máy tính theo mã
    @DeleteMapping("/XoaMayTinh")
    public ResponseEntity<String> xoa(@RequestParam Long maMay, @RequestParam String token) {

        // In ra token để kiểm tra (có thể thay thế bằng việc xác thực token trong thực tế)
        System.out.println("Token: " + token);

        mayTinhService.xoa(maMay, token);
        return new ResponseEntity<>("Đã xoá máy tính với mã " + maMay, HttpStatus.OK);
    }

    // Lấy máy tính theo mã
    @GetMapping("/MayTinh")
    public ResponseEntity<MayTinh> layMayTinhTheoMa(@RequestParam Long maMay, @RequestParam String token) {

        // In ra token để kiểm tra (có thể thay thế bằng việc xác thực token trong thực tế)
        System.out.println("Token: " + token);

        MayTinh mayTinh = mayTinhService.layMayTinhTheoMa(maMay, token);
        if (mayTinh != null) {
            return new ResponseEntity<>(mayTinh, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
