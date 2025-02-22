package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.LichTruc;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.entity.ToaNha;
import com.example.QuanLyPhongMayBackEnd.repository.ToaNhaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ToaNhaService {

    @Autowired
    private ToaNhaRepository toaNhaRepository;

    @Autowired
    private TangService tangService;

    @Autowired
    private LichTrucService lichTrucService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public ToaNha layToaNhaTheoMa(Long maToaNha) {
        ToaNha toaNha = null;
        Optional<ToaNha> kq = toaNhaRepository.findById(maToaNha);
        try {
            toaNha = kq.get();
            return toaNha;
        } catch (Exception e) {
            return toaNha;
        }
    }

    public List<ToaNha> layDSToaNha() {
        return toaNhaRepository.findAll();
    }

    @Transactional
    public void xoa(Long maToaNha,String token) {
        List<Tang> dsTang = tangService.layTangTheoToaNha(maToaNha);
        for (Tang tang : dsTang) {
            List<LichTruc> dsLichTruc = lichTrucService.layLichTrucTheoMaTang(tang.getMaTang());
            for (LichTruc lichTruc : dsLichTruc) {
                lichTrucService.xoa(lichTruc.getMaLich());
            }
            tangService.xoa(tang.getMaTang(),token);
        }
        toaNhaRepository.deleteById(maToaNha);
    }

    public ToaNha luu(ToaNha toaNha) {
        return toaNhaRepository.save(toaNha);
    }
}
