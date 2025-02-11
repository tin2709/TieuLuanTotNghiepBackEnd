package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.ChucVu;
import com.example.QuanLyPhongMayBackEnd.repository.ChucVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class ChucVuService {

    @Autowired
    private ChucVuRepository chucVuRepository;

    public ChucVu layCVTheoMa(Long maCV) {
        ChucVu chucVu = null;
        Optional<ChucVu> kq = chucVuRepository.findById(maCV);
        try {
            chucVu = kq.get();
            return chucVu;
        } catch (Exception e) {
            return chucVu;
        }
    }

    public List<ChucVu> layDSCV() {
        return chucVuRepository.findAll();
    }

    public void xoa(Long maCV) {
        chucVuRepository.deleteById(maCV);
    }

    public ChucVu luu(ChucVu chucVu) {
        return chucVuRepository.save(chucVu);
    }
}
