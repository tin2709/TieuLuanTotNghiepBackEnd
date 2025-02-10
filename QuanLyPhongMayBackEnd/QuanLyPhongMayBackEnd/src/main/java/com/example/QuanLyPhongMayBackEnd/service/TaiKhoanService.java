package com.example.QuanLyPhongMayBackEnd.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Xóa tài khoản theo mã
    public void xoa(String maTK) {
        taiKhoanRepository.deleteById(maTK);
    }

    // Lưu tài khoản
    public TaiKhoan luu(TaiKhoan taiKhoan) {
        return taiKhoanRepository.save(taiKhoan);
    }
}

