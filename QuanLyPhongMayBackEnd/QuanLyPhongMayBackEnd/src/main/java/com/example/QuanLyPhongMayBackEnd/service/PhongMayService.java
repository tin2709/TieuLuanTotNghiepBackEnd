package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.PhongMayDTO;
import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TangRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
    @Autowired
    private GhiChuMayTinhService ghiChuMayTinhService;
    @Autowired
    private GhiChuPhongMayService ghiChuPhongMayService;
    @Autowired
    private TangRepository tangRepository;
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
    public PhongMay capNhatTheoMaTang(Long maTang, PhongMay phongMay, String token) {
        // Kiểm tra lại quyền người dùng
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        // Kiểm tra nếu maTang có tồn tại phòng máy
        List<PhongMay> danhSachPhongMay = phongMayRepository.findByTang_MaTang(maTang);
        if (danhSachPhongMay.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy phòng máy thuộc tầng với maTang: " + maTang);
        }

        // Cập nhật thông tin phòng máy (ví dụ lấy phòng máy đầu tiên từ danh sách)
        PhongMay phongMayToUpdate = danhSachPhongMay.get(0);  // Hoặc logic lấy phòng máy cụ thể nào đó
        phongMayToUpdate.setTenPhong(phongMay.getTenPhong());
        phongMayToUpdate.setSoMay(phongMay.getSoMay());
        phongMayToUpdate.setMoTa(phongMay.getMoTa());
        phongMayToUpdate.setTrangThai(phongMay.getTrangThai());
        phongMayToUpdate.setTang(phongMay.getTang());  // Cập nhật lại tầng cho phòng máy

        // Lưu thông tin phòng máy đã cập nhật
        return phongMayRepository.save(phongMayToUpdate);
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

    @Transactional
    public void xoa(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }

        // Get the list of computers in the room
        List<MayTinh> danhSachMayTinh = mayTinhService.layDSMayTinhTheoMaPhong(maPhong, token);

        // Get the list of practice shifts associated with the room
        List<CaThucHanh> danhSachCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMaPhong(maPhong, token);
        ghiChuPhongMayService.xoaByMaPhong(maPhong, token);

        // Delete all entries in the ghi_chu_may_tinh table that reference the computers
        for (MayTinh mayTinh : danhSachMayTinh) {
            // Assuming ghiChuMayTinhService can delete records by maMay
            ghiChuMayTinhService.xoaTheoMaMay(mayTinh.getMaMay(), token);
        }

        // Delete all computers in the room
        for (MayTinh mayTinh : danhSachMayTinh) {
            mayTinhService.xoa(mayTinh.getMaMay(), token);
        }

        // Delete all practice shifts associated with the room
        for (CaThucHanh caThucHanh : danhSachCaThucHanh) {
            caThucHanhService.xoa(caThucHanh.getMaCa(), token);
        }

        // Finally, delete the room itself
        phongMayRepository.deleteById(maPhong);

        // Ensure changes are flushed to the database immediately
        entityManager.flush();
        entityManager.clear();
    }

    public PhongMay luu(PhongMay phongMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return phongMayRepository.save(phongMay);
    }

    public PhongMay capNhatTheoMa(Long maPhong, String tenPhong, int soMay, String moTa, String trangThai, Long maTang, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        Optional<PhongMay> phongMayDB = phongMayRepository.findById(maPhong);
        if (phongMayDB.isPresent()) {
            PhongMay phongMayCu = phongMayDB.get();

            // Cập nhật thông tin từ request
            phongMayCu.setTenPhong(tenPhong);
            phongMayCu.setSoMay(soMay);
            phongMayCu.setMoTa(moTa);
            phongMayCu.setTrangThai(trangThai);

            // Tìm Tang theo maTang
            Optional<Tang> tangOptional = tangRepository.findById(maTang);
            if (!tangOptional.isPresent()) {
                // Handle case where Tang is not found
                return null; // Or throw an exception
            }
            Tang tang = tangOptional.get();
            phongMayCu.setTang(tang); // Set the Tang object

            // Lưu lại vào cơ sở dữ liệu
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
