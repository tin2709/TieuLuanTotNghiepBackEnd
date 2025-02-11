package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.repository.MayTinhRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MayTinhService {

    @Autowired
    private MayTinhRepository mayTinhRepository;



    public MayTinh layMayTinhTheoMa(Long maMay) {
        MayTinh mayTinh = null;
        Optional<MayTinh> kq = mayTinhRepository.findById(maMay);
        try {
            mayTinh = kq.get();
            return mayTinh;
        } catch (Exception e) {
            return mayTinh;
        }
    }

    public List<MayTinh> findByTrangThai(String trangThai) {
        return mayTinhRepository.findByTrangThai(trangThai);
    }

    public List<MayTinh> layDSMayTinhTheoMaPhong(Long maPhong) {
        return mayTinhRepository.findByPhongMay_MaPhong(maPhong);
    }

    public List<MayTinh> layDSMayTinh() {
        return mayTinhRepository.findAll();
    }

    @Transactional
    public void xoa(Long maMay) {

        mayTinhRepository.deleteById(maMay);
    }

    public MayTinh luu(MayTinh mayTinh) {
        return mayTinhRepository.save(mayTinh);
    }
}
