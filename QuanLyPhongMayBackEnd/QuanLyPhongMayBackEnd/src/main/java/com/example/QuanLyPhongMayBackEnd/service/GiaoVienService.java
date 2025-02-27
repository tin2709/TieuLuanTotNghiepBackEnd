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

    @Autowired
    private TaiKhoanService taiKhoanService;

    // Phương thức kiểm tra token
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public GiaoVien layGVTheoMa(String maGiaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ, trả về null hoặc có thể throw exception
        }
        return giaoVienRepository.findById(maGiaoVien).orElse(null);
    }

    public List<GiaoVien> layDSGV(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return giaoVienRepository.findAll();
    }

    // Phương thức phân trang lấy danh sách giáo viên
    public Page<GiaoVien> layDSGVPhanTrang(int pageNumber, String token) {
        if (!isUserLoggedIn(token)) {
            return Page.empty(); // Token không hợp lệ, trả về trang trống
        }
        Pageable pageable = PageRequest.of(pageNumber, 10); // Mỗi trang sẽ có 10 giáo viên
        return giaoVienRepository.findAll(pageable);
    }

    @Transactional
    public void xoa(Long maGiaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        // Xóa bản ghi giáo viên có maGiaoVien
        giaoVienRepository.deleteByMaGiaoVien(maGiaoVien);
    }



    public GiaoVien luu(GiaoVien giaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        if (giaoVien.getTaiKhoan() != null) {
            userRepository.findById(giaoVien.getTaiKhoan().getMaTK())
                    .ifPresent(giaoVien::setTaiKhoan);
        }
        return giaoVienRepository.save(giaoVien);
    }

    public GiaoVien capNhat(GiaoVien giaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return giaoVienRepository.save(giaoVien);
    }
}
