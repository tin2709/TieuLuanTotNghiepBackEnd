package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.CaThucHanhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.CaThucHanhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.GiaoVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.MonHocRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaThucHanhService {
    @Autowired
    private CaThucHanhRepository caThucHanhRepository;
    @Autowired
    private MonHocRepository monHocRepository;
    @Autowired
    private PhongMayRepository phongMayRepository;
    @Autowired
    private GiaoVienRepository giaoVienRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Method to handle user login validation before performing any operation
    private Map<String, Object> checkTokenAndReturnResponse(String token) {
        Map<String, Object> response = new HashMap<>();
        if (!isUserLoggedIn(token)) {
            response.put("status", "error");
            response.put("message", "You need to log in to perform this action.");
        }
        return response;
    }

    public CaThucHanh layCaThucHanhTheoMa(Long maCaThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        CaThucHanh caThucHanh = null;
        Optional<CaThucHanh> kq = caThucHanhRepository.findById(maCaThucHanh);
        try {
            caThucHanh = kq.get();
        } catch (Exception e) {
            // Handle exception as needed
        }
        return caThucHanh;
    }

    public List<CaThucHanh> layDSCaThucHanhTheoNgay(Date ngayThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findByNgayThucHanh(ngayThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanh(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findAll();
    }

    public void xoa(Long maCaThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return;
        }
        caThucHanhRepository.deleteById(maCaThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMonHoc(Long maMon, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findByMonHoc_MaMon(maMon);
    }

    public CaThucHanh capNhat(CaThucHanh caThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.save(caThucHanh);
    }

    public CaThucHanh luu(CaThucHanh caThucHanh, String token, Long maGiaoVien, Long maPhong, Long maMon) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        // Fetch GiaoVien, PhongMay, and MonHoc from the database
        GiaoVien giaoVien = giaoVienRepository.findById(String.valueOf(maGiaoVien)).orElse(null);
        PhongMay phongMay = phongMayRepository.findById(maPhong).orElse(null);
        MonHoc monHoc = monHocRepository.findById(maMon).orElse(null);

        if (giaoVien == null || phongMay == null || monHoc == null) {
            // Handle the case where related entities are not found (e.g., throw an exception or return null)
            return null; // Or throw EntityNotFoundException
        }

        // Set the fetched entities to CaThucHanh
        caThucHanh.setGiaoVien(giaoVien);
        caThucHanh.setPhongMay(phongMay);
        caThucHanh.setMonHoc(monHoc);

        return caThucHanhRepository.save(caThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMaPhong(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return caThucHanhRepository.findByPhongMay_MaPhong(maPhong);
    }
    public List<CaThucHanhDTO> timKiemCaThucHanh(String keyword, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        // Phân tích keyword thành cột và giá trị
        String[] parts = keyword.split(":");
        if (parts.length != 2) {
            return null; // Nếu không có dấu ":" hoặc có quá nhiều, trả về null
        }

        String column = parts[0].trim(); // Cột cần tìm
        String value = parts[1].trim();  // Giá trị cần tìm

        // Kiểm tra xem cột có hợp lệ không
        List<String> validColumns = Arrays.asList("ten_ca", "ngay_thuc_hanh", "tiet_bat_dau", "tiet_ket_thuc", "buoi_so");
        if (!validColumns.contains(column)) {
            return null; // Nếu cột không hợp lệ, trả về null
        }

        // Xây dựng Specification để tìm kiếm động
        Specification<CaThucHanh> specification = (root, query, criteriaBuilder) -> {
            switch (column) {
                case "ten_ca":
                    return criteriaBuilder.like(root.get("tenCa"), "%" + value + "%");
                case "ngay_thuc_hanh":
                    return criteriaBuilder.like(root.get("ngayThucHanh"), "%" + value + "%");
                case "tiet_bat_dau":
                    try {
                        // Kiểm tra nếu value có phải là số hợp lệ
                        int tietBatDau = Integer.parseInt(value);
                        return criteriaBuilder.equal(root.get("tietBatDau"), tietBatDau);
                    } catch (NumberFormatException e) {
                        return null; // Trả về null nếu value không phải là số hợp lệ
                    }
                case "tiet_ket_thuc":
                    try {
                        // Kiểm tra nếu value có phải là số hợp lệ
                        int tietKetThuc = Integer.parseInt(value);
                        return criteriaBuilder.equal(root.get("tietKetThuc"), tietKetThuc);
                    } catch (NumberFormatException e) {
                        return null; // Trả về null nếu value không phải là số hợp lệ
                    }
                case "buoi_so":
                    // Kiểm tra nếu value có phải là số hợp lệ
                    try {
                        int buoiSo = Integer.parseInt(value);
                        return criteriaBuilder.equal(root.get("buoiSo"), buoiSo);
                    } catch (NumberFormatException e) {
                        return criteriaBuilder.like(root.get("buoiSo"), "%" + value + "%");
                    }
                default:
                    return null;
            }
        };

        // Truy vấn danh sách các CaThucHanh
        List<CaThucHanh> results = caThucHanhRepository.findAll(specification);

        // Chuyển kết quả thành DTO
        return results.stream()
                .map(caThucHanh -> new CaThucHanhDTO(
                        caThucHanh.getMaCa(),
                        caThucHanh.getNgayThucHanh(),
                        caThucHanh.getTenCa(),
                        caThucHanh.getTietBatDau(),
                        caThucHanh.getTietKetThuc(),
                        caThucHanh.getBuoiSo()))
                .collect(Collectors.toList());
    }
    public List<CaThucHanh> layDSCaThucHanhTheoTenGiaoVien(String hoTenGiaoVien, String token) { // Changed parameter name to hoTenGiaoVien
        if (!isUserLoggedIn(token)) {
            return null;
        }
        return caThucHanhRepository.findByGiaoVien_HoTen(hoTenGiaoVien); // Corrected to HoTen to match GiaoVien entity field
    }






}
