package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuMayTinhRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class GhiChuMayTinhService {

    @Autowired
    private GhiChuMayTinhRepository ghiChuMayTinhRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public GhiChuMayTinh layGhiChuTheoMa(Long maGhiChuMT) {
        GhiChuMayTinh ghiChuMayTinh = null;
        Optional<GhiChuMayTinh> kq = ghiChuMayTinhRepository.findById(maGhiChuMT);
        try {
            ghiChuMayTinh = kq.get();
            return ghiChuMayTinh;
        } catch (Exception e) {
            return ghiChuMayTinh;
        }
    }

    @Transactional
    public void xoa(Long maGhiChuMT) {
        ghiChuMayTinhRepository.deleteById(maGhiChuMT);
    }

    @Transactional
    public void xoaTheoMaMay(Long maMay) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhRepository.findByMayTinh_MaMay(maMay);
        for (GhiChuMayTinh ghiChuMayTinh : dsGhiChuMayTinh) {
            ghiChuMayTinhRepository.delete(ghiChuMayTinh);
        }
    }

    public GhiChuMayTinh luu(GhiChuMayTinh ghiChuMayTinh) {
        return ghiChuMayTinhRepository.save(ghiChuMayTinh);
    }

    public List<GhiChuMayTinh> layDSGhiChu() {
        return ghiChuMayTinhRepository.findAll();
    }

    public GhiChuMayTinh capNhat(GhiChuMayTinh ghiChuMayTinh) {
        return ghiChuMayTinhRepository.save(ghiChuMayTinh);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoNgaySua(Date ngaySua) {
        return ghiChuMayTinhRepository.findByNgaySua(ngaySua);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoNgayBaoLoi(Date ngayBaoLoi) {
        return ghiChuMayTinhRepository.findByNgayBaoLoi(ngayBaoLoi);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoMayTinh(Long maMay) {
        return ghiChuMayTinhRepository.findByMayTinh_MaMay(maMay);
    }

    public GhiChuMayTinh layGhiChuGanNhatTheoMayTinh(Long maMay) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhRepository.findByMayTinh_MaMayOrderByNgayBaoLoiDesc(maMay);
        return dsGhiChuMayTinh.isEmpty() ? null : dsGhiChuMayTinh.get(0);
    }
}
