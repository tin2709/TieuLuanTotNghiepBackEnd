// MayTinhController.java
package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.service.MayTinhService;
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.format.annotation.DateTimeFormat; // No longer needed
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

// import java.util.Date; // No longer needed for params
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin // Configure CORS appropriately for production
public class MayTinhController {

    @Autowired
    private MayTinhService mayTinhService;

    // Thêm mới máy tính (CREATE)
    @PostMapping("/LuuMayTinh")
    public ResponseEntity<Object> luuMayTinh(
            @RequestParam String tenMay,
            @RequestParam String trangThai,
            @RequestParam String moTa,
            @RequestParam Long maPhong,
            @RequestParam String token) {

        try {
            PhongMay phongMayRef = new PhongMay();
            phongMayRef.setMaPhong(maPhong);

            MayTinh mayTinh = new MayTinh();
            mayTinh.setTenMay(tenMay);
            mayTinh.setTrangThai(trangThai);
            mayTinh.setMoTa(moTa);
            // ngayLapDat will be set by @PrePersist
            mayTinh.setPhongMay(phongMayRef);

            MayTinh savedMayTinh = mayTinhService.luu(mayTinh, token); // Triggers @PrePersist
            if (savedMayTinh == null && !mayTinhService.isUserLoggedIn(token)) {
                return new ResponseEntity<>("Unauthorized or Invalid Token", HttpStatus.UNAUTHORIZED);
            }
            // savedMayTinh will have ngayLapDat set, ngayCapNhat will be null
            return new ResponseEntity<>(savedMayTinh, HttpStatus.CREATED);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu máy tính: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ... (other methods remain the same)

    // Cập nhật máy tính (UPDATE)
    @PutMapping("/CapNhatMayTinh")
    public ResponseEntity<Object> capNhatMayTinh(
            @RequestParam Long maMay,
            @RequestParam String tenMay,
            @RequestParam String trangThai,
            @RequestParam String moTa,
            @RequestParam Long maPhong,
            @RequestParam String token) {

        try {
            PhongMay phongMay = mayTinhService.getPhongMayById(maPhong, token);
            if (phongMay == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy phòng máy với mã phòng: " + maPhong + " hoặc token không hợp lệ.");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            MayTinh mayTinh = mayTinhService.layMayTinhTheoMa(maMay, token);
            if (mayTinh == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy máy tính với mã máy: " + maMay + " hoặc token không hợp lệ.");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            mayTinh.setTenMay(tenMay);
            mayTinh.setTrangThai(trangThai);
            mayTinh.setMoTa(moTa);
            mayTinh.setPhongMay(phongMay);
            // ngayCapNhat will be set by @PreUpdate before the actual DB update

            MayTinh updatedMayTinh = mayTinhService.capNhatMayTinh(mayTinh, token); // Triggers @PreUpdate
            if (updatedMayTinh == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Cập nhật máy tính thất bại hoặc token không hợp lệ.");
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // updatedMayTinh will have ngayLapDat unchanged, ngayCapNhat updated
            return new ResponseEntity<>(updatedMayTinh, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật máy tính: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ... (other methods like get, delete remain the same)

    @GetMapping("/DSMayTinhtheoTrangThai")
    public List<MayTinh> getMayTinhsByTrangThai(@RequestParam String trangThai,
                                                @RequestParam String token) {

        System.out.println("Token: " + token);
        return mayTinhService.findByTrangThai(trangThai, token);
    }

    @GetMapping("/DSMayTinh")
    public List<MayTinh> layDSMayTinh(@RequestParam String token) {

        System.out.println("Token: " + token);
        return mayTinhService.layDSMayTinh(token);
    }

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

            return "Đã xoá " + maMayTinhList.size() + " máy tính";
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Có lỗi xảy ra khi xoá các máy tính: " + e.getMessage(), e);
        }
    }

    @GetMapping("/MayTinh")
    public ResponseEntity<MayTinh> layMayTinhTheoMa(@RequestParam Long maMay, @RequestParam String token) {

        System.out.println("Token: " + token);

        MayTinh mayTinh = mayTinhService.layMayTinhTheoMa(maMay, token);
        if (mayTinh != null) {
            return new ResponseEntity<>(mayTinh, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/CapNhatTrangThaiNhieuMay")
    public ResponseEntity<Object> capNhatTrangThaiNhieuMay(
            @RequestParam List<Long> maMayTinhList, // Spring tự động chuyển đổi từ "1,2,3" hoặc ?maMayTinhList=1&maMayTinhList=2...
            @RequestParam List<String> trangThaiList, // Tương tự cho trạng thái "status1,status2,status3"
            @RequestParam String token) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<MayTinh> updatedComputers = mayTinhService.capNhatTrangThaiNhieuMay(maMayTinhList, trangThaiList, token);
            response.put("message", "Cập nhật trạng thái thành công cho " + updatedComputers.size() + " máy tính.");
            response.put("updatedComputers", updatedComputers); // Tùy chọn: trả về danh sách đã cập nhật
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AccessDeniedException e) {
            Sentry.captureException(e); // Ghi log lỗi bảo mật
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        } catch (IllegalArgumentException e) {
            // Lỗi do input không hợp lệ (vd: list khác size)
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (EntityNotFoundException e) {
            // Lỗi do không tìm thấy một máy tính nào đó
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            // Các lỗi khác không mong muốn
            Sentry.captureException(e);
            response.put("message", "Có lỗi xảy ra trong quá trình cập nhật trạng thái: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}
