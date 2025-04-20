package com.example.QuanLyPhongMayBackEnd.service;


import java.util.List;
import java.util.Optional;

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
            System.out.println("QuyenService - Token không hợp lệ in layQuyenTheoMa"); // Log token invalid
            return null; // Token không hợp lệ
        }
        System.out.println("QuyenService - Tìm kiếm Quyen với maQuyen: " + maQuyen); // Log the maQuyen being searched
        Optional<Quyen> quyenOptional = quyenRepository.findById(maQuyen);

        if (quyenOptional.isPresent()) {
            Quyen quyen = quyenOptional.get();
            System.out.println("QuyenService - Quyen found: " + quyen); // Log if Quyen is found (toString included)
            System.out.println("QuyenService - Quyen tenQuyen: " + quyen.getTenQuyen()); // Log tenQuyen specifically
            return quyen;
        } else {
            System.out.println("QuyenService - Quyen NOT found for maQuyen: " + maQuyen); // Log if Quyen is NOT found
            return null;
        }
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

