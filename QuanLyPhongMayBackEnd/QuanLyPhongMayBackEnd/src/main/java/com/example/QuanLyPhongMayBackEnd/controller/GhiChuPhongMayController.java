package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuPhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
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

    // API thêm ghi chú phòng máy
    @PostMapping("/LuuGhiChuPhongMay")
    public GhiChuPhongMay luu(@RequestParam Long maGhiChu,
                              @RequestParam String noiDung,
                              @RequestParam Long maPhong,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
                              @RequestParam String maTKBaoLoi,
                              @RequestParam String nguoiSuaLoi,
                              @RequestParam String token) {

        PhongMay phongMay = new PhongMay();
        phongMay.setMaPhong(maPhong); // Giả sử PhongMay có setter cho maPhong

        GhiChuPhongMay ghiChuPhongMay = new GhiChuPhongMay(maGhiChu, noiDung, phongMay, ngayBaoLoi, ngaySua, maTKBaoLoi, nguoiSuaLoi);
        return ghiChuPhongMayService.luu(ghiChuPhongMay);
    }

    // API lấy danh sách ghi chú phòng máy
    @GetMapping("/DSGhiChuPhongMay")
    public List<GhiChuPhongMay> layDSGhiChu(@RequestParam String token){
        return ghiChuPhongMayService.layDSGhiChu();
    }

    // API lấy danh sách ghi chú theo ngày sửa
    @GetMapping("/DSGhiChuPhongMayTheoNgaySua/{ngaySua}")
    public ResponseEntity<List<GhiChuPhongMay>> layDSGhiChuTheoNgaySua(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
            @RequestParam String token) {
        List<GhiChuPhongMay> dsGhiChu = ghiChuPhongMayService.layDSGhiChuTheoNgaySua(ngaySua);
        return new ResponseEntity<>(dsGhiChu, HttpStatus.OK);
    }

    // API lấy danh sách ghi chú theo ngày báo lỗi
    @GetMapping("/DSGhiChuPhongMayTheoNgayBaoLoi/{ngayBaoLoi}")
    public ResponseEntity<List<GhiChuPhongMay>> layDSGhiChuTheoNgayBaoLoi(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
            @RequestParam String token) {
        List<GhiChuPhongMay> dsGhiChu = ghiChuPhongMayService.layDSGhiChuTheoNgayBaoLoi(ngayBaoLoi);
        return new ResponseEntity<>(dsGhiChu, HttpStatus.OK);
    }

    // API lấy ghi chú theo mã ghi chú
    @GetMapping("/GhiChuPhongMay/{maGhiChu}")
    public GhiChuPhongMay layGhiChuTheoMa(@PathVariable Long maGhiChu, @RequestParam String token){
        return ghiChuPhongMayService.layGhiChuTheoMa(maGhiChu);
    }

    // API lấy danh sách ghi chú theo phòng máy
    @GetMapping("/DSGhiChuPhongMayTheoPhongMay/{maPhong}")
    public ResponseEntity<List<GhiChuPhongMay>> layDSGhiChuTheoPhongMay(
            @PathVariable Long maPhong, @RequestParam String token) {
        List<GhiChuPhongMay> dsGhiChu = ghiChuPhongMayService.layDSGhiChuTheoPhongMay(maPhong);

        if (dsGhiChu.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        return new ResponseEntity<>(dsGhiChu, HttpStatus.OK);
    }

    // API cập nhật ghi chú phòng máy
    @PutMapping("/CapNhatGhiChuPhongMay/{maGhiChu}")
    public GhiChuPhongMay capNhat(@PathVariable Long maGhiChu,
                                  @RequestParam String noiDung,
                                  @RequestParam Long maPhong,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
                                  @RequestParam String maTKBaoLoi,
                                  @RequestParam String nguoiSuaLoi,
                                  @RequestParam String token) {
        GhiChuPhongMay existingGhiChu = ghiChuPhongMayService.layGhiChuTheoMa(maGhiChu);
        if (existingGhiChu != null) {
            PhongMay phongMay = new PhongMay();
            phongMay.setMaPhong(maPhong);  // Giả sử PhongMay có method setMaPhong
            existingGhiChu.setNoiDung(noiDung);
            existingGhiChu.setPhongMay(phongMay);
            existingGhiChu.setNgayBaoLoi(ngayBaoLoi);
            existingGhiChu.setNgaySua(ngaySua);
            existingGhiChu.setMaTKBaoLoi(maTKBaoLoi);
            existingGhiChu.setNguoiSuaLoi(nguoiSuaLoi);

            return ghiChuPhongMayService.capNhat(existingGhiChu);
        }
        return null;
    }

    // API xoá ghi chú phòng máy
    @DeleteMapping("/XoaGhiChuPhongMay/{maGhiChu}")
    public String xoa(@PathVariable Long maGhiChu, @RequestParam String token) {
        ghiChuPhongMayService.xoa(maGhiChu);
        return "Đã xoá ghi chú với mã " + maGhiChu;
    }

    // API lấy ghi chú gần nhất theo phòng máy
    @GetMapping("/GhiChuPhongMayGanNhatTheoPhongMay/{maPhong}")
    public ResponseEntity<GhiChuPhongMay> layGhiChuGanNhatTheoPhongMay(
            @PathVariable Long maPhong, @RequestParam String token) {
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
