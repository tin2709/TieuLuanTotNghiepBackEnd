package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // Phương thức phân trang lấy danh sách tài khoản
    public Page<TaiKhoan> layDSTaiKhoanPhanTrang(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10); // Mỗi trang 10 tài khoản
        return taiKhoanRepository.findAll(pageable);
    }
}
