package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.MayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MayTinhService {

    @Autowired
    private MayTinhRepository mayTinhRepository;

    @Autowired
    private PhongMayRepository phongMayRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    // Phương thức lấy máy tính theo mã
    public MayTinh layMayTinhTheoMa(Long maMay) {
        Optional<MayTinh> mayTinhOptional = mayTinhRepository.findById(maMay);
        return mayTinhOptional.orElse(null);  // Trả về null nếu không tìm thấy
    }

    // Phương thức lấy máy tính theo trạng thái
    public List<MayTinh> findByTrangThai(String trangThai) {
        return mayTinhRepository.findByTrangThai(trangThai);
    }

    // Phương thức lấy danh sách máy tính theo mã phòng
    public List<MayTinh> layDSMayTinhTheoMaPhong(Long maPhong) {
        return mayTinhRepository.findByPhongMay_MaPhong(maPhong);
    }

    // Phương thức lấy danh sách tất cả máy tính
    public List<MayTinh> layDSMayTinh() {
        return mayTinhRepository.findAll();
    }

    // Phương thức xóa máy tính theo mã
    @Transactional
    public void xoa(Long maMay) {
        mayTinhRepository.deleteById(maMay);
    }

    // Phương thức lưu máy tính (cập nhật thêm phongMay)
    @Transactional
    public MayTinh luu(MayTinh mayTinh) {
        // Kiểm tra PhongMay từ maPhong
        PhongMay phongMay = getPhongMayById(mayTinh.getPhongMay().getMaPhong());
        if (phongMay != null) {
            mayTinh.setPhongMay(phongMay);  // Gán PhongMay cho máy tính
        }
        return mayTinhRepository.save(mayTinh);
    }

    // Phương thức lấy PhongMay theo maPhong
    public PhongMay getPhongMayById(Long maPhong) {
        Optional<PhongMay> phongMayOptional = phongMayRepository.findById(maPhong);
        return phongMayOptional.orElse(null);  // Trả về null nếu không tìm thấy
    }
}
