package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.GiaoVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.GiaoVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GiaoVienService {

    @Autowired
    private GiaoVienRepository giaoVienRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    // Phương thức kiểm tra token
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public GiaoVien layGVTheoMa(String maGiaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ, trả về null hoặc có thể throw exception
        }
        return giaoVienRepository.findById(maGiaoVien).orElse(null);
    }

    public List<GiaoVien> layDSGV(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return giaoVienRepository.findAll();
    }

    // Phương thức phân trang lấy danh sách giáo viên
    public Page<GiaoVien> layDSGVPhanTrang(int pageNumber, String token) {
        if (!isUserLoggedIn(token)) {
            return Page.empty(); // Token không hợp lệ, trả về trang trống
        }
        Pageable pageable = PageRequest.of(pageNumber, 10); // Mỗi trang sẽ có 10 giáo viên
        return giaoVienRepository.findAll(pageable);
    }

    @Transactional
    public void xoa(Long maGiaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        // Xóa bản ghi giáo viên có maGiaoVien
        giaoVienRepository.deleteByMaGiaoVien(maGiaoVien);
    }



    public GiaoVien luu(GiaoVien giaoVien) {
        if (giaoVien.getTaiKhoan() != null) {
            // Kiểm tra tài khoản có tồn tại trong DB chưa
            TaiKhoan taiKhoan = userRepository.findById(String.valueOf(giaoVien.getTaiKhoan().getMaTK()))
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

            giaoVien.setTaiKhoan(taiKhoan); // Gán bản đã tồn tại từ DB
        }

        return giaoVienRepository.save(giaoVien);
    }


    public GiaoVien capNhat(GiaoVien giaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return giaoVienRepository.save(giaoVien);
    }
    public List<GiaoVienDTO> timKiemGiaoVien(String keyword, String token) {
        // Ensure the user is logged in (you can implement token verification here)
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        // Split the keyword into column and value
        String[] parts = keyword.split(":");
        if (parts.length != 2) {
            return null; // Invalid keyword format
        }

        String column = parts[0].trim();
        String value = parts[1].trim();

        // Define valid columns for search
        List<String> validColumns = Arrays.asList("ho_ten", "so_dien_thoai", "email", "hoc_vi");
        if (!validColumns.contains(column)) {
            return null; // Invalid column name
        }

        // Build the Specification for dynamic search
        Specification<GiaoVien> specification = (root, query, criteriaBuilder) -> {
            switch (column) {
                case "ho_ten":
                    return criteriaBuilder.like(root.get("hoTen"), "%" + value + "%");
                case "so_dien_thoai":
                    return criteriaBuilder.like(root.get("soDienThoai"), "%" + value + "%");
                case "email":
                    return criteriaBuilder.like(root.get("email"), "%" + value + "%");
                case "hoc_vi":
                    return criteriaBuilder.like(root.get("hocVi"), "%" + value + "%");
                default:
                    return null;
            }
        };

        // Query the database using the Specification
        List<GiaoVien> results = giaoVienRepository.findAll(specification);

        // Map results to DTOs
        return results.stream()
                .map(giaoVien -> new GiaoVienDTO(
                        giaoVien.getMaGiaoVien(),
                        giaoVien.getHoTen(),
                        giaoVien.getSoDienThoai(),
                        giaoVien.getEmail(),
                        giaoVien.getHocVi()
                ))
                .collect(Collectors.toList());
    }

}
