package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.GiaoVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.*;
import com.example.QuanLyPhongMayBackEnd.service.GiaoVienService;
import com.example.QuanLyPhongMayBackEnd.service.NhanVienService;
import com.example.QuanLyPhongMayBackEnd.service.QuyenService;
import com.example.QuanLyPhongMayBackEnd.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
public class GiaoVienController {

    @Autowired
    private GiaoVienService giaoVienService;
    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private QuyenService  quyenService;

    // API lấy danh sách giáo viên
    @GetMapping("/DSGiaoVien")
    public List<GiaoVien> layDSGV(@RequestParam String token) {
        List<GiaoVien> giaoVienList = giaoVienService.layDSGV(token);
        if (giaoVienList == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }
        return giaoVienList;
    }

    // API lấy giáo viên theo mã giáo viên
    @GetMapping("/GiaoVien/{maGiaoVien}")
    public GiaoVien layGVTheoMa(@PathVariable String maGiaoVien, @RequestParam String token) {
        GiaoVien giaoVien = giaoVienService.layGVTheoMa(maGiaoVien, token);
        if (giaoVien == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc giáo viên không tồn tại");
        }
        return giaoVien;
    }

    // API thêm mới giáo viên
    @PostMapping("/LuuGiaoVien")
    public GiaoVien luu(
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String email,
            @RequestParam String hocVi,
            @RequestParam Long taiKhoanMaTK,
            @RequestParam Long khoaMaKhoa
           ) {



        // Tạo đối tượng TaiKhoan và Khoa từ các tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);  // Giả sử TaiKhoan có constructor nhận maTK
        Khoa khoa = new Khoa(khoaMaKhoa);  // Giả sử Khoa có constructor nhận maKhoa

        // Tạo giáo viên từ các tham số
        GiaoVien giaoVien = new GiaoVien();
        giaoVien.setHoTen(hoTen);
        giaoVien.setSoDienThoai(soDienThoai);
        giaoVien.setEmail(email);
        giaoVien.setHocVi(hocVi);
        giaoVien.setKhoa(khoa);
        giaoVien.setTaiKhoan(taiKhoan);



        return giaoVienService.luu(giaoVien);
    }


    // API xóa giáo viên theo mã giáo viên
    @DeleteMapping("/XoaGiaoVien/{maGiaoVien}")
    public String xoa(@PathVariable String maGiaoVien, @RequestParam String token) {
        if (!giaoVienService.isUserLoggedIn(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ");
        }
        giaoVienService.xoa(Long.valueOf(maGiaoVien), token);
        return "Đã xoá giáo viên " + maGiaoVien;
    }

    // API phân trang danh sách giáo viên
    @GetMapping("/DSGiaoVien/phantang")
    public Page<GiaoVien> layDSGVPhanTrang(@RequestParam int pageNumber, @RequestParam String token) {
        Page<GiaoVien> giaoVienPage = giaoVienService.layDSGVPhanTrang(pageNumber, token);
        if (giaoVienPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token không hợp lệ hoặc không có dữ liệu");
        }
        return giaoVienPage;
    }
    @GetMapping("/searchGiaoVien")
    public ResponseEntity<Map<String, Object>> searchGiaoVien(@RequestParam String keyword, @RequestParam String token) {
        if (!giaoVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<GiaoVienDTO> results = giaoVienService.timKiemGiaoVien(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/searchGiaoVienByAdmin")
    public ResponseEntity<Map<String, Object>> searchGiaoVienByAdmin(@RequestParam String keyword, @RequestParam String token) {
        if (!giaoVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<GiaoVienDTO> results = giaoVienService.timKiemGiaoVienByAdmin(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/chuyendoigiaovien")
    public ResponseEntity<NhanVien> chuyenDoiGiaoVien(
            @RequestParam String token,
            @RequestParam Long maGV,
            @RequestParam String tenNV,
            @RequestParam String email,
            @RequestParam String sDT,
            @RequestParam Long maCV,
            @RequestParam Long taiKhoanMaTK
    ) {

        if (!nhanVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        GiaoVien giaoVien = giaoVienService.layGVTheoMa(String.valueOf(maGV), token);
        if (giaoVien == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Giáo viên không tồn tại
        }

        // Tạo đối tượng TaiKhoan và ChucVu từ tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);
        ChucVu chucVu = new ChucVu(maCV); // Assuming you have a constructor that accepts maCV

        // Tạo đối tượng NhanVien từ thông tin và các tham số
        NhanVien nhanVien = new NhanVien();
        nhanVien.setTenNV(tenNV);
        nhanVien.setEmail(email);
        nhanVien.setsDT(sDT);
        nhanVien.setChucVu(chucVu);
        nhanVien.setTaiKhoan(taiKhoan);

        NhanVien savedNhanVien = nhanVienService.luu(nhanVien); // Save the new NhanVien
        if (savedNhanVien == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Lưu nhân viên thất bại
        }


        giaoVienService.xoaGiaoVienOnly(String.valueOf(maGV), token); // Xóa GiaoVien (chỉ xóa GiaoVien, không TaiKhoan)



        // Cập nhật ma_quyen của TaiKhoan thành 3 (Nhân viên)
        TaiKhoan taiKhoanToUpdate = giaoVien.getTaiKhoan(); // Lấy TaiKhoan từ GiaoVien gốc
        if (taiKhoanToUpdate != null) {
            Quyen quyenNhanVien = quyenService.layQuyenTheoMa(3L, token); // Fetch Quyen with maQuyen = 3
            if (quyenNhanVien != null) {
                taiKhoanToUpdate.setQuyen(quyenNhanVien); // Đặt Quyen cho TaiKhoan
                taiKhoanService.luu(taiKhoanToUpdate); // Lưu lại TaiKhoan đã cập nhật
            } else {
                // Handle case where Quyen with maQuyen=3 is not found in DB
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Quyền nhân viên không tồn tại trong hệ thống!");
            }
        }

        return new ResponseEntity<>(savedNhanVien, HttpStatus.OK); // Return the created NhanVien
    }

}
