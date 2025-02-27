package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuPhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuPhongMayRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class GhiChuPhongMayService {

    @Autowired
    private GhiChuPhongMayRepository ghiChuPhongMayRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public GhiChuPhongMay layGhiChuTheoMa(Long maGhiChu,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        GhiChuPhongMay ghiChuPhongMay = null;
        Optional<GhiChuPhongMay> kq = ghiChuPhongMayRepository.findById(maGhiChu);
        try {
            ghiChuPhongMay = kq.get();
            return ghiChuPhongMay;
        } catch (Exception e) {
            return ghiChuPhongMay;
        }
    }

    @Transactional
    public void xoa(Long maGhiChu, String token) {
        if (!isUserLoggedIn(token)) {
            return ; // Token không hợp lệ
        }
        ghiChuPhongMayRepository.deleteById(maGhiChu);
    }

    public GhiChuPhongMay luu(GhiChuPhongMay ghiChuPhongMay,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuPhongMayRepository.save(ghiChuPhongMay);
    }

    public List<GhiChuPhongMay> layDSGhiChu(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuPhongMayRepository.findAll();
    }

    public GhiChuPhongMay capNhat(GhiChuPhongMay ghiChuPhongMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuPhongMayRepository.save(ghiChuPhongMay);
    }

    public List<GhiChuPhongMay> layDSGhiChuTheoNgaySua(Date ngaySua, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuPhongMayRepository.findByNgaySua(ngaySua);
    }

    public List<GhiChuPhongMay> layDSGhiChuTheoNgayBaoLoi(Date ngayBaoLoi, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuPhongMayRepository.findByNgayBaoLoi(ngayBaoLoi);
    }

    public List<GhiChuPhongMay> layDSGhiChuTheoPhongMay(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuPhongMayRepository.findByPhongMay_MaPhong(maPhong);
    }

    public GhiChuPhongMay layGhiChuGanNhatTheoPhongMay(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        List<GhiChuPhongMay> dsGhiChu = ghiChuPhongMayRepository.findByPhongMay_MaPhongOrderByNgayBaoLoiDesc(maPhong);
        return dsGhiChu.isEmpty() ? null : dsGhiChu.get(0);
    }
}
