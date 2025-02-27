package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.repository.CaThucHanhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CaThucHanhService {
    @Autowired
    private CaThucHanhRepository caThucHanhRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Method to handle user login validation before performing any operation
    private Map<String, Object> checkTokenAndReturnResponse(String token) {
        Map<String, Object> response = new HashMap<>();
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to perform this action.");
        }
        return response;
    }

    public CaThucHanh layCaThucHanhTheoMa(Long maCaThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        CaThucHanh caThucHanh = null;
        Optional<CaThucHanh> kq = caThucHanhRepository.findById(maCaThucHanh);
        try {
            caThucHanh = kq.get();
        } catch (Exception e) {
            // Handle exception as needed
        }
        return caThucHanh;
    }

    public List<CaThucHanh> layDSCaThucHanhTheoNgay(Date ngayThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findByNgayThucHanh(ngayThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanh(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findAll();
    }

    public void xoa(Long maCaThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return;
        }
        caThucHanhRepository.deleteById(maCaThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMonHoc(Long maMon, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findByMonHoc_MaMon(maMon);
    }

    public CaThucHanh capNhat(CaThucHanh caThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.save(caThucHanh);
    }

    public CaThucHanh luu(CaThucHanh caThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.save(caThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMaPhong(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findByPhongMay_MaPhong(maPhong);
    }
}
