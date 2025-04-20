package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.NhanVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.*;
import com.example.QuanLyPhongMayBackEnd.service.GiaoVienService;
import com.example.QuanLyPhongMayBackEnd.service.NhanVienService;
import com.example.QuanLyPhongMayBackEnd.service.QuyenService;
import com.example.QuanLyPhongMayBackEnd.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private GiaoVienService giaoVienService;
    @Autowired
    private QuyenService quyenService;

    // API phân trang lấy danh sách nhân viên
    @GetMapping("/DSNhanVienPhanTrang")
    public Page<NhanVien> layDSNVPhanTrang(@RequestParam int pageNumber, @RequestParam String token) {
        return nhanVienService.layDSNVPhanTrang(pageNumber, token);
    }

    // API lấy danh sách nhân viên
    @GetMapping("/DSNhanVien")
    public List<NhanVien> layDSNV(@RequestParam String token) {
        return nhanVienService.layDSNV(token);
    }

    // API lấy nhân viên theo mã
    @GetMapping("/NhanVien")
    public NhanVien layNVTheoMa(@RequestParam Long maNV, @RequestParam String token) {
        return nhanVienService.layNVTheoMa(maNV, token);
    }

    // API lưu nhân viên
    @PostMapping("/LuuNhanVien")
    public NhanVien luu(
            @RequestParam String tenNV,
            @RequestParam String email,
            @RequestParam String sDT,
            @RequestParam Long maCV,
            @RequestParam Long taiKhoanMaTK) {
        // Tạo đối tượng NhanVien từ các tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);  // Giả sử TaiKhoan có constructor nhận maTK
        ChucVu chucVu = new ChucVu(maCV);
        NhanVien nhanVien = new NhanVien();
        nhanVien.setTenNV(tenNV);
        nhanVien.setEmail(email);
        nhanVien.setsDT(sDT);
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVien.setChucVu(chucVu);


        return nhanVienService.luu(nhanVien);
    }

    // API xóa nhân viên theo mã nhân viên
    @DeleteMapping("/XoaNhanVien/{maNV}")
    public String xoa(@PathVariable String maNV, @RequestParam String token) {
        nhanVienService.xoa(maNV, token);
        return "Đã xoá nhân viên " + maNV;
    }

    @GetMapping("/searchNhanVien")
    public ResponseEntity<Map<String, Object>> searchNhanVien(@RequestParam String keyword, @RequestParam String token) {
        if (!nhanVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<NhanVienDTO> results = nhanVienService.timKiemNhanVien(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchNhanVienByAdmin")
    public ResponseEntity<Map<String, Object>> searchNhanVienByAdmin(@RequestParam String keyword, @RequestParam String token) {
        // Assuming nhanVienService.isUserLoggedIn(token) exists, or adjust authentication logic as needed
        if (!nhanVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<NhanVienDTO> results = nhanVienService.timKiemNhanVienByAdmin(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/chuyendoinhanvien")
    public ResponseEntity<GiaoVien> chuyenDoiNhanVien(
            @RequestParam String token,
            @RequestParam Long maNV,
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String email,
            @RequestParam String hocVi,
            @RequestParam Long taiKhoanMaTK,
            @RequestParam Long khoaMaKhoa
            // Removed @RequestParam Long maQuyenGiaoVien
    ) {

        if (!nhanVienService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        NhanVien nhanVien = nhanVienService.layNVTheoMa(maNV, token);
        if (nhanVien == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Nhân viên không tồn tại
        }

        // Tạo đối tượng TaiKhoan và Khoa từ tham số
        TaiKhoan taiKhoan = new TaiKhoan(taiKhoanMaTK);
        Khoa khoa = new Khoa(khoaMaKhoa);

        // Tạo đối tượng GiaoVien từ thông tin và các tham số, CÓ liên quan đến TaiKhoan
        GiaoVien giaoVien = new GiaoVien();
        giaoVien.setHoTen(hoTen);
        giaoVien.setSoDienThoai(soDienThoai);
        giaoVien.setEmail(email);
        giaoVien.setHocVi(hocVi);
        giaoVien.setTaiKhoan(taiKhoan);
        giaoVien.setKhoa(khoa);

        GiaoVien savedGiaoVien = giaoVienService.luu(giaoVien);
        if (savedGiaoVien == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Lưu giáo viên thất bại
        }

        nhanVienService.xoaNhanVienOnly(String.valueOf(maNV), token); // Xóa nhân viên (chỉ xóa NhanVien, không TaiKhoan)
        // Cập nhật ma_quyen của TaiKhoan thành 2 (Giáo viên)
        TaiKhoan taiKhoanToUpdate = nhanVien.getTaiKhoan(); // Lấy TaiKhoan từ NhanVien gốc
        if (taiKhoanToUpdate != null) {
            // Fetch Quyen object from database using QuyenService
            Quyen quyenGiaoVien = quyenService.layQuyenTheoMa(2L, token); // Fetch Quyen with maQuyen = 2
            if (quyenGiaoVien != null) {
                taiKhoanToUpdate.setQuyen(quyenGiaoVien); // Đặt Quyen cho TaiKhoan
                taiKhoanService.luu(taiKhoanToUpdate); // Lưu lại TaiKhoan đã cập nhật
            } else {
                // Handle case where Quyen with maQuyen=2 is not found in DB (unlikely, but good practice)
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Quyền giáo viên không tồn tại trong hệ thống!");
            }
        }



        return new ResponseEntity<>(savedGiaoVien, HttpStatus.OK);
    }
}
