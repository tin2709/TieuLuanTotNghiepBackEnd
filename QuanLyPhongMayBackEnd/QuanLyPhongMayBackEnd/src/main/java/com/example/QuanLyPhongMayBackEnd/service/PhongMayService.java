package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.PhongMayDTO;
import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public PhongMay layPhongMayTheoMa(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        PhongMay phongMay = null;
        Optional<PhongMay> kq = phongMayRepository.findById(maPhong);
        try {
            phongMay = kq.get();
            return phongMay;
        } catch (Exception e) {
            return phongMay;
        }
    }

    public List<PhongMay> findByTrangThai(String trangThai, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayRepository.findByTrangThai(trangThai);
    }

    public List<PhongMay> layDSPhongMay(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayRepository.findAll();
    }

    public void xoa(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }

        // Get the list of computers and practice sessions for this room
        List<MayTinh> danhSachMayTinh = mayTinhService.layDSMayTinhTheoMaPhong(maPhong, token);

        // Now, you need to provide both parameters to `layDSCaThucHanhTheoMaPhong`
        List<CaThucHanh> danhSachCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMaPhong(maPhong, token);

        // Continue with the deletion logic
        for (MayTinh mayTinh : danhSachMayTinh) {
            mayTinhService.xoa(mayTinh.getMaMay(), token);
        }

        for (CaThucHanh caThucHanh : danhSachCaThucHanh) {
            caThucHanhService.xoa(caThucHanh.getMaCa(), token);
        }

        entityManager.flush();
        entityManager.clear();
        phongMayRepository.deleteById(maPhong);

    }

    public PhongMay luu(PhongMay phongMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayRepository.save(phongMay);
    }

    public PhongMay capNhatTheoMa(Long maPhong, PhongMay phongMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Optional<PhongMay> phongMayDB = phongMayRepository.findById(maPhong);
        if (phongMayDB.isPresent()) {
            PhongMay phongMayCu = phongMayDB.get();
            return phongMayRepository.save(phongMayCu);
        }
        return null;
    }

    public List<PhongMay> layPhongMayTheoMaTang(Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayRepository.findByTang_MaTang(maTang);
    }

    public List<PhongMayDTO> timKiemPhongMay(String keyword, String token) {
        // Ensure the user is logged in (you can implement token verification here)
        if (!isUserLoggedIn(token)) {
            return null; // Invalid token
        }

        // Split the keyword into column and value
        String[] parts = keyword.split(":");
        if (parts.length != 2) {
            return null; // Invalid keyword format
        }

        String column = parts[0].trim();
        String value = parts[1].trim();

        // Define valid columns for search
        List<String> validColumns = Arrays.asList("ten_phong", "so_may", "mo_ta", "trang_thai");
        if (!validColumns.contains(column)) {
            return null; // Invalid column name
        }

        // Build the Specification for dynamic search
        Specification<PhongMay> specification = (root, query, criteriaBuilder) -> {
            switch (column) {
                case "ten_phong":
                    return criteriaBuilder.like(root.get("tenPhong"), "%" + value + "%");
                case "so_may":
                    return criteriaBuilder.equal(root.get("soMay"), Integer.parseInt(value));
                case "mo_ta":
                    return criteriaBuilder.like(root.get("moTa"), "%" + value + "%");
                case "trang_thai":
                    return criteriaBuilder.like(root.get("trangThai"), "%" + value + "%");
                default:
                    return null;
            }
        };

        // Query the database using the Specification
        List<PhongMay> results = phongMayRepository.findAll(specification);

        // Map results to DTOs
        return results.stream()
                .map(pm -> new PhongMayDTO(
                        pm.getMaPhong(),
                        pm.getTenPhong(),
                        pm.getSoMay(),
                        pm.getMoTa(),
                        pm.getTrangThai()

                ))
                .collect(Collectors.toList());
    }
}
