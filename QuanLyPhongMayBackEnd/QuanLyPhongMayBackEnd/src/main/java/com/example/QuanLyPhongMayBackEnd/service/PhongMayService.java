package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhongMayService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MayTinhService mayTinhService;



    @Autowired
    private CaThucHanhService caThucHanhService;

    @Autowired
    private PhongMayRepository phongMayRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public PhongMay layPhongMayTheoMa(Long maPhong) {
        PhongMay phongMay = null;
        Optional<PhongMay> kq = phongMayRepository.findById(maPhong);
        try {
            phongMay = kq.get();
            return phongMay;
        } catch (Exception e) {
            return phongMay;
        }
    }

    public List<PhongMay> findByTrangThai(String trangThai) {
        return phongMayRepository.findByTrangThai(trangThai);
    }

    public List<PhongMay> layDSPhongMay() {
        return phongMayRepository.findAll();
    }

    public void xoa(Long maPhong, String token) {

        // Get the list of computers and practice sessions for this room
        List<MayTinh> danhSachMayTinh = mayTinhService.layDSMayTinhTheoMaPhong(maPhong);

        // Now, you need to provide both parameters to `layDSCaThucHanhTheoMaPhong`
        List<CaThucHanh> danhSachCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMaPhong(maPhong, token);

        // Continue with the deletion logic
        for (MayTinh mayTinh : danhSachMayTinh) {
            mayTinhService.xoa(mayTinh.getMaMay());
        }

        for (CaThucHanh caThucHanh : danhSachCaThucHanh) {
            caThucHanhService.xoa(caThucHanh.getMaCa(),token);
        }

        entityManager.flush();
        entityManager.clear();
        phongMayRepository.deleteById(maPhong);

    }

    public PhongMay luu(PhongMay phongMay) {
        return phongMayRepository.save(phongMay);
    }

    public PhongMay capNhatTheoMa(Long maPhong, PhongMay phongMay) {
        Optional<PhongMay> phongMayDB = phongMayRepository.findById(maPhong);
        if (phongMayDB.isPresent()) {
            PhongMay phongMayCu = phongMayDB.get();
            return phongMayRepository.save(phongMayCu);
        }
        return null;
    }

    public List<PhongMay> layPhongMayTheoMaTang(Long maTang) {
        return phongMayRepository.findByTang_MaTang(maTang);
    }
}
