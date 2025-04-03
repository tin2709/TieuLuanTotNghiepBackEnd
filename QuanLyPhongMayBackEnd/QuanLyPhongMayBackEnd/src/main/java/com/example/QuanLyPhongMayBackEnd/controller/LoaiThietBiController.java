package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.LoaiThietBi;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.service.LoaiThietBiService;
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
public class LoaiThietBiController {

    @Autowired
    private LoaiThietBiService loaiThietBiService;

    // CREATE
    @PostMapping("/LuuLoaiThietBi")
    public ResponseEntity<Object> luuLoaiThietBi(
            @RequestParam String tenLoai,
            @RequestParam String token) {
        try {
            LoaiThietBi newLoai = new LoaiThietBi();
            newLoai.setTenLoai(tenLoai);
            LoaiThietBi savedLoai = loaiThietBiService.luuLoaiThietBi(newLoai, token);
            return new ResponseEntity<>(savedLoai, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lưu loại thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ALL
    @GetMapping("/DSLoaiThietBi")
    public List<LoaiThietBi> layDSLoaiThietBi(@RequestParam String token) {

        System.out.println("Token: " + token);
        return loaiThietBiService.layDSLoaiThietBi(token);
    }

    // READ ONE
    @GetMapping("/LoaiThietBi")
    public ResponseEntity<?> layLoaiThietBiTheoMa(@RequestParam Long maLoai, @RequestParam String token) {
        try {
            LoaiThietBi loai = loaiThietBiService.layLoaiThietBiTheoMa(maLoai, token);
            return new ResponseEntity<>(loai, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi lấy loại thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE
    @PutMapping("/CapNhatLoaiThietBi")
    public ResponseEntity<Object> capNhatLoaiThietBi(
            @RequestParam Long maLoai,
            @RequestParam String tenLoai,
            @RequestParam String token) {
        try {
            LoaiThietBi loaiDetails = new LoaiThietBi();
            loaiDetails.setMaLoai(maLoai); // Set the ID for lookup
            loaiDetails.setTenLoai(tenLoai);

            LoaiThietBi updatedLoai = loaiThietBiService.capNhatLoaiThietBi(loaiDetails, token);
            return new ResponseEntity<>(updatedLoai, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi khi cập nhật loại thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE ONE
    @DeleteMapping("/XoaLoaiThietBi")
    public ResponseEntity<String> xoaLoaiThietBi(@RequestParam Long maLoai, @RequestParam String token) {
        try {
            loaiThietBiService.xoaLoaiThietBi(maLoai, token);
            return new ResponseEntity<>("Đã xoá loại thiết bị với mã " + maLoai, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) { // Catch specific state exceptions from service
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
        }
        catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>("Lỗi khi xoá loại thiết bị: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE MANY
    @DeleteMapping("/XoaNhieuLoaiThietBi")
    public ResponseEntity<String> xoaNhieuLoaiThietBi(
            @RequestParam List<Long> maLoaiList,
            @RequestParam String token) {
        if (maLoaiList == null || maLoaiList.isEmpty()) {
            return new ResponseEntity<>("Danh sách mã loại thiết bị không được rỗng.", HttpStatus.BAD_REQUEST);
        }
        try {
            loaiThietBiService.xoaNhieuLoaiThietBi(maLoaiList, token);
            return new ResponseEntity<>("Đã xoá " + maLoaiList.size() + " loại thiết bị.", HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("Lỗi khi xoá nhiều loại thiết bị: " + e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            Sentry.captureException(e);
            return new ResponseEntity<>("Lỗi khi xoá nhiều loại thiết bị: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}