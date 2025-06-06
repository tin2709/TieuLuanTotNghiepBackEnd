package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.CaThucHanhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.CaThucHanhService;
import io.sentry.Sentry; // Assuming you use Sentry
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional; // For XoaNhieu


import java.util.*;

@RestController
@CrossOrigin // Configure CORS appropriately for production
public class CaThucHanhController {

    @Autowired
    private CaThucHanhService caThucHanhService;

    // API lưu CaThucHanh
    @PostMapping("/LuuCaThucHanh")
    public ResponseEntity<Object> luu(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String tenCa,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam int buoiSo,
            @RequestParam Long maGiaoVien,
            @RequestParam Long maPhong,
            @RequestParam Long maMon,
            @RequestParam String token) {
        Map<String, String> errorResponse = new HashMap<>();
        try {
            if (!caThucHanhService.isUserLoggedIn(token)) {
                errorResponse.put("message", "Token không hợp lệ hoặc đã hết hạn.");
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }

            CaThucHanh caThucHanh = new CaThucHanh(null, ngayThucHanh, tenCa, tietBatDau, tietKetThuc, buoiSo,
                    null, null, null);

            CaThucHanh savedCaThucHanh = caThucHanhService.luu(caThucHanh, token, maGiaoVien, maPhong, maMon);

            if (savedCaThucHanh == null) {
                // This could happen if related entities were not found or other service-side validation failed
                errorResponse.put("message", "Lưu ca thực hành thất bại. Vui lòng kiểm tra lại thông tin mã giáo viên, mã phòng, mã môn.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(savedCaThucHanh, HttpStatus.CREATED);
        } catch (AccessDeniedException e) {
            errorResponse.put("message", "Token không hợp lệ hoặc bạn không có quyền thực hiện hành động này.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Sentry.captureException(e);
            errorResponse.put("message", "Có lỗi xảy ra khi lưu ca thực hành: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API lấy danh sách CaThucHanh
    @GetMapping("/DSCaThucHanh")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanh(@RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.UNAUTHORIZED);
        }
        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanh(token);
        if (dsCaThucHanh == null || dsCaThucHanh.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API lấy danh sách CaThucHanh theo ngày
    @GetMapping("/DSCaThucHanhTheoNgay/{ngayThucHanh}")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoNgay(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String token) {

        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.UNAUTHORIZED);
        }

        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoNgay(ngayThucHanh, token);
        if (dsCaThucHanh == null || dsCaThucHanh.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API lấy CaThucHanh theo mã
    @GetMapping("/CaThucHanh/{maCaThucHanh}")
    public ResponseEntity<CaThucHanh> layCaThucHanhTheoMa(@PathVariable Long maCaThucHanh, @RequestParam String token) {
        CaThucHanh caThucHanh;
        try {
            caThucHanh = caThucHanhService.layCaThucHanhTheoMa(maCaThucHanh, token);
            if (caThucHanh == null) {
                // Could be due to token or not found
                // Service method now throws exceptions for clarity
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(caThucHanh, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // API lấy danh sách CaThucHanh theo môn học
    @GetMapping("/DSCaThucHanhTheoMonHoc/{maMon}")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoMonHoc(@PathVariable Long maMon, @RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.UNAUTHORIZED);
        }

        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMonHoc(maMon, token);

        if (dsCaThucHanh == null || dsCaThucHanh.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK); // Or NO_CONTENT
        }

        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // API cập nhật CaThucHanh theo mã
    @PutMapping("/CapNhatCaThucHanh")
    public ResponseEntity<Object> capNhat(
            @RequestParam Long maCaThucHanh,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayThucHanh,
            @RequestParam String tenCa,
            @RequestParam int tietBatDau,
            @RequestParam int tietKetThuc,
            @RequestParam int buoiSo,
            @RequestParam Long maGiaoVien, // Frontend sends ID, service will fetch entity
            @RequestParam Long maPhong,   // Frontend sends ID
            @RequestParam Long maMon,     // Frontend sends ID
            @RequestParam String token) {
        Map<String, String> errorResponse = new HashMap<>();
        try {

            CaThucHanh caThucHanhDetails = new CaThucHanh();
            caThucHanhDetails.setMaCa(maCaThucHanh);
            caThucHanhDetails.setNgayThucHanh(ngayThucHanh);
            caThucHanhDetails.setTenCa(tenCa);
            caThucHanhDetails.setTietBatDau(tietBatDau);
            caThucHanhDetails.setTietKetThuc(tietKetThuc);
            caThucHanhDetails.setBuoiSo(buoiSo);
            // The service will handle setting GiaoVien, PhongMay, MonHoc by their IDs

            CaThucHanh updatedCaThucHanh = caThucHanhService.capNhat(caThucHanhDetails, token, maGiaoVien, maPhong, maMon);
            return new ResponseEntity<>(updatedCaThucHanh, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            errorResponse.put("message", "Token không hợp lệ hoặc bạn không có quyền thực hiện hành động này.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Sentry.captureException(e);
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật ca thực hành: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API xóa CaThucHanh
    @DeleteMapping("/XoaCaThucHanh/{maCaThucHanh}") // Changed to PathVariable for consistency
    public ResponseEntity<Object> xoa(@PathVariable Long maCaThucHanh, @RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        try {
            caThucHanhService.xoa(maCaThucHanh, token); // Service will throw exceptions if needed
            response.put("message", "Đã xoá ca thực hành với mã " + maCaThucHanh);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            response.put("message", "Token không hợp lệ hoặc không có quyền xóa.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Lỗi khi xóa ca thực hành: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchCaThucHanh")
    public ResponseEntity<Map<String, Object>> timKiemCaThucHanh(@RequestParam String keyword, @RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!caThucHanhService.isUserLoggedIn(token)) {
                response.put("message", "Token không hợp lệ.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            if (keyword == null || keyword.trim().isEmpty() || !keyword.contains(":")) {
                response.put("message", "Keyword không hợp lệ. Định dạng mong muốn: 'ten_cot:gia_tri'.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<CaThucHanhDTO> results = caThucHanhService.timKiemCaThucHanh(keyword, token);

            response.put("results", results);
            response.put("totalCount", results.size());

            return new ResponseEntity<>(response, HttpStatus.OK); // Return OK even if results are empty

        } catch (IllegalArgumentException e) {
            response.put("message", "Định dạng giá trị tìm kiếm không hợp lệ: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Lỗi tìm kiếm: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/DSCaThucHanhTheoGiaoVienTen")
    public ResponseEntity<List<CaThucHanh>> layDSCaThucHanhTheoTenGiaoVien(
            @RequestParam String hoTenGiaoVien,
            @RequestParam String token) {
        if (!caThucHanhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.UNAUTHORIZED);
        }

        List<CaThucHanh> dsCaThucHanh = caThucHanhService.layDSCaThucHanhTheoTenGiaoVien(hoTenGiaoVien, token);

        if (dsCaThucHanh == null || dsCaThucHanh.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK); // Or NO_CONTENT
        }

        return new ResponseEntity<>(dsCaThucHanh, HttpStatus.OK);
    }

    // ========== NEW APIS ==========

    @DeleteMapping("/XoaNhieuCaThucHanh")
    @Transactional // Ensures atomicity for multiple deletes if service method doesn't already handle it with Spring @Transactional
    public ResponseEntity<Object> xoaNhieuCaThucHanh(@RequestParam List<Long> maCaThucHanhList, @RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        try {
            int soLuongDaXoa = caThucHanhService.xoaNhieuCaThucHanh(maCaThucHanhList, token);
            response.put("message", "Đã xoá thành công " + soLuongDaXoa + " ca thực hành.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            response.put("message", "Token không hợp lệ hoặc không có quyền thực hiện hành động này.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            response.put("message", e.getMessage() + " Một vài ca thực hành có thể chưa được xóa.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Có lỗi xảy ra khi xoá các ca thực hành: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchCaThucHanhByAdmin")
    public ResponseEntity<Map<String, Object>> searchCaThucHanhByAdmin(
            @RequestParam String keyword,
            @RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!caThucHanhService.isUserLoggedIn(token)) {
                response.put("message", "Token không hợp lệ.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            if (keyword == null || keyword.trim().isEmpty()) {
                response.put("message", "Search parameter 'keyword' không được để trống.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<CaThucHanhDTO> results = caThucHanhService.timKiemCaThucHanhByAdmin(keyword, token);

            response.put("results", results);
            response.put("size", results.size()); // Consistent with MonHocController searchMonHocByAdmin

            // Return OK even if results are empty, frontend can handle display.
            // If results is null (e.g., service error before query), it would be caught by general Exception.
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AccessDeniedException e) {
            response.put("message", "Token không hợp lệ hoặc bạn không có quyền thực hiện hành động này.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            // This can be thrown by the service for invalid search syntax or values
            response.put("message", "Lỗi cú pháp tìm kiếm hoặc giá trị không hợp lệ: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Có lỗi xảy ra trong quá trình tìm kiếm ca thực hành: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}