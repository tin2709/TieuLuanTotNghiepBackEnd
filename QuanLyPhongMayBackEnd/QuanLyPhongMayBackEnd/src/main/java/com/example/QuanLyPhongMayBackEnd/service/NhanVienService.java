package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.NhanVien;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.NhanVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public NhanVien layNVTheoMa(String maNV,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        NhanVien nhanVien = null;
        Optional<NhanVien> kq = nhanVienRepository.findById(maNV);
        try {
            nhanVien = kq.get();
            return nhanVien;
        } catch (Exception e) {
            return nhanVien;
        }
    }

    public List<NhanVien> layDSNV(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return nhanVienRepository.findAll();
    }

    @Transactional
    public void xoa(String maNV,String token) {
        if (!isUserLoggedIn(token)) {
            return ; // Token không hợp lệ
        }
        Optional<TaiKhoan> kq = userRepository.findById(maNV);
        TaiKhoan taiKhoan = kq.get();
        userRepository.deleteById(taiKhoan.getMaTK());
    }

    public NhanVien luu(NhanVien nhanVien,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        if (nhanVien.getTaiKhoan() != null) {
            Optional<TaiKhoan> kq = userRepository.findById(nhanVien.getTaiKhoan().getMaTK());
            TaiKhoan tk = kq.get();
            nhanVien.setTaiKhoan(tk);
        }
        return nhanVienRepository.save(nhanVien);
    }

    // Phương thức phân trang lấy danh sách nhân viên
    public Page<NhanVien> layDSNVPhanTrang(int pageNumber,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Pageable pageable = PageRequest.of(pageNumber, 10);  // Mỗi trang có tối đa 10 nhân viên
        return nhanVienRepository.findAll(pageable);
    }
}
