package com.example.QuanLyPhongMayBackEnd.controller;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuPhongMay;
import com.example.QuanLyPhongMayBackEnd.service.GhiChuPhongMayService;
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
public class GhiChuPhongMayController {
    @Autowired
    private GhiChuPhongMayService ghiChuPhongMayService;
    @PostMapping("/LuuGhiChuPhongMay")
    public GhiChuPhongMay luu(@RequestBody GhiChuPhongMay ghiChuPhongMay){
        return ghiChuPhongMayService.luu(ghiChuPhongMay);
    }
    @GetMapping("/DSGhiChuPhongMay")
    public List<GhiChuPhongMay> layDSGhiChu(){
        return ghiChuPhongMayService.layDSGhiChu();
    }
    @GetMapping("/DSGhiChuPhongMayTheoNgaySua/{ngaySua}")
    public ResponseEntity<List<GhiChuPhongMay>> layDSGhiChuTheoNgaySua(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua) {
        List<GhiChuPhongMay> dsCGhiChu = ghiChuPhongMayService.layDSGhiChuTheoNgaySua(ngaySua);
        return new ResponseEntity<>(dsCGhiChu, HttpStatus.OK);
    }
    @GetMapping("/DSGhiChuPhongMayTheoNgayBaoLoi/{ngayBaoLoi}")
    public ResponseEntity<List<GhiChuPhongMay>> layDSGhiChuTheoNgayBaoLoi(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi) {
        List<GhiChuPhongMay> dsCGhiChu = ghiChuPhongMayService.layDSGhiChuTheoNgayBaoLoi(ngayBaoLoi);
        return new ResponseEntity<>(dsCGhiChu, HttpStatus.OK);
    }
    @GetMapping("/GhiChuPhongMay/{maGhiChu}")
    public GhiChuPhongMay layGhiChuTheoMa(@PathVariable Long maGhiChu){
        return ghiChuPhongMayService.layGhiChuTheoMa(maGhiChu);
    }
    @GetMapping("/DSGhiChuPhongMayTheoPhongMay/{maPhong}")
    public ResponseEntity<List<GhiChuPhongMay>> layDSGhiChuTheoPhongMay(@PathVariable Long maPhong) {
        List<GhiChuPhongMay> dsCGhiChu = ghiChuPhongMayService.layDSGhiChuTheoPhongMay(maPhong);

        if (dsCGhiChu.isEmpty()) {
            return  ResponseEntity.ok(new ArrayList<>());
        }

        return new ResponseEntity<>(dsCGhiChu, HttpStatus.OK);
    }
    @PutMapping("/CapNhatGhiChuPhongMay/{maGhiChu}")
    public GhiChuPhongMay capNhat(@PathVariable Long maGhiChu, @RequestBody GhiChuPhongMay ghiChuPhongMay){
        GhiChuPhongMay existingGhiChu = ghiChuPhongMayService.layGhiChuTheoMa(maGhiChu);
        if (existingGhiChu != null) {
            existingGhiChu.setNgayBaoLoi(ghiChuPhongMay.getNgayBaoLoi());
            existingGhiChu.setNgaySua(ghiChuPhongMay.getNgaySua());
            existingGhiChu.setNoiDung(ghiChuPhongMay.getNoiDung());
            existingGhiChu.setPhongMay(ghiChuPhongMay.getPhongMay());


            return ghiChuPhongMayService.capNhat(existingGhiChu);
        } else {
            return null;
        }
    }

    @DeleteMapping("/XoaGhiChuPhongMay/{maGhiChu}")
    public String xoa(@PathVariable Long maGhiChu){
        ghiChuPhongMayService.xoa(maGhiChu);
        return "Đã xoá chức vụ " + maGhiChu;
    }
    @GetMapping("/GhiChuPhongMayGanNhatTheoPhongMay/{maPhong}")
    public ResponseEntity<GhiChuPhongMay> layGhiChuGanNhatTheoPhongMay(@PathVariable Long maPhong) {
        List<GhiChuPhongMay> dsGhiChu = ghiChuPhongMayService.layDSGhiChuTheoPhongMay(maPhong);

        if (dsGhiChu.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Tìm GhiChu có ngày báo lỗi gần nhất
        GhiChuPhongMay ghiChuGanNhat = dsGhiChu.stream()
                .max(Comparator.comparing(GhiChuPhongMay::getNgayBaoLoi))
                .orElse(null);

        return new ResponseEntity<>(ghiChuGanNhat, HttpStatus.OK);
    }

}
