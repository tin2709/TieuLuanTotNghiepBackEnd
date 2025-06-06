package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.MonHocDTO;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.service.MonHocService;
import io.sentry.Sentry; // Giả sử bạn dùng Sentry như MayTinhController
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Cho trường hợp token không hợp lệ
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional; // Cho XoaNhieuMonHoc


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin // Configure CORS appropriately for production
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    // API lưu môn học mới
    @PostMapping("/LuuMonHoc")
    public ResponseEntity<Object> luu(@RequestParam String tenMon,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
                                      @RequestParam int soBuoi,
                                      @RequestParam String token) {
        try {
            MonHoc monHoc = new MonHoc();
            monHoc.setTenMon(tenMon);
            monHoc.setNgayBatDau(ngayBatDau);
            monHoc.setNgayKetThuc(ngayKetThuc);
            monHoc.setSoBuoi(soBuoi);

            MonHoc savedMonHoc = monHocService.luu(monHoc, token);
            if (savedMonHoc == null) { // Service có thể trả về null nếu token không hợp lệ
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Lưu môn học thất bại hoặc token không hợp lệ.");
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // Hoặc BAD_REQUEST tùy logic
            }
            return new ResponseEntity<>(savedMonHoc, HttpStatus.CREATED);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu môn học: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API cập nhật môn học (MỚI)
    @PutMapping("/CapNhatMonHoc") // Sử dụng PathVariable cho maMon giống frontend
    public ResponseEntity<Object> capNhatMonHoc(
            @RequestParam Long maMon, // Lấy maMon từ path
            @RequestParam String tenMon,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
            @RequestParam int soBuoi,
            @RequestParam String token) {
        try {
            MonHoc monHocDetails = new MonHoc();
            monHocDetails.setMaMon(maMon); // Gán mã môn để service tìm và cập nhật
            monHocDetails.setTenMon(tenMon);
            monHocDetails.setNgayBatDau(ngayBatDau);
            monHocDetails.setNgayKetThuc(ngayKetThuc);
            monHocDetails.setSoBuoi(soBuoi);

            MonHoc updatedMonHoc = monHocService.capNhatMonHoc(monHocDetails, token);
            // Service sẽ ném EntityNotFoundException nếu không tìm thấy
            // Service sẽ trả về null nếu token không hợp lệ (hoặc ném AccessDeniedException)

            return new ResponseEntity<>(updatedMonHoc, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token không hợp lệ hoặc bạn không có quyền thực hiện hành động này.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) { // Ví dụ: ngày kết thúc trước ngày bắt đầu
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật môn học: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API lấy danh sách môn học
    @GetMapping("/DSMonHoc")
    public ResponseEntity<List<MonHoc>> layDSMonHoc(@RequestParam String token) {
        List<MonHoc> dsMonHoc = monHocService.layDSMonHoc(token);
        if (dsMonHoc == null) { // Service trả về null nếu token không hợp lệ
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (dsMonHoc.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dsMonHoc, HttpStatus.OK);
    }

    // API xóa môn học theo mã môn
    @DeleteMapping("/XoaMonHoc/{maMon}")
    public ResponseEntity<Object> xoa(@PathVariable Long maMon, @RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        try {
            monHocService.xoa(maMon, token); // Service sẽ ném lỗi nếu cần
            response.put("message", "Đã xoá môn học với mã " + maMon);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            response.put("message", "Token không hợp lệ hoặc không có quyền xóa.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Lỗi khi xóa môn học: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API xóa nhiều môn học (MỚI)
    @DeleteMapping("/XoaNhieuMonHoc")
    @Transactional // Đảm bảo tính nhất quán
    public ResponseEntity<Object> xoaNhieuMonHoc(@RequestParam List<Long> maMonList, @RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        try {
            // Service sẽ kiểm tra token và xử lý logic xóa
            int soLuongDaXoa = monHocService.xoaNhieuMonHoc(maMonList, token);
            response.put("message", "Đã xoá thành công " + soLuongDaXoa + " môn học.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AccessDeniedException e) {
            response.put("message", "Token không hợp lệ hoặc không có quyền thực hiện hành động này.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) { // Nếu một trong các môn học không tìm thấy
            response.put("message", e.getMessage() + " Một vài môn học có thể chưa được xóa.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Có lỗi xảy ra khi xoá các môn học: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // API lấy môn học theo mã môn
    @GetMapping("/MonHoc/{maMon}")
    public ResponseEntity<MonHoc> layMonHocTheoMa(@PathVariable Long maMon, @RequestParam String token) {
        MonHoc monHoc = monHocService.layMonHocTheoMa(maMon, token);
        if (monHoc == null) {
            // Có thể token không hợp lệ hoặc không tìm thấy môn học
            // Để phân biệt rõ hơn, service nên ném exception
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Hoặc UNAUTHORIZED nếu biết chắc do token
        }
        return new ResponseEntity<>(monHoc, HttpStatus.OK);
    }

    @GetMapping("/searchMonHoc")
    public ResponseEntity<Map<String, Object>> searchMonHoc(@RequestParam String keyword, @RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!monHocService.isUserLoggedIn(token)) {
                response.put("message", "Token không hợp lệ.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            if (keyword == null || keyword.trim().isEmpty() || !keyword.contains(":")) {
                response.put("message", "Keyword không hợp lệ. Định dạng mong muốn: 'ten_cot:gia_tri'.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<MonHocDTO> results = monHocService.timKiemMonHoc(keyword, token);

            response.put("results", results);
            response.put("totalCount", results.size()); // Sửa lại thành totalCount để nhất quán

            if (results.isEmpty()) {
                // Vẫn trả về OK với results rỗng, totalCount = 0
                // Frontend sẽ hiển thị "Không có dữ liệu"
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) { // Bắt lỗi từ việc parse Date hoặc Integer trong service
            response.put("message", "Định dạng giá trị tìm kiếm không hợp lệ cho cột: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Sentry.captureException(e);
            response.put("message", "Lỗi tìm kiếm: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // NEW API based on searchGhiChuMayTinhByAdmin
    @GetMapping("/searchMonHocByAdmin")
    public ResponseEntity<Map<String, Object>> searchMonHocByAdmin(
            @RequestParam String keyword,
            @RequestParam String token) {

        if (!monHocService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Search parameter cannot be empty.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<MonHocDTO> results = monHocService.timKiemMonHocByAdmin(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        HttpStatus status = (results == null || results.isEmpty()) ? HttpStatus.NO_CONTENT : HttpStatus.OK;

        return new ResponseEntity<>(response, status);
    }
}