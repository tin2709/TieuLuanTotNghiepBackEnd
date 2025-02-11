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

    public Tang layTangTheoMa(Long maTang) {
        Tang tang = null;
        Optional<Tang> kq = tangRepository.findById(maTang);
        try {
            tang = kq.get();
            return tang;
        } catch (Exception e) {
            return tang;
        }
    }

    public List<PhongMay> layDSPhongMayTheoTang(Long maTang) {
        return phongMayService.layPhongMayTheoMaTang(maTang);
    }



    public List<Tang> layDSTang() {
        return tangRepository.findAll();
    }

    @Transactional
    public void xoa(Long maTang) {
        List<PhongMay> danhSachPhongMay = phongMayRepository.findByTang_MaTang(maTang);

        for (PhongMay phongMay : danhSachPhongMay) {
            List<LichTruc> dsLichTruc = lichTrucService.layLichTrucTheoMaTang(maTang);

            // Xoá từng lịch trực liên quan
            for (LichTruc lichTruc : dsLichTruc) {
                lichTrucService.xoa(lichTruc.getMaLich());
            }

            // Xoá phòng máy
            phongMayService.xoa(phongMay.getMaPhong());
        }

        // Sau khi xoá tất cả phòng máy và lịch trực, tiến hành xoá tầng
        tangRepository.deleteById(maTang);
    }

    public Tang luu(Tang tang) {
        return tangRepository.save(tang);
    }

    public List<Tang> layTangTheoToaNha(Long maToaNha) {
        return tangRepository.findByToaNha_MaToaNha(maToaNha);
    }

    public Long tinhSoLuongTangTheoMaToaNha(Long maToaNha) {
        return tangRepository.countByToaNha_MaToaNha(maToaNha);
    }
}