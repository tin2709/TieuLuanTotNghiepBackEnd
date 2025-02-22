package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.CaThucHanhService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class CaThucHanhController {
    @Autowired
    private CaThucHanhService caThucHanhService;

    // API lưu CaThucHanh
    @PostMapping("/LuuCaThucHanh")
    public CaThucHanh luu(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String tenCa,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam int buoiSo,
            @RequestParam Long maGiaoVien,
            @RequestParam Long maPhong,
            @RequestParam Long maMon,
            @RequestParam String token) {
        // Tạo đối tượng CaThucHanh mới từ các tham số
        return caThucHanhService.luu(new CaThucHanh(null, ngayThucHanh, tenCa, tietBatDau, tietKetThuc, buoiSo,
                new GiaoVien(maGiaoVien), new PhongMay(maPhong), new MonHoc(maMon)), token);
    }

    // API lấy danh sách CaThucHanh
    @GetMapping("/DSCaThucHanh")
    public List<CaThucHanh> layDSCaThucHanh(@RequestParam String token){
        return caThucHanhService.layDSCaThucHanh(token);
    }

    // API lấy danh sách CaThucHanh theo ngày
    @GetMapping("/DSCaThucHanhTheoNgay/{ngayThucHanh}")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoNgay(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String token) {
        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoNgay(ngayThucHanh,token);
        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API lấy CaThucHanh theo mã
    @GetMapping("/CaThucHanh/{maCaThucHanh}")
    public CaThucHanh layCaThucHanhTheoMa(@PathVariable Long maCaThucHanh, @RequestParam String token) {
        return caThucHanhService.layCaThucHanhTheoMa(maCaThucHanh,token);
    }

    // API lấy danh sách CaThucHanh theo môn học
    @GetMapping("/DSCaThucHanhTheoMonHoc/{maMon}")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoMonHoc(@PathVariable Long maMon, @RequestParam String token) {
        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMonHoc(maMon,token);

        if (dsCaThucHanh.isEmpty()) {
            return  ResponseEntity.ok(new ArrayList<>());
        }

        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API cập nhật CaThucHanh theo mã
    @PutMapping("/CapNhatCaThucHanh/{maCaThucHanh}")
    public CaThucHanh capNhat(
            @PathVariable Long maCaThucHanh,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String tenCa,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam int buoiSo,
            @RequestParam Long maGiaoVien,
            @RequestParam Long maPhong,
            @RequestParam Long maMon,
            @RequestParam String token) {
        CaThucHanh existingCaThucHanh = caThucHanhService.layCaThucHanhTheoMa(maCaThucHanh,token);
        if (existingCaThucHanh != null) {
            existingCaThucHanh.setNgayThucHanh(ngayThucHanh);
            existingCaThucHanh.setTenCa(tenCa);
            existingCaThucHanh.setTietBatDau(tietBatDau);
            existingCaThucHanh.setTietKetThuc(tietKetThuc);
            existingCaThucHanh.setBuoiSo(buoiSo);
            existingCaThucHanh.setGiaoVien(new GiaoVien(maGiaoVien));
            existingCaThucHanh.setPhongMay(new PhongMay(maPhong));
            existingCaThucHanh.setMonHoc(new MonHoc(maMon));

            return caThucHanhService.capNhat(existingCaThucHanh,token);
        } else {
            return null;
        }
    }

    // API xóa CaThucHanh
    @DeleteMapping("/XoaCaThucHanh/{maCaThucHanh}")
    public String xoa(@PathVariable Long maCaThucHanh, @RequestParam String token){
        caThucHanhService.xoa(maCaThucHanh,token);
        return "Đã xoá ca thực hành với mã " + maCaThucHanh;
    }
}
