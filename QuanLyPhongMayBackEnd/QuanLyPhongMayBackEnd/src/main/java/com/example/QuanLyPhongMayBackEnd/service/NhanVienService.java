package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.NhanVienDTO;
import com.example.QuanLyPhongMayBackEnd.entity.ChucVu;
import com.example.QuanLyPhongMayBackEnd.entity.NhanVien;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.NhanVienRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserRepository;
import jakarta.persistence.criteria.Join;
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
    public NhanVien layNVTheoMa(Long maNV,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        NhanVien nhanVien = null;
        Optional<NhanVien> kq = nhanVienRepository.findById(String.valueOf(maNV));
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
                        nv.getsDT(),
                        nv.getChucVu().getTenCV()
                ))
                .collect(Collectors.toList());
    }
    public List<NhanVienDTO> timKiemNhanVienByAdmin(String search, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }

        Specification<NhanVien> spec = buildSpecification(search);
        if (spec == null) {
            return null; // Cú pháp tìm kiếm không hợp lệ
        }

        List<NhanVien> results = nhanVienRepository.findAll(spec);

        return results.stream()
                .map(nhanVien -> new NhanVienDTO(
                        nhanVien.getMaNhanVien(),
                        nhanVien.getTenNV(),
                        nhanVien.getEmail(),
                        nhanVien.getsDT(),
                        nhanVien.getChucVu().getTenCV() // Include tenCV from ChucVu
                ))
                .collect(Collectors.toList());
    }

    private Specification<NhanVien> buildSpecification(String search) {
        if (search == null || search.trim().isEmpty()) {
            return null; // Không có điều kiện tìm kiếm
        }

        String[] searchParts = search.split(";"); // Tách các điều kiện bằng dấu ';' (AND)

        Specification<NhanVien> finalSpec = Specification.where(null); // Specification ban đầu không có điều kiện

        for (String part : searchParts) {
            Specification<NhanVien> partSpec = parseSearchPart(part);
            if (partSpec != null) {
                finalSpec = finalSpec.and(partSpec); // Kết hợp các điều kiện bằng AND
            } else {
                return null; // Nếu có bất kỳ điều kiện nào không hợp lệ, trả về null
            }
        }
        return finalSpec;
    }


    private Specification<NhanVien> parseSearchPart(String part) {
        String[] tokens = part.split(":", 3); // Tách thành field, operator, value

        if (tokens.length != 3) {
            return null; // Sai cú pháp, cần có field:operator:value
        }

        String field = tokens[0].trim();
        String operator = tokens[1].trim().toUpperCase();
        String value = tokens[2].trim();

        return (root, query, criteriaBuilder) -> {
            switch (field) {
                case "tenNV":
                case "email":
                case "sDT":
                case "tenCV": // Added tenCV as searchable field
                    String fieldName = field; // Default fieldName is the field itself
                    Join<NhanVien, ChucVu> chucVuJoin = null; // Declare Join object, initialize to null

                    if ("tenCV".equals(field)) {
                        fieldName = "tenCV"; // Field name remains tenCV for operator switch
                        chucVuJoin = root.join("chucVu"); // Perform the join and assign to chucVuJoin
                    }

                    switch (operator) {
                        case "EQUALS":
                        case "EQ":
                            if ("tenCV".equals(field)) {
                                return criteriaBuilder.equal(chucVuJoin.get("tenCV"), value); // Use chucVuJoin for tenCV
                            } else {
                                return criteriaBuilder.equal(root.get(fieldName), value); // Use root for other fields
                            }
                        case "NOT_EQUALS":
                        case "NE":
                            if ("tenCV".equals(field)) {
                                return criteriaBuilder.notEqual(chucVuJoin.get("tenCV"), value); // Use chucVuJoin for tenCV
                            } else {
                                return criteriaBuilder.notEqual(root.get(fieldName), value); // Use root for other fields
                            }
                        case "LIKE":
                            if ("tenCV".equals(field)) {
                                return criteriaBuilder.like(chucVuJoin.get("tenCV"), "%" + value + "%"); // Use chucVuJoin for tenCV
                            } else {
                                return criteriaBuilder.like(root.get(fieldName), "%" + value + "%"); // Use root for other fields
                            }
                        case "STARTS_WITH":
                            if ("tenCV".equals(field)) {
                                return criteriaBuilder.like(chucVuJoin.get("tenCV"), value + "%"); // Use chucVuJoin for tenCV
                            } else {
                                return criteriaBuilder.like(root.get(fieldName), value + "%"); // Use root for other fields
                            }
                        case "ENDS_WITH":
                            if ("tenCV".equals(field)) {
                                return criteriaBuilder.like(chucVuJoin.get("tenCV"), "%" + value); // Use chucVuJoin for tenCV
                            } else {
                                return criteriaBuilder.like(root.get(fieldName), "%" + value); // Use root for other fields
                            }
                        case "GT":
                        case "GREATER_THAN":
                            try {
                                if ("tenCV".equals(field)) {
                                    return criteriaBuilder.greaterThan(chucVuJoin.get("tenCV"), value); // Use chucVuJoin for tenCV
                                } else {
                                    return criteriaBuilder.greaterThan(root.get(fieldName), value); // Use root for other fields
                                }
                            } catch (IllegalArgumentException e) {
                                return null; // Giá trị không hợp lệ cho kiểu số
                            }
                        case "LT":
                        case "LESS_THAN":
                            try {
                                if ("tenCV".equals(field)) {
                                    return criteriaBuilder.lessThan(chucVuJoin.get("tenCV"), value); // Use chucVuJoin for tenCV
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
                                if ("tenCV".equals(field)) {
                                    return chucVuJoin.get("tenCV").in(valueList); // Use chucVuJoin for tenCV
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
                                if ("tenCV".equals(field)) {
                                    return criteriaBuilder.not(chucVuJoin.get("tenCV").in(valueList)); // Use chucVuJoin for tenCV
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
    public void xoaNhanVienOnly(String maNV, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        nhanVienRepository.deleteById(maNV); // Just delete NhanVien, don't touch TaiKhoan
    }

}
