package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.service.GhiChuMayTinhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class GhiChuMayTinhController {
    @Autowired
    private GhiChuMayTinhService ghiChuMayTinhService;

    // API lưu GhiChuMayTinh
    @PostMapping("/LuuGhiChuMayTinh")
    public GhiChuMayTinh luu(
            @RequestParam String noiDung,
            @RequestParam Long maMay,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
            @RequestParam String maTKBaoLoi,
            @RequestParam String nguoiSuaLoi,
            @RequestParam String token) {

        GhiChuMayTinh ghiChuMayTinh = new GhiChuMayTinh(null, noiDung, null, ngayBaoLoi, ngaySua, maTKBaoLoi, nguoiSuaLoi);
        return ghiChuMayTinhService.luu(ghiChuMayTinh);
    }

    // API lấy danh sách GhiChuMayTinh
    @GetMapping("/DSGhiChuMayTinh")
    public List<GhiChuMayTinh> layDSGhiChuMayTinh(@RequestParam String token){
        return ghiChuMayTinhService.layDSGhiChu();
    }

    // API lấy danh sách GhiChuMayTinh theo ngày sửa
    @GetMapping("/DSGhiChuMayTinhTheoNgaySua/{ngaySua}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoNgaySua(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
            @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoNgaySua(ngaySua);
        return new ResponseEntity<>(dsGhiChuMayTinh, HttpStatus.OK);
    }

    // API lấy danh sách GhiChuMayTinh theo ngày báo lỗi
    @GetMapping("/DSGhiChuMayTinhTheoNgayBaoLoi/{ngayBaoLoi}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoNgayBaoLoi(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
            @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoNgayBaoLoi(ngayBaoLoi);
        return new ResponseEntity<>(dsGhiChuMayTinh, HttpStatus.OK);
    }

    // API lấy GhiChuMayTinh theo mã
    @GetMapping("/GhiChuMayTinh/{maGhiChuMT}")
    public GhiChuMayTinh layGhiChuTheoMa(@PathVariable Long maGhiChuMT, @RequestParam String token){
        return ghiChuMayTinhService.layGhiChuTheoMa(maGhiChuMT);
    }

    // API lấy danh sách GhiChuMayTinh theo máy tính
    @GetMapping("/DSGhiChuMayTinhTheoMayTinh/{maMay}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoMayTinh(@PathVariable Long maMay, @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoMayTinh(maMay);

        if (dsGhiChuMayTinh.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return new ResponseEntity<>(dsGhiChuMayTinh, HttpStatus.OK);
    }

    // API cập nhật GhiChuMayTinh
    @PutMapping("/CapNhatGhiChuMayTinh/{maGhiChuMT}")
    public GhiChuMayTinh capNhat(
            @PathVariable Long maGhiChuMT,
            @RequestParam String noiDung,
            @RequestParam Long maMay,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
            @RequestParam String maTKBaoLoi,
            @RequestParam String nguoiSuaLoi,
            @RequestParam String token) {

        GhiChuMayTinh existingGhiChu = ghiChuMayTinhService.layGhiChuTheoMa(maGhiChuMT);
        if (existingGhiChu != null) {
            existingGhiChu.setNgayBaoLoi(ngayBaoLoi);
            existingGhiChu.setNgaySua(ngaySua);
            existingGhiChu.setNoiDung(noiDung);
            existingGhiChu.setMayTinh(null); // Không có thay đổi cho MayTinh

            return ghiChuMayTinhService.capNhat(existingGhiChu);
        } else {
            return null;
        }
    }

    // API xóa GhiChuMayTinh
    @DeleteMapping("/XoaGhiChuMayTinh/{maGhiChuMT}")
    public String xoa(@PathVariable Long maGhiChuMT, @RequestParam String token){
        ghiChuMayTinhService.xoa(maGhiChuMT);
        return "Đã xoá ghi chú máy tính " + maGhiChuMT;
    }

    // API lấy ghi chú gần nhất theo máy tính
    @GetMapping("/GhiChuGanNhatTheoPhongMay/{maMay}")
    public ResponseEntity<GhiChuMayTinh> layGhiChuGanNhatTheoMayTinh(@PathVariable Long maMay, @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChu = ghiChuMayTinhService.layDSGhiChuTheoMayTinh(maMay);
        if (dsGhiChu.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GhiChuMayTinh ghiChuGanNhat = dsGhiChu.stream()
                .max(Comparator.comparing(GhiChuMayTinh::getNgayBaoLoi))
                .orElse(null);
        return new ResponseEntity<>(ghiChuGanNhat, HttpStatus.OK);
    }
}
