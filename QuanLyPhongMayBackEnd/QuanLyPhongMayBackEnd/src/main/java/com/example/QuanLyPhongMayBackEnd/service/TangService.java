package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TangRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TangService {

    @Autowired
    private PhongMayRepository phongMayRepository;

    @Autowired
    private TangRepository tangRepository;
    @Autowired
    private  LichTrucService lichTrucService;


    @Autowired
    private PhongMayService phongMayService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public Tang layTangTheoMa(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Tang tang = null;
        Optional<Tang> kq = tangRepository.findById(maTang);
        try {
            tang = kq.get();
            return tang;
        } catch (Exception e) {
            return tang;
        }
    }

    public List<PhongMay> layDSPhongMayTheoTang(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayService.layPhongMayTheoMaTang(maTang, token);
    }



    public List<Tang> layDSTang(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.findAll();
    }

    @Transactional
    public void xoa(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        List<PhongMay> danhSachPhongMay = phongMayRepository.findByTang_MaTang(maTang);

        for (PhongMay phongMay : danhSachPhongMay) {
            List<LichTruc> dsLichTruc = lichTrucService.layLichTrucTheoMaTang(maTang, token);

            // Xoá từng lịch trực liên quan
            for (LichTruc lichTruc : dsLichTruc) {
                lichTrucService.xoa(lichTruc.getMaLich(), token);
            }

            // Xoá phòng máy
            phongMayService.xoa(phongMay.getMaPhong(),token);
        }

        // Sau khi xoá tất cả phòng máy và lịch trực, tiến hành xoá tầng
        tangRepository.deleteById(maTang);
    }

    public Tang luu(Tang tang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.save(tang);
    }

    public List<Tang> layTangTheoToaNha(Long maToaNha, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.findByToaNha_MaToaNha(maToaNha);
    }

    public Long tinhSoLuongTangTheoMaToaNha(Long maToaNha,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return tangRepository.countByToaNha_MaToaNha(maToaNha);
    }
}