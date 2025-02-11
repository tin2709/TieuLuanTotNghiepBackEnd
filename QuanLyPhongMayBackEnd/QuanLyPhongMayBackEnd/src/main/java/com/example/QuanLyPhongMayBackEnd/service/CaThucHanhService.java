package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.repository.CaThucHanhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CaThucHanhService {
    @Autowired
    private CaThucHanhRepository caThucHanhRepository;

    public CaThucHanh layCaThucHanhTheoMa(Long maCaThucHanh) {
        CaThucHanh caThucHanh = null;
        Optional<CaThucHanh> kq = caThucHanhRepository.findById(maCaThucHanh);
        try {
            caThucHanh = kq.get();
            return caThucHanh;
        } catch (Exception e) {
            return caThucHanh;
        }
    }

    public List<CaThucHanh> layDSCaThucHanhTheoNgay(Date ngayThucHanh) {
        return caThucHanhRepository.findByNgayThucHanh(ngayThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanh() {
        return caThucHanhRepository.findAll();
    }

    public void xoa(Long maCaThucHanh) {
        caThucHanhRepository.deleteById(maCaThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMonHoc(Long maMon) {
        return caThucHanhRepository.findByMonHoc_MaMon(maMon);
    }

    public CaThucHanh capNhat(CaThucHanh caThucHanh) {
        return caThucHanhRepository.save(caThucHanh);
    }

    public CaThucHanh luu(CaThucHanh caThucHanh) {
        return caThucHanhRepository.save(caThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMaPhong(Long maPhong) {
        return caThucHanhRepository.findByPhongMay_MaPhong(maPhong);
    }
}
