package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.repository.GiaoVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class GiaoVienService {

    @Autowired
    private GiaoVienRepository giaoVienRepository;

    @Autowired
    private UserRepository userRepository;

    public GiaoVien layGVTheoMa(String maGiaoVien) {
        return giaoVienRepository.findById(maGiaoVien).orElse(null);
    }

    public List<GiaoVien> layDSGV() {
        return giaoVienRepository.findAll();
    }

    // Phương thức phân trang lấy danh sách giáo viên
    public Page<GiaoVien> layDSGVPhanTrang(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10); // Mỗi trang sẽ có 10 giáo viên
        return giaoVienRepository.findAll(pageable);
    }

    @Transactional
    public void xoa(String maGiaoVien) {
        userRepository.findById(maGiaoVien).ifPresent(userRepository::delete);
    }

    public GiaoVien luu(GiaoVien giaoVien) {
        if (giaoVien.getTaiKhoan() != null) {
            userRepository.findById(giaoVien.getTaiKhoan().getMaTK())
                    .ifPresent(giaoVien::setTaiKhoan);
        }
        return giaoVienRepository.save(giaoVien);
    }

    public GiaoVien capNhat(GiaoVien giaoVien) {
        return giaoVienRepository.save(giaoVien);
    }
}
