package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.GiaoVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import com.example.QuanLyPhongMayBackEnd.entity.Khoa;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.GiaoVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserRepository;
import jakarta.persistence.criteria.Join;
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

    public List<GiaoVienDTO> timKiemGiaoVien(String keyword, String token) { // keyword is still used for compatibility, consider renaming to search if you refactor frontend
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

        // Define valid columns for search - added tenKhoa
        List<String> validColumns = Arrays.asList("ho_ten", "so_dien_thoai", "email", "hoc_vi", "tenKhoa"); // Added tenKhoa to valid columns
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
                case "tenKhoa": // Added case for tenKhoa
                    return criteriaBuilder.like(root.get("khoa").get("tenKhoa"), "%" + value + "%"); // Assuming relation path to tenKhoa
                default:
                    return null;
            }
        };

        // Query the database using the Specification
        List<GiaoVien> results = giaoVienRepository.findAll(specification);

        // Map results to DTOs - Added tenKhoa to DTO mapping
        return results.stream()
                .map(giaoVien -> new GiaoVienDTO(
                        giaoVien.getMaGiaoVien(),
                        giaoVien.getHoTen(),
                        giaoVien.getSoDienThoai(),
                        giaoVien.getEmail(),
                        giaoVien.getHocVi(),
                        giaoVien.getKhoa().getTenKhoa() // Include tenKhoa in DTO
                ))
                .collect(Collectors.toList());
    }

    public List<GiaoVienDTO> timKiemGiaoVienByAdmin(String search, String token) { // Changed keyword to search
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        Specification<GiaoVien> spec = buildSpecification(search);
        if (spec == null) {
            return null; // Cú pháp tìm kiếm không hợp lệ
        }

        List<GiaoVien> results = giaoVienRepository.findAll(spec);

        return results.stream()
                .map(giaoVien -> new GiaoVienDTO(
                        giaoVien.getMaGiaoVien(),
                        giaoVien.getHoTen(),
                        giaoVien.getSoDienThoai(),
                        giaoVien.getEmail(),
                        giaoVien.getHocVi(),
                        giaoVien.getKhoa().getTenKhoa() // Include tenKhoa in DTO mapping for ByAdmin as well
                ))
                .collect(Collectors.toList());
    }

    private Specification<GiaoVien> buildSpecification(String search) { // Changed keyword to search
        if (search == null || search.trim().isEmpty()) {
            return null; // Không có điều kiện tìm kiếm
        }

        String[] searchParts = search.split(";"); // Tách các điều kiện bằng dấu ';' (AND)

        Specification<GiaoVien> finalSpec = Specification.where(null); // Specification ban đầu không có điều kiện

        for (String part : searchParts) {
            Specification<GiaoVien> partSpec = parseSearchPart(part);
            if (partSpec != null) {
                finalSpec = finalSpec.and(partSpec); // Kết hợp các điều kiện bằng AND
            } else {
                return null; // Nếu có bất kỳ điều kiện nào không hợp lệ, trả về null
            }
        }
        return finalSpec;
    }


    private Specification<GiaoVien> parseSearchPart(String part) {
        String[] tokens = part.split(":", 3); // Tách thành field, operator, value

        if (tokens.length != 3) {
            return null; // Sai cú pháp, cần có field:operator:value
        }

        String field = tokens[0].trim();
        String operator = tokens[1].trim().toUpperCase();
        String value = tokens[2].trim();

        return (root, query, criteriaBuilder) -> {
            switch (field) {
                case "hoTen":
                case "soDienThoai":
                case "email":
                case "hocVi":
                case "tenKhoa": // Added tenKhoa as searchable field
                    String fieldName = field; // Default fieldName is the field itself
                    Join<GiaoVien, Khoa> khoaJoin = null; // Declare Join object, initialize to null

                    if ("tenKhoa".equals(field)) {
                        fieldName = "tenKhoa"; // Field name remains tenKhoa for operator switch
                        khoaJoin = root.join("khoa"); // Perform the join and assign to khoaJoin
                    }

                    switch (operator) {
                        case "EQUALS":
                        case "EQ":
                            if ("tenKhoa".equals(field)) {
                                return criteriaBuilder.equal(khoaJoin.get("tenKhoa"), value); // Use khoaJoin for tenKhoa
                            } else {
                                return criteriaBuilder.equal(root.get(fieldName), value); // Use root for other fields
                            }
                        case "NOT_EQUALS":
                        case "NE":
                            if ("tenKhoa".equals(field)) {
                                return criteriaBuilder.notEqual(khoaJoin.get("tenKhoa"), value); // Use khoaJoin for tenKhoa
                            } else {
                                return criteriaBuilder.notEqual(root.get(fieldName), value); // Use root for other fields
                            }
                        case "LIKE":
                            if ("tenKhoa".equals(field)) {
                                return criteriaBuilder.like(khoaJoin.get("tenKhoa"), "%" + value + "%"); // Use khoaJoin for tenKhoa
                            } else {
                                return criteriaBuilder.like(root.get(fieldName), "%" + value + "%"); // Use root for other fields
                            }
                        case "STARTS_WITH":
                            if ("tenKhoa".equals(field)) {
                                return criteriaBuilder.like(khoaJoin.get("tenKhoa"), value + "%"); // Use khoaJoin for tenKhoa
                            } else {
                                return criteriaBuilder.like(root.get(fieldName), value + "%"); // Use root for other fields
                            }
                        case "ENDS_WITH":
                            if ("tenKhoa".equals(field)) {
                                return criteriaBuilder.like(khoaJoin.get("tenKhoa"), "%" + value); // Use khoaJoin for tenKhoa
                            } else {
                                return criteriaBuilder.like(root.get(fieldName), "%" + value); // Use root for other fields
                            }
                        case "GT":
                        case "GREATER_THAN":
                            try {
                                if ("tenKhoa".equals(field)) {
                                    return criteriaBuilder.greaterThan(khoaJoin.get("tenKhoa"), value); // Use khoaJoin for tenKhoa
                                } else {
                                    return criteriaBuilder.greaterThan(root.get(fieldName), value); // Use root for other fields
                                }
                            } catch (IllegalArgumentException e) {
                                return null; // Giá trị không hợp lệ cho kiểu số
                            }
                        case "LT":
                        case "LESS_THAN":
                            try {
                                if ("tenKhoa".equals(field)) {
                                    return criteriaBuilder.lessThan(khoaJoin.get("tenKhoa"), value); // Use khoaJoin for tenKhoa
                                } else {
                                    return criteriaBuilder.lessThan(root.get(fieldName), value); // Use root for other fields
                                }
                            } catch (IllegalArgumentException e) {
                                return null; // Giá trị không hợp lệ cho kiểu số
                            }
                        case "IN":
                            // Value should be comma-separated list in parentheses e.g., (value1,value2,value3)
                            if (value.startsWith("(") && value.endsWith(")")) {
                                String values = value.substring(1, value.length() - 1);
                                List<String> valueList = Arrays.stream(values.split(","))
                                        .map(String::trim)
                                        .collect(Collectors.toList());
                                if ("tenKhoa".equals(field)) {
                                    return khoaJoin.get("tenKhoa").in(valueList); // Use khoaJoin for tenKhoa
                                } else {
                                    return root.get(fieldName).in(valueList); // Use root for other fields
                                }
                            }
                            return null; // Invalid IN format
                        case "NOT_IN":
                        case "OUT":
                            // Value should be comma-separated list in parentheses e.g., (value1,value2,value3)
                            if (value.startsWith("(") && value.endsWith(")")) {
                                String values = value.substring(1, value.length() - 1);
                                List<String> valueList = Arrays.stream(values.split(","))
                                        .map(String::trim)
                                        .collect(Collectors.toList());
                                if ("tenKhoa".equals(field)) {
                                    return criteriaBuilder.not(khoaJoin.get("tenKhoa").in(valueList)); // Use khoaJoin for tenKhoa
                                } else {
                                    return criteriaBuilder.not(root.get(fieldName).in(valueList)); // Use root for other fields
                                }
                            }
                            return null; // Invalid NOT_IN format

                        default:
                            return null; // Toán tử không được hỗ trợ
                    }

                default:
                    return null; // Trường không hợp lệ
            }
        };

    }
    @Transactional
    public void xoaGiaoVienOnly(String maGV, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }

        try {
            Long maGiaoVien = Long.parseLong(maGV);
            giaoVienRepository.deleteByMaGiaoVien(maGiaoVien);
        } catch (NumberFormatException e) {
            // Handle the case where maGV is not a valid Long
            throw new IllegalArgumentException("Invalid maGV format. Must be a valid number.", e);
        }
    }
}
