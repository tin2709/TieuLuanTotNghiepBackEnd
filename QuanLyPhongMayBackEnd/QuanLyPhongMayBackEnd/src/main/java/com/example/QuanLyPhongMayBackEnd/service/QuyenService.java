package com.example.QuanLyPhongMayBackEnd.service;


import java.util.List;

import com.example.QuanLyPhongMayBackEnd.entity.Quyen;
import com.example.QuanLyPhongMayBackEnd.repository.QuyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class QuyenService {

    @Autowired
    private QuyenRepository quyenRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    // Lấy quyền theo mã
    public Quyen layQuyenTheoMa(Long maQuyen, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return null; // Bạn có thể thêm logic tại đây
    }

    // Lấy danh sách quyền
    public List<Quyen> layDSQuyen(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return quyenRepository.findAll();
    }

    // Xóa quyền theo mã
    public void xoa(Long maQuyen, String token) {
        if (!isUserLoggedIn(token)) {
            return ; // Token không hợp lệ
        }
        quyenRepository.deleteById(maQuyen);
    }

    // Lưu quyền
    public Quyen luu(Quyen quyen , String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return quyenRepository.save(quyen);
    }
}

