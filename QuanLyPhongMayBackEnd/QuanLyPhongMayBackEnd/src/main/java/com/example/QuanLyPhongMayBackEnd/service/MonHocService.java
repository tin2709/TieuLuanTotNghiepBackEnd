package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.MonHocDTO;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.repository.MonHocRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonHocService {



    @Autowired
    private MonHocRepository monHocRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    public MonHoc layMonHocTheoMa(Long maMon,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        MonHoc monHoc = null;
        Optional<MonHoc> kq = monHocRepository.findById(maMon);
        try {
            monHoc = kq.get();
            return monHoc;
        } catch (Exception e) {
            return monHoc;
        }
    }
    @Cacheable(value = "monhocs") // Lưu trữ kết quả trong cache với tên "phongMays"
    public List<MonHoc> layDSMonHoc(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return monHocRepository.findAll();
    }

    @Transactional
    public void xoa(Long maMon,String token) {
        if (!isUserLoggedIn(token)) {
            return ; // Token không hợp lệ
        }
        monHocRepository.deleteById(maMon);
    }

    public MonHoc luu(MonHoc monHoc,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return monHocRepository.save(monHoc);
    }
    public List<MonHocDTO> timKiemMonHoc(String keyword, String token) {
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
        List<String> validColumns = Arrays.asList("ten_mon", "ngay_bat_dau", "ngay_ket_thuc", "so_buoi");
        if (!validColumns.contains(column)) {
            return null; // Invalid column name
        }

        // Build the Specification for dynamic search
        Specification<MonHoc> specification = (root, query, criteriaBuilder) -> {
            switch (column) {
                case "ten_mon":
                    return criteriaBuilder.like(root.get("tenMon"), "%" + value + "%");
                case "ngay_bat_dau":
                    return criteriaBuilder.equal(root.get("ngayBatDau"), Date.valueOf(value));
                case "ngay_ket_thuc":
                    return criteriaBuilder.equal(root.get("ngayKetThuc"), Date.valueOf(value));
                case "so_buoi":
                    return criteriaBuilder.equal(root.get("soBuoi"), Integer.parseInt(value));
                default:
                    return null;
            }
        };

        // Query the database using the Specification
        List<MonHoc> results = monHocRepository.findAll(specification);

        // Map results to DTOs
        return results.stream()
                .map(monHoc -> new MonHocDTO(
                        monHoc.getMaMon(),
                        monHoc.getTenMon(),
                        monHoc.getNgayBatDau(),
                        monHoc.getNgayKetThuc(),
                        monHoc.getSoBuoi()
                ))
                .collect(Collectors.toList());
    }
}
