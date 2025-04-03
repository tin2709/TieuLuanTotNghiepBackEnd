package com.example.QuanLyPhongMayBackEnd.controller;

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
            @RequestParam Long maLoai, // Foreign key
            @RequestParam String token) {
        try {
            ThietBi newThietBi = new ThietBi();
            newThietBi.setTenThietBi(tenThietBi);
            newThietBi.setTrangThai(trangThai);
            newThietBi.setMoTa(moTa);
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
}