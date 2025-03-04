package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.PhongMayDTO;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.service.PhongMayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class PhongMayController {

    @Autowired
    private PhongMayService phongMayService;

    @PostMapping("/LuuPhongMay")
    public PhongMay luu(
            @RequestParam String tenPhong,
            @RequestParam int soMay,
            @RequestParam String moTa,
            @RequestParam String trangThai,
            @RequestParam Long maTang,
            @RequestParam String token) {

        // Create a PhongMay object from the request parameters
        PhongMay phongMay = new PhongMay();
        phongMay.setTenPhong(tenPhong);
        phongMay.setSoMay(soMay);
        phongMay.setMoTa(moTa);
        phongMay.setTrangThai(trangThai);

        // Assuming the Tang entity is being set based on maTang (you may need a TangService to fetch Tang)
        Tang tang = new Tang();  // This would need to be retrieved from the Tang entity based on maTang
        tang.setMaTang(maTang);  // Assuming Tang has a setMaTang method
        phongMay.setTang(tang);

        return phongMayService.luu(phongMay,token);
    }

    @GetMapping("/DSPhongMay")
    public List<PhongMay> layDSPhongMay(@RequestParam String token) {
        // Handle token validation if necessary
        return phongMayService.layDSPhongMay(token);
    }

    @GetMapping("/DSPhongMaytheoTrangThai/{trangThai}")
    public List<PhongMay> getPhongMaysByTrangThai(@PathVariable String trangThai, @RequestParam String token) {
        // Handle token validation if necessary
        return phongMayService.findByTrangThai(trangThai,token);
    }

    @GetMapping("/PhongMay/{maPhong}")
    public PhongMay layPhongMayTheoMa(@PathVariable Long maPhong, @RequestParam String token) {
        // Handle token validation if necessary
        return phongMayService.layPhongMayTheoMa(maPhong,token);
    }

    @DeleteMapping("/XoaPhongMay/{maPhong}")
    @Transactional
    public String xoa(@PathVariable Long maPhong, @RequestParam String token) {
        // Handle token validation if necessary
        phongMayService.xoa(maPhong,token);
        return "Đã xoá " + maPhong;
    }

    @PutMapping("/CapNhatPhongMay/{maPhong}")
    public PhongMay capNhatTheoMa(
            @PathVariable Long maPhong,
            @RequestParam String tenPhong,
            @RequestParam int soMay,
            @RequestParam String moTa,
            @RequestParam String trangThai,
            @RequestParam Long maTang,
            @RequestParam String token) {

        // Create a PhongMay object from the request parameters
        PhongMay phongMay = new PhongMay();
        phongMay.setTenPhong(tenPhong);
        phongMay.setSoMay(soMay);
        phongMay.setMoTa(moTa);
        phongMay.setTrangThai(trangThai);

        // Assuming the Tang entity is being set based on maTang (you may need a TangService to fetch Tang)
        Tang tang = new Tang();  // This would need to be retrieved from the Tang entity based on maTang
        tang.setMaTang(maTang);  // Assuming Tang has a setMaTang method
        phongMay.setTang(tang);

        return phongMayService.capNhatTheoMa(maPhong, phongMay,token);
    }
    @GetMapping("/searchPhongMay")
    public ResponseEntity<Map<String, Object>> searchPhongMay(@RequestParam String keyword, @RequestParam String token) {
        if (!phongMayService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<PhongMayDTO> results = phongMayService.timKiemPhongMay(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
