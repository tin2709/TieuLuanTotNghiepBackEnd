package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TokenRepository tokenRepository;
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

    // Tìm tài khoản theo username (dùng trong login)
    public Optional<TaiKhoan> timTaiKhoanByUsername(String username) {
        return taiKhoanRepository.findByTenDangNhap(username); // Giả sử có phương thức này trong TaiKhoanRepository
    }
}
