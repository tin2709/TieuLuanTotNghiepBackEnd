package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;
import com.example.QuanLyPhongMayBackEnd.service.ThietBiService;
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin // Configure CORS appropriately for production
public class ThietBiController {

    @Autowired
    private ThietBiService thietBiService;

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return errorResponse;
    }

    // CREATE
    @PostMapping("/LuuThietBi")
    public ResponseEntity<Object> luuThietBi(
            @RequestParam String tenThietBi,
            @RequestParam String trangThai,
            @RequestParam String moTa,
            @RequestParam Long maPhong,
            @RequestParam Long maLoai, // Foreign key
            @RequestParam String token) {
        try {
            PhongMay phongMayRef = new PhongMay();
            phongMayRef.setMaPhong(maPhong);
            ThietBi newThietBi = new ThietBi();
            newThietBi.setTenThietBi(tenThietBi);
            newThietBi.setTrangThai(trangThai);
            newThietBi.setMoTa(moTa);
            newThietBi.setPhongMay(phongMayRef);
            // Service handles finding LoaiThietBi by maLoai and associating
            ThietBi savedThietBi = thietBiService.luuThietBi(newThietBi, maLoai, token);
            return new ResponseEntity<>(savedThietBi, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) { // Catch if LoaiThietBi not found
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>(createErrorResponse("Lỗi khi lưu thiết bị: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ALL
    @GetMapping("/DSThietBi")
    public List<ThietBi> layDSMayTinh(@RequestParam String token) {

        System.out.println("Token: " + token);
        return thietBiService.layDSThietBi(token);
    }

    // READ ONE
    @GetMapping("/ThietBi")
    public ResponseEntity<?> layThietBiTheoMa(@RequestParam Long maThietBi, @RequestParam String token) {
        try {
            ThietBi thietBi = thietBiService.layThietBiTheoMa(maThietBi, token);
            return new ResponseEntity<>(thietBi, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>(createErrorResponse("Lỗi khi lấy thiết bị: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE
    @PutMapping("/CapNhatThietBi")
    public ResponseEntity<Object> capNhatThietBi(
            @RequestParam Long maThietBi, // ID of the device to update
            @RequestParam String tenThietBi,
            @RequestParam String trangThai,
            @RequestParam String moTa,
            @RequestParam Long maLoai, // New foreign key
            @RequestParam String token) {
        try {
            ThietBi thietBiDetails = new ThietBi();
            thietBiDetails.setMaThietBi(maThietBi); // Set ID for lookup
            thietBiDetails.setTenThietBi(tenThietBi);
            thietBiDetails.setTrangThai(trangThai);
            thietBiDetails.setMoTa(moTa);
            // Pass maLoai separately for service to handle association
            ThietBi updatedThietBi = thietBiService.capNhatThietBi(thietBiDetails, maLoai, token);
            return new ResponseEntity<>(updatedThietBi, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) { // Catch if ThietBi or LoaiThietBi not found
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>(createErrorResponse("Lỗi khi cập nhật thiết bị: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE ONE
    @DeleteMapping("/XoaThietBi")
    public ResponseEntity<Object> xoaThietBi(@RequestParam Long maThietBi, @RequestParam String token) {
        try {
            thietBiService.xoaThietBi(maThietBi, token);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Đã xoá thiết bị với mã " + maThietBi);
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>(createErrorResponse("Lỗi khi xoá thiết bị: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE MANY
    @DeleteMapping("/XoaNhieuThietBi")
    public ResponseEntity<Object> xoaNhieuThietBi(
            @RequestParam List<Long> maThietBiList,
            @RequestParam String token) {
        if (maThietBiList == null || maThietBiList.isEmpty()) {
            return new ResponseEntity<>(createErrorResponse("Danh sách mã thiết bị không được rỗng."), HttpStatus.BAD_REQUEST);
        }
        try {
            thietBiService.xoaNhieuThietBi(maThietBiList, token);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Đã xoá " + maThietBiList.size() + " thiết bị.");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>(createErrorResponse("Lỗi khi xoá nhiều thiết bị: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Example: Add endpoint similar to MayTinhController's getByTrangThai if needed
    @GetMapping("/DSThietBitheoTrangThai")
    public ResponseEntity<?> getThietBiByTrangThai(@RequestParam String trangThai,
                                                   @RequestParam String token) {
        try {
            List<ThietBi> dsThietBi = thietBiService.findByTrangThai(trangThai, token);
            // Check if service returns null due to token, though throwing SecurityException is preferred
            // if (dsThietBi == null) {
            //    return new ResponseEntity<>(createErrorResponse("Unauthorized: Invalid Token"), HttpStatus.UNAUTHORIZED);
            // }
            return new ResponseEntity<>(dsThietBi, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>(createErrorResponse("Lỗi khi lấy danh sách thiết bị theo trạng thái: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/DSThietBiTheoPhong")
    public ResponseEntity<?> getThietBiByPhongMay(
            @RequestParam Long maPhong,
            @RequestParam(name = "maLoai", required = false) Long maLoai, // Parameter name and type changed
            @RequestParam String token) {
        try {
            // Call the updated service method, passing maLoai
            // The service method now returns List<ThietBi>
            List<ThietBi> dsThietBi = thietBiService.layDSThietBiTheoPhong(maPhong, maLoai, token);

            // Return 200 OK with the list (which might be empty)
            return new ResponseEntity<>(dsThietBi, HttpStatus.OK); // Variable name dsThietBiDTO changed to dsThietBi for clarity

        } catch (SecurityException e) {
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) { // Catch if the service validates room and it's not found
            return new ResponseEntity<>(createErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            // Updated error message construction
            String filterInfo = (maLoai != null) ? " và mã loại " + maLoai : "";
            return new ResponseEntity<>(createErrorResponse("Lỗi khi lấy danh sách thiết bị theo phòng" + filterInfo + ": " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/CapNhatTrangThaiNhieuThietBi")
    public ResponseEntity<Object> capNhatTrangThaiNhieuThietBi(
            @RequestParam List<Long> maThietBiList, // List of device IDs
            @RequestParam List<String> trangThaiList, // List of corresponding statuses
            @RequestParam String token) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Call the corresponding service method (needs to be created in ThietBiService)
            List<ThietBi> updatedDevices = thietBiService.capNhatTrangThaiNhieuThietBi(maThietBiList, trangThaiList, token);

            // Success response
            response.put("message", "Cập nhật trạng thái thành công cho " + updatedDevices.size() + " thiết bị.");
            response.put("updatedDevices", updatedDevices); // Optionally return the updated list
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (SecurityException e) { // Using SecurityException consistent with ThietBiService
            // Sentry.captureException(e); // Keep Sentry logging
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        } catch (IllegalArgumentException e) {
            // Error due to invalid input (e.g., lists have different sizes)
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (EntityNotFoundException e) {
            // Error because some device ID was not found
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            // Other unexpected errors
            Sentry.captureException(e);
            response.put("message", "Có lỗi xảy ra trong quá trình cập nhật trạng thái thiết bị: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

}