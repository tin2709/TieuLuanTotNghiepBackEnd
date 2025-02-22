package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.repository.MonHocRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonHocService {



    @Autowired
    private MonHocRepository monHocRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public MonHoc layMonHocTheoMa(Long maMon) {
        MonHoc monHoc = null;
        Optional<MonHoc> kq = monHocRepository.findById(maMon);
        try {
            monHoc = kq.get();
            return monHoc;
        } catch (Exception e) {
            return monHoc;
        }
    }

    public List<MonHoc> layDSMonHoc() {
        return monHocRepository.findAll();
    }

    @Transactional
    public void xoa(Long maMon) {
        monHocRepository.deleteById(maMon);
    }

    public MonHoc luu(MonHoc monHoc) {
        return monHocRepository.save(monHoc);
    }
}
