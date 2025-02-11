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
    @PostMapping("/LuuGhiChuMayTinh")
    public GhiChuMayTinh luu(@RequestBody GhiChuMayTinh ghiChuMayTinh){
        return ghiChuMayTinhService.luu(ghiChuMayTinh);
    }
    @GetMapping("/DSGhiChuMayTinh")
    public List<GhiChuMayTinh> layDSGhiChuMayTinh(){
        return ghiChuMayTinhService.layDSGhiChu();
    }
    @GetMapping("/DSGhiChuMayTinhTheoNgaySua/{ngaySua}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoNgaySua(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua) {
        List<GhiChuMayTinh> dsCGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoNgaySua(ngaySua);
        return new ResponseEntity<>(dsCGhiChuMayTinh, HttpStatus.OK);
    }
    @GetMapping("/DSGhiChuMayTinhTheoNgayBaoLoi/{ngayBaoLoi}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoNgayBaoLoi(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi) {
        List<GhiChuMayTinh> dsCGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoNgayBaoLoi(ngayBaoLoi);
        return new ResponseEntity<>(dsCGhiChuMayTinh, HttpStatus.OK);
    }
    @GetMapping("/GhiChuMayTinh/{maGhiChuMT}")
    public GhiChuMayTinh layGhiChuTheoMa(@PathVariable Long maGhiChuMT){
        return ghiChuMayTinhService.layGhiChuTheoMa(maGhiChuMT);
    }
    @GetMapping("/DSGhiChuMayTinhTheoMayTinh/{maMay}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoMayTinh(@PathVariable Long maMay) {
        List<GhiChuMayTinh> dsCGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoMayTinh(maMay);

        if (dsCGhiChuMayTinh.isEmpty()) {
            return  ResponseEntity.ok(new ArrayList<>());
        }
        return new ResponseEntity<>(dsCGhiChuMayTinh, HttpStatus.OK);
    }
    @PutMapping("/CapNhatGhiChuMayTinh/{maGhiChuMT}")
    public GhiChuMayTinh capNhat(@PathVariable Long maGhiChuMT, @RequestBody GhiChuMayTinh ghiChuMayTinh){
        GhiChuMayTinh existingGhiChu = ghiChuMayTinhService.layGhiChuTheoMa(maGhiChuMT);
        if (existingGhiChu != null) {
            existingGhiChu.setNgayBaoLoi(ghiChuMayTinh.getNgayBaoLoi());
            existingGhiChu.setNgaySua(ghiChuMayTinh.getNgaySua());
            existingGhiChu.setNoiDung(ghiChuMayTinh.getNoiDung());
            existingGhiChu.setMayTinh(ghiChuMayTinh.getMayTinh());


            return ghiChuMayTinhService.capNhat(existingGhiChu);
        } else {
            return null;
        }
    }

    @DeleteMapping("/XoaGhiChuMayTinh/{maGhiChuMT}")
    public String xoa(@PathVariable Long maGhiChuMT){
        ghiChuMayTinhService.xoa(maGhiChuMT);
        return "Đã xoá chức vụ " + maGhiChuMT;
    }
    @GetMapping("/GhiChuGanNhatTheoPhongMay/{maMay}")
    public ResponseEntity<GhiChuMayTinh> layGhiChuGanNhatTheoMayTinh(@PathVariable Long maMay) {
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
