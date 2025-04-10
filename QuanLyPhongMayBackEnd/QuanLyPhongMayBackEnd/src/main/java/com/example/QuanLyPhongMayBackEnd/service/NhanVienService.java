package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.NhanVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.NhanVien;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.NhanVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public NhanVien layNVTheoMa(String maNV,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        NhanVien nhanVien = null;
        Optional<NhanVien> kq = nhanVienRepository.findById(maNV);
        try {
            nhanVien = kq.get();
            return nhanVien;
        } catch (Exception e) {
            return nhanVien;
        }
    }
    @Cacheable(value = "nhanviens") // Lưu trữ kết quả trong cache với tên "phongMays"
    public List<NhanVien> layDSNV(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return nhanVienRepository.findAll();
    }

    @Transactional
    public void xoa(String maNV,String token) {
        if (!isUserLoggedIn(token)) {
            return ; // Token không hợp lệ
        }
        Optional<TaiKhoan> kq = userRepository.findById(maNV);
        TaiKhoan taiKhoan = kq.get();
        userRepository.deleteById(String.valueOf(taiKhoan.getMaTK()));
    }

    public NhanVien luu(NhanVien nhanVien) {

        if (nhanVien.getTaiKhoan() != null) {
            Optional<TaiKhoan> kq = userRepository.findById(String.valueOf(nhanVien.getTaiKhoan().getMaTK()));
            TaiKhoan tk = kq.get();
            nhanVien.setTaiKhoan(tk);
        }
        return nhanVienRepository.save(nhanVien);
    }

    // Phương thức phân trang lấy danh sách nhân viên
    public Page<NhanVien> layDSNVPhanTrang(int pageNumber,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Pageable pageable = PageRequest.of(pageNumber, 10);  // Mỗi trang có tối đa 10 nhân viên
        return nhanVienRepository.findAll(pageable);
    }
    public List<NhanVienDTO> timKiemNhanVien(String keyword, String token) {
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
        List<String> validColumns = Arrays.asList("ten_nv", "email", "sdt");
        if (!validColumns.contains(column)) {
            return null; // Invalid column name
        }

        // Build the Specification for dynamic search
        Specification<NhanVien> specification = (root, query, criteriaBuilder) -> {
            switch (column) {
                case "ten_nv":
                    return criteriaBuilder.like(root.get("tenNV"), "%" + value + "%");
                case "email":
                    return criteriaBuilder.like(root.get("email"), "%" + value + "%");
                case "sdt":
                    return criteriaBuilder.like(root.get("sDT"), "%" + value + "%");
                default:
                    return null;
            }
        };

        // Query the database using the Specification
        List<NhanVien> results = nhanVienRepository.findAll(specification);

        // Map results to DTOs
        return results.stream()
                .map(nv -> new NhanVienDTO(
                        nv.getMaNhanVien(),
                        nv.getTenNV(),
                        nv.getEmail(),
                        nv.getsDT()
                ))
                .collect(Collectors.toList());
    }
}
