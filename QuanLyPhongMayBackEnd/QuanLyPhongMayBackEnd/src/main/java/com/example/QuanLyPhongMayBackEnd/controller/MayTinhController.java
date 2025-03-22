// MayTinhController.java
package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.MayTinhService;
import io.sentry.Sentry;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class MayTinhController {

    @Autowired
    private MayTinhService mayTinhService;

    // Thêm mới máy tính
    @PostMapping("/LuuMayTinh")
    public ResponseEntity<Object> luu(
            @RequestParam String tenMay,
            @RequestParam String trangThai,
            @RequestParam String moTa,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayLapDat,
            @RequestParam Long maPhong,
            @RequestParam String token) {

        try {
            PhongMay phongMay = mayTinhService.getPhongMayById(maPhong, token);

            if (phongMay == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy phòng máy với mã phòng: " + maPhong);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            MayTinh mayTinh = new MayTinh();
            mayTinh.setTenMay(tenMay);
            mayTinh.setTrangThai(trangThai);
            mayTinh.setMoTa(moTa);
            mayTinh.setNgayLapDat(ngayLapDat);
            mayTinh.setPhongMay(phongMay);

            MayTinh savedMayTinh = mayTinhService.luu(mayTinh, token);
            return new ResponseEntity<>(savedMayTinh, HttpStatus.CREATED);

        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu máy tính: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy danh sách máy tính theo trạng thái
    @GetMapping("/DSMayTinhtheoTrangThai")
    public List<MayTinh> getMayTinhsByTrangThai(@RequestParam String trangThai,
                                                @RequestParam String token) {

        System.out.println("Token: " + token);
        return mayTinhService.findByTrangThai(trangThai, token);
    }

    // Lấy danh sách máy tính
    @GetMapping("/DSMayTinh")
    public List<MayTinh> layDSMayTinh(@RequestParam String token) {

        System.out.println("Token: " + token);
        return mayTinhService.layDSMayTinh(token);
    }

    // Xóa máy tính theo mã
    @DeleteMapping("/XoaMayTinh")
    public ResponseEntity<String> xoa(@RequestParam Long maMay, @RequestParam String token) {

        System.out.println("Token: " + token);
        mayTinhService.xoa(maMay, token);
        return new ResponseEntity<>("Đã xoá máy tính với mã " + maMay, HttpStatus.OK);
    }

    @DeleteMapping("/XoaNhieuMayTinh")
    @Transactional
    public String xoaNhieuPhongMay(@RequestParam List<Long> maMayTinhList, @RequestParam String token) {
        try {
            if (!mayTinhService.isUserLoggedIn(token)) {
                throw new RuntimeException("Token không hợp lệ");
            }

            for (Long maMayTinh : maMayTinhList) {
                mayTinhService.xoa(maMayTinh, token);
            }

            return "Đã xoá " + maMayTinhList.size() + " tầng";
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Có lỗi xảy ra khi xoá các tầng: " + e.getMessage(), e);
        }
    }
    // Cập nhật máy tính
    @PutMapping("/CapNhatMayTinh") // Use PUT for updates
    public ResponseEntity<Object> capNhatMayTinh(
            @RequestParam Long maMay, // Need maMay to identify the computer
            @RequestParam String tenMay,
            @RequestParam String trangThai,
            @RequestParam String moTa,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayLapDat,
            @RequestParam Long maPhong,
            @RequestParam String token) {

        try {
            PhongMay phongMay = mayTinhService.getPhongMayById(maPhong, token);
            if (phongMay == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy phòng máy với mã phòng: " + maPhong);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            MayTinh mayTinh = mayTinhService.layMayTinhTheoMa(maMay, token);
            if(mayTinh == null){
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy máy tính với mã máy: " + maMay);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            // Update the fields
            mayTinh.setTenMay(tenMay);
            mayTinh.setTrangThai(trangThai);
            mayTinh.setMoTa(moTa);
            mayTinh.setNgayLapDat(ngayLapDat);
            mayTinh.setPhongMay(phongMay); // Associate with the new PhongMay

            MayTinh updatedMayTinh = mayTinhService.capNhatMayTinh(mayTinh, token); // Call update method
            return new ResponseEntity<>(updatedMayTinh, HttpStatus.OK); // Return 200 OK with updated object


        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật máy tính: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Lấy máy tính theo mã
    @GetMapping("/MayTinh")
    public ResponseEntity<MayTinh> layMayTinhTheoMa(@RequestParam Long maMay, @RequestParam String token) {

        System.out.println("Token: " + token);

        MayTinh mayTinh = mayTinhService.layMayTinhTheoMa(maMay, token);
        if (mayTinh != null) {
            return new ResponseEntity<>(mayTinh, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}