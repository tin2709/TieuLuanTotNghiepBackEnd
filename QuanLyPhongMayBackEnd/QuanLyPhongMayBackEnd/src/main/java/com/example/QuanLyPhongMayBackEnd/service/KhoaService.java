package com.example.QuanLyPhongMayBackEnd.service;


import com.example.QuanLyPhongMayBackEnd.entity.Khoa;
import com.example.QuanLyPhongMayBackEnd.repository.KhoaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class KhoaService {

    @Autowired
    private KhoaRepository khoaRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public Khoa layKhoaTheoMa(Long maKhoa, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Khoa khoa = null;
        Optional<Khoa> kq = khoaRepository.findById(maKhoa);
        try {
            khoa = kq.get();
            return khoa;
        } catch (Exception e) {
            return khoa;
        }
    }
    @Cacheable(value = "khoas") // Lưu trữ kết quả trong cache với tên "phongMays"
    public List<Khoa> layDSKhoa(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return khoaRepository.findAll();
    }


    @Transactional
    public void xoa(Long maKhoa, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        khoaRepository.deleteById(maKhoa);
    }

    public Khoa luu(Khoa khoa, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return khoaRepository.save(khoa);
    }

}

