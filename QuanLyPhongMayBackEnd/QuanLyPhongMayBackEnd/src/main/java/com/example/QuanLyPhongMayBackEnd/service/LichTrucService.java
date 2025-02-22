package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.repository.LichTrucRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
public class LichTrucService {

    @Autowired
    private LichTrucRepository lichTrucRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public LichTruc layLTTheoMa(Long maLich) {
        LichTruc lichTruc = null;
        Optional<LichTruc> kq = lichTrucRepository.findById(maLich);
        try {
            lichTruc = kq.get();
            return lichTruc;
        } catch (Exception e) {
            return lichTruc;
        }
    }

    public List<LichTruc> layDSLT() {
        return lichTrucRepository.findAll();
    }

    public void xoa(Long maLich) {
        lichTrucRepository.deleteById(maLich);
    }

    public LichTruc luu(LichTruc lichTruc) {
        return lichTrucRepository.save(lichTruc);
    }

    public List<LichTruc> layLichTrucTheoMaTang(Long maTang) {
        return lichTrucRepository.findByTang_MaTang(maTang);
    }

    public List<Tang> layTangChuaCoNhanVienTrucTrongThang() {
        try {
            // Sử dụng LichTrucRepository để lấy danh sách tầng chưa có nhân viên trực trong tháng hiện tại
            List<Tang> tangChuaCoNhanVienTruc = lichTrucRepository.findTangChuaCoNhanVienTrucTrongThang();

            // Kiểm tra và log thông tin để đảm bảo bạn nhận được dữ liệu đúng
            if (tangChuaCoNhanVienTruc != null) {
                System.out.println("Danh sách tầng chưa có nhân viên trực trong tháng hiện tại:");
                for (Tang tang : tangChuaCoNhanVienTruc) {
                    System.out.println(tang);
                }
            }

            return tangChuaCoNhanVienTruc;
        } catch (Exception e) {
            // Log lỗi nếu có
            e.printStackTrace();
            return Collections.emptyList(); // hoặc trả về danh sách rỗng hoặc xử lý lỗi khác tùy thuộc vào yêu cầu của bạn
        }
    }

    public LichTruc updateLichTruc(LichTruc lichTruc) {
        Optional<LichTruc> existingLichTruc = lichTrucRepository.findById(lichTruc.getMaLich());
        if (existingLichTruc.isPresent()) {
            LichTruc updatedLichTruc = existingLichTruc.get();
            updatedLichTruc.setNgayTruc(lichTruc.getNgayTruc());
            updatedLichTruc.setThoiGianBatDau(lichTruc.getThoiGianBatDau());
            updatedLichTruc.setThoiGianKetThuc(lichTruc.getThoiGianKetThuc());
            updatedLichTruc.setTang(lichTruc.getTang());
            updatedLichTruc.setNhanVien(lichTruc.getNhanVien());

            return lichTrucRepository.save(updatedLichTruc);
        } else {
            return null;
        }
    }
}
