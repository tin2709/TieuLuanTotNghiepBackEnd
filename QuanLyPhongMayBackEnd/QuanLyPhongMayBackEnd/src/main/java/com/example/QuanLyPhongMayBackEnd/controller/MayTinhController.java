// MayTinhController.java
package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.MayTinhDTO;
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
    public ResponseEntity<List<MayTinhDTO>> layDSMayTinh(@RequestParam String token) {
        try {
            // Call the service method that returns DTOs
            List<MayTinhDTO> mayTinhDTOs = mayTinhService.layDSMayTinhDTO(token);

            // Check if the service returned empty list due to invalid token or no data
            if (mayTinhDTOs.isEmpty()) {
                // You could check the token validity separately here if needed
                // to distinguish between "no data" and "unauthorized"
                // For now, assume empty list means either no data or handled unauthorized case
                return ResponseEntity.ok(mayTinhDTOs); // Return 200 OK with empty list
            }

            return ResponseEntity.ok(mayTinhDTOs); // Return 200 OK with the list of DTOs
        } catch (Exception e) {
            // Log the exception
            // logger.error("Error fetching MayTinh list: {}", e.getMessage(), e);
            // Optionally return an error response object
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 error
        }
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
    @GetMapping("/MayTinh") // Keep endpoint name as requested
    public ResponseEntity<MayTinhDTO> layMayTinhTheoMa(@RequestParam Long maMay, @RequestParam String token) {
        // No need for System.out.println in production code
        // System.out.println("Token: " + token);

        // Call the service method that returns a DTO
        MayTinhDTO mayTinhDto = mayTinhService.layMayTinhDTOTheoMa(maMay, token);

        if (mayTinhDto != null) {
            // If DTO is returned, send 200 OK response with DTO body
            return new ResponseEntity<>(mayTinhDto, HttpStatus.OK);
        } else {
            // If service returns null (either not found or potentially unauthorized)
            // Return 404 Not Found - Service should ideally throw exceptions
            // for clearer distinction, but based on current service logic, null means failure.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
