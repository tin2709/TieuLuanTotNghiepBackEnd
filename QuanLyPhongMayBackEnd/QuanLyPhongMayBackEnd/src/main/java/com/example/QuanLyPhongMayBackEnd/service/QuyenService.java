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

    // Lấy quyền theo mã
    public Quyen layQuyenTheoMa(Long maQuyen) {
        return null; // Bạn có thể thêm logic tại đây
    }

    // Lấy danh sách quyền
    public List<Quyen> layDSQuyen() {
        return quyenRepository.findAll();
    }

    // Xóa quyền theo mã
    public void xoa(Long maQuyen) {
        quyenRepository.deleteById(maQuyen);
    }

    // Lưu quyền
    public Quyen luu(Quyen quyen) {
        return quyenRepository.save(quyen);
    }
}

