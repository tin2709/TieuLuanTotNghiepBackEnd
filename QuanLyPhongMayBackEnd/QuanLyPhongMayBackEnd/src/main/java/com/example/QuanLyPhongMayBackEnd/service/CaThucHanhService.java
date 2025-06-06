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
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        // Assuming TaiKhoanService has a method like this:
        Map<String, Object> loginStatus = taiKhoanService.checkUserLoginStatus(token);
        return loginStatus != null && "success".equals(loginStatus.get("status"));
    }


    public CaThucHanh layCaThucHanhTheoMa(Long maCaThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }
        return caThucHanhRepository.findById(maCaThucHanh)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ca thực hành với mã: " + maCaThucHanh));
    }

    public List<CaThucHanh> layDSCaThucHanhTheoNgay(Date ngayThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList(); // Or throw exception
        }
        return caThucHanhRepository.findByNgayThucHanh(ngayThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanh(String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList(); // Or throw exception
        }
        return caThucHanhRepository.findAll();
    }

    @Transactional
    public void xoa(Long maCaThucHanh, String token) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }
        if (!caThucHanhRepository.existsById(maCaThucHanh)) {
            throw new EntityNotFoundException("Không tìm thấy ca thực hành với mã: " + maCaThucHanh + " để xóa.");
        }
        try {
            caThucHanhRepository.deleteById(maCaThucHanh);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Không tìm thấy ca thực hành với mã: " + maCaThucHanh + " để xóa.");
        }
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMonHoc(Long maMon, String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList(); // Or throw exception
        }
        return caThucHanhRepository.findByMonHoc_MaMon(maMon);
    }

    @Transactional
    public CaThucHanh capNhat(CaThucHanh caThucHanhDetails, String token, Long maGiaoVien, Long maPhong, Long maMon) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }

        CaThucHanh existingCaThucHanh = caThucHanhRepository.findById(caThucHanhDetails.getMaCa())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ca thực hành với mã: " + caThucHanhDetails.getMaCa()));

        // Fetch related entities
        GiaoVien giaoVien = giaoVienRepository.findById(String.valueOf(maGiaoVien)) // Assuming GiaoVien ID is String
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giáo viên với mã: " + maGiaoVien));
        PhongMay phongMay = phongMayRepository.findById(maPhong)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng máy với mã: " + maPhong));
        MonHoc monHoc = monHocRepository.findById(maMon)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy môn học với mã: " + maMon));

        // Validate tietBatDau < tietKetThuc
        if (caThucHanhDetails.getTietBatDau() >= caThucHanhDetails.getTietKetThuc()) {
            throw new IllegalArgumentException("Tiết bắt đầu phải nhỏ hơn tiết kết thúc.");
        }


        existingCaThucHanh.setNgayThucHanh(caThucHanhDetails.getNgayThucHanh());
        existingCaThucHanh.setTenCa(caThucHanhDetails.getTenCa());
        existingCaThucHanh.setTietBatDau(caThucHanhDetails.getTietBatDau());
        existingCaThucHanh.setTietKetThuc(caThucHanhDetails.getTietKetThuc());
        existingCaThucHanh.setBuoiSo(caThucHanhDetails.getBuoiSo());
        existingCaThucHanh.setGiaoVien(giaoVien);
        existingCaThucHanh.setPhongMay(phongMay);
        existingCaThucHanh.setMonHoc(monHoc);

        return caThucHanhRepository.save(existingCaThucHanh);
    }

    @Transactional
    public CaThucHanh luu(CaThucHanh caThucHanh, String token, Long maGiaoVien, Long maPhong, Long maMon) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }

        GiaoVien giaoVien = giaoVienRepository.findById(String.valueOf(maGiaoVien)) // Assuming GiaoVien ID is String
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giáo viên với mã: " + maGiaoVien));
        PhongMay phongMay = phongMayRepository.findById(maPhong)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng máy với mã: " + maPhong));
        MonHoc monHoc = monHocRepository.findById(maMon)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy môn học với mã: " + maMon));

        // Validate tietBatDau < tietKetThuc
        if (caThucHanh.getTietBatDau() >= caThucHanh.getTietKetThuc()) {
            throw new IllegalArgumentException("Tiết bắt đầu phải nhỏ hơn tiết kết thúc.");
        }

        caThucHanh.setGiaoVien(giaoVien);
        caThucHanh.setPhongMay(phongMay);
        caThucHanh.setMonHoc(monHoc);

        return caThucHanhRepository.save(caThucHanh);
    }

    public List<CaThucHanh> layDSCaThucHanhTheoMaPhong(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList();
        }
        return caThucHanhRepository.findByPhongMay_MaPhong(maPhong);
    }

    public List<CaThucHanhDTO> timKiemCaThucHanh(String keyword, String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList();
        }

        String[] parts = keyword.split(":", 2); // Split only into 2 parts
        if (parts.length != 2) {
            throw new IllegalArgumentException("Định dạng keyword không hợp lệ. Mong muốn: 'column:value'.");
        }

        String column = parts[0].trim();
        String value = parts[1].trim();

        List<String> validColumns = Arrays.asList("ten_ca", "ngay_thuc_hanh", "tiet_bat_dau", "tiet_ket_thuc", "buoi_so");
        if (!validColumns.contains(column)) {
            throw new IllegalArgumentException("Tên cột không hợp lệ: " + column);
        }

        Specification<CaThucHanh> specification = (root, query, criteriaBuilder) -> {
            try {
                switch (column) {
                    case "ten_ca":
                        return criteriaBuilder.like(criteriaBuilder.lower(root.get("tenCa")), "%" + value.toLowerCase() + "%");
                    case "ngay_thuc_hanh": // Assuming value is yyyy-MM-dd string
                        java.util.Date dateValue = parseUtilDate(value); // Use helper for robust parsing
                        // To match any time on that day, we need a range
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dateValue);
                        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        Date startDate = cal.getTime();
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        Date endDate = cal.getTime();
                        return criteriaBuilder.between(root.get("ngayThucHanh"), startDate, endDate);
                    case "tiet_bat_dau":
                        return criteriaBuilder.equal(root.get("tietBatDau"), Integer.parseInt(value));
                    case "tiet_ket_thuc":
                        return criteriaBuilder.equal(root.get("tietKetThuc"), Integer.parseInt(value));
                    case "buoi_so":
                        return criteriaBuilder.equal(root.get("buoiSo"), Integer.parseInt(value));
                    default:
                        return criteriaBuilder.disjunction(); // No-op if column is somehow invalid past the check
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giá trị không phải là số hợp lệ cho cột " + column + ": " + value);
            } catch (IllegalArgumentException e) { // Catches parseUtilDate's exception
                throw new IllegalArgumentException("Định dạng ngày không hợp lệ cho giá trị '" + value + "'. Sử dụng yyyy-MM-dd.");
            }
        };

        List<CaThucHanh> results = caThucHanhRepository.findAll(specification);
        return results.stream()
                .map(this::mapToCaThucHanhDTO) // Use the new DTO mapper
                .collect(Collectors.toList());
    }
    public List<CaThucHanh> layDSCaThucHanhTheoTenGiaoVien(String hoTenGiaoVien, String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList();
        }
        return caThucHanhRepository.findByGiaoVien_HoTenIgnoreCaseContaining(hoTenGiaoVien); // More flexible search
    }

    // ========== NEW METHODS ==========

    @Transactional
    public int xoaNhieuCaThucHanh(List<Long> maCaThucHanhList, String token) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }
        if (maCaThucHanhList == null || maCaThucHanhList.isEmpty()) {
            return 0;
        }

        int soLuongDaXoa = 0;
        List<Long> khongTimThay = new ArrayList<>();

        for (Long maCa : maCaThucHanhList) {
            if (caThucHanhRepository.existsById(maCa)) {
                caThucHanhRepository.deleteById(maCa);
                soLuongDaXoa++;
            } else {
                khongTimThay.add(maCa);
            }
        }

        if (!khongTimThay.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy các ca thực hành với mã: " + khongTimThay.toString());
        }
        return soLuongDaXoa;
    }

    @Transactional(readOnly = true)
    public List<CaThucHanhDTO> timKiemCaThucHanhByAdmin(String search, String token) {
        if (!isUserLoggedIn(token)) {
            // Or throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return Collections.emptyList();
        }

        Specification<CaThucHanh> spec = buildCaThucHanhSpecification(search);
        if (spec == null) {
            System.err.println("Invalid search query format resulted in null specification for CaThucHanh: " + search);
            // Consider throwing IllegalArgumentException here if strict parsing is required
            return Collections.emptyList();
        }

        List<CaThucHanh> results;
        try {
            results = caThucHanhRepository.findAll(spec);
        } catch (Exception e) {
            // Log the exception, e.g., using Sentry or a logger
            System.err.println("Error executing search specification for CaThucHanh: " + e.getMessage());
            e.printStackTrace(); // For debugging, replace with proper logging
            return Collections.emptyList();
        }

        return results.stream()
                .map(this::mapToCaThucHanhDTO)
                .collect(Collectors.toList());
    }

    private CaThucHanhDTO mapToCaThucHanhDTO(CaThucHanh caThucHanh) {
        // This DTO only contains direct fields of CaThucHanh.
        // If related entity information (like names) is needed in the DTO for search results,
        // this DTO and mapping logic would need to be extended.
        return new CaThucHanhDTO(
                caThucHanh.getMaCa(),
                caThucHanh.getNgayThucHanh(),
                caThucHanh.getTenCa(),
                caThucHanh.getTietBatDau(),
                caThucHanh.getTietKetThuc(),
                caThucHanh.getBuoiSo()
                // If DTO is extended, add mappings for GiaoVien, PhongMay, MonHoc info here
                // e.g., caThucHanh.getGiaoVien() != null ? caThucHanh.getGiaoVien().getHoTen() : null
        );
    }

    private Specification<CaThucHanh> buildCaThucHanhSpecification(String search) {
        if (search == null || search.trim().isEmpty()) {
            return Specification.where(null); // Return all if search is empty
        }

        String[] searchParts = search.split(";");
        Specification<CaThucHanh> finalSpec = Specification.where(null);

        for (String part : searchParts) {
            if (part == null || part.trim().isEmpty()) continue;

            Specification<CaThucHanh> partSpec = parseCaThucHanhSearchPart(part.trim());
            if (partSpec != null) {
                finalSpec = finalSpec.and(partSpec);
            } else {
                // Log or handle invalid part - potentially throw IllegalArgumentException
                System.err.println("Invalid search part for CaThucHanh skipped due to parsing error: " + part);
                // To fail fast: throw new IllegalArgumentException("Invalid search part: " + part);
            }
        }
        return finalSpec;
    }

    private Specification<CaThucHanh> parseCaThucHanhSearchPart(String part) {
        String[] tokens = part.split(":", 3);

        if (tokens.length != 3) {
            System.err.println("Invalid CaThucHanh search part format (expected field:operator:value): " + part);
            // throw new IllegalArgumentException("Invalid search part format: " + part);
            return null; // Or return a disjunction to make it fail silently for this part
        }

        String field = tokens[0].trim();
        String operator = tokens[1].trim().toUpperCase();
        String value = tokens[2].trim();

        if (value.isEmpty() && !operator.equals("IS_NULL") && !operator.equals("IS_NOT_NULL")) {
            System.err.println("Empty value provided for operator " + operator + " which requires a value, in CaThucHanh part: " + part);
            // throw new IllegalArgumentException("Empty value for operator " + operator + " in part: " + part);
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            try {
                Path<?> path; // Path to the attribute
                Join<?, ?> giaoVienJoin = null;
                Join<?, ?> phongMayJoin = null;
                Join<?, ?> monHocJoin = null;

                // Determine the path, joining if necessary
                switch (field) {
                    case "maCa": path = root.get("maCa"); break;
                    case "tenCa": path = root.get("tenCa"); break;
                    case "ngayThucHanh": path = root.get("ngayThucHanh"); break;
                    case "tietBatDau": path = root.get("tietBatDau"); break;
                    case "tietKetThuc": path = root.get("tietKetThuc"); break;
                    case "buoiSo": path = root.get("buoiSo"); break;
                    case "giaoVienMa":
                        giaoVienJoin = root.join("giaoVien", JoinType.LEFT); // Use LEFT join if CTH might not have GV
                        path = giaoVienJoin.get("maGiaoVien");
                        break;
                    case "giaoVienHoTen":
                        giaoVienJoin = root.join("giaoVien", JoinType.LEFT);
                        path = giaoVienJoin.get("hoTen");
                        break;
                    case "phongMayMa":
                        phongMayJoin = root.join("phongMay", JoinType.LEFT);
                        path = phongMayJoin.get("maPhong");
                        break;
                    case "phongMayTen":
                        phongMayJoin = root.join("phongMay", JoinType.LEFT);
                        path = phongMayJoin.get("tenPhong");
                        break;
                    case "monHocMa":
                        monHocJoin = root.join("monHoc", JoinType.LEFT);
                        path = monHocJoin.get("maMon");
                        break;
                    case "monHocTen":
                        monHocJoin = root.join("monHoc", JoinType.LEFT);
                        path = monHocJoin.get("tenMon");
                        break;
                    default:
                        System.err.println("Invalid CaThucHanh search field specified: " + field);
                        throw new IllegalArgumentException("Invalid search field: " + field);
                }

                if ("IS_NULL".equals(operator)) return criteriaBuilder.isNull(path);
                if ("IS_NOT_NULL".equals(operator)) return criteriaBuilder.isNotNull(path);

                Class<?> fieldType = path.getJavaType();

                if (fieldType == String.class) {
                    Expression<String> stringPath = path.as(String.class);
                    switch (operator) {
                        case "EQUALS": case "EQ": return criteriaBuilder.equal(stringPath, value);
                        case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(stringPath, value);
                        case "LIKE": return criteriaBuilder.like(criteriaBuilder.lower(stringPath), "%" + value.toLowerCase() + "%");
                        case "NOT_LIKE": return criteriaBuilder.notLike(criteriaBuilder.lower(stringPath), "%" + value.toLowerCase() + "%");
                        case "STARTS_WITH": return criteriaBuilder.like(criteriaBuilder.lower(stringPath), value.toLowerCase() + "%");
                        case "ENDS_WITH": return criteriaBuilder.like(criteriaBuilder.lower(stringPath), "%" + value.toLowerCase());
                        case "IN":
                            List<String> inValues = parseCsvStringList(value);
                            return inValues.isEmpty() ? criteriaBuilder.disjunction() : stringPath.in(inValues);
                        case "NOT_IN": case "OUT":
                            List<String> notInValues = parseCsvStringList(value);
                            return notInValues.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(stringPath.in(notInValues));
                        default: throw new IllegalArgumentException("Unsupported string operator: " + operator);
                    }
                } else if (java.util.Date.class.isAssignableFrom(fieldType)) {
                    Expression<java.util.Date> datePath = path.as(java.util.Date.class);
                    if ("BETWEEN".equals(operator) || "NOT_BETWEEN".equals(operator)) {
                        String[] dateParts = value.split(",", 2);
                        if (dateParts.length != 2) throw new IllegalArgumentException("BETWEEN/NOT_BETWEEN requires two dates (yyyy-MM-dd,yyyy-MM-dd). Found: " + value);
                        java.util.Date startDate = parseUtilDate(dateParts[0].trim());
                        java.util.Date endDate = parseUtilDate(dateParts[1].trim());
                        if (startDate.after(endDate)) throw new IllegalArgumentException("Start date cannot be after end date. Found: " + value);

                        // Adjust endDate to be the start of the next day for inclusive end date matching
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        java.util.Date endOfDayExclusive = cal.getTime();

                        // Adjust startDate to be the start of the day
                        cal.setTime(startDate);
                        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        java.util.Date startOfDayInclusive = cal.getTime();


                        Predicate betweenPredicate = criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDayInclusive),
                                criteriaBuilder.lessThan(datePath, endOfDayExclusive)
                        );
                        return "BETWEEN".equals(operator) ? betweenPredicate : criteriaBuilder.not(betweenPredicate);
                    }

                    java.util.Date dateValue = parseUtilDate(value);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateValue);
                    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                    java.util.Date startOfDay = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    java.util.Date startOfNextDay = cal.getTime();

                    switch (operator) {
                        case "EQUALS": case "EQ": // Matches any time on that specific day
                            return criteriaBuilder.and(
                                    criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay),
                                    criteriaBuilder.lessThan(datePath, startOfNextDay)
                            );
                        case "NOT_EQUALS": case "NE": // Does not match any time on that specific day
                            return criteriaBuilder.or(
                                    criteriaBuilder.lessThan(datePath, startOfDay),
                                    criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDay)
                            );
                        case "GREATER_THAN": case "GT": return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDay); // Greater than the specified day
                        case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay); // Greater than or equal to the start of the specified day
                        case "LESS_THAN": case "LT": return criteriaBuilder.lessThan(datePath, startOfDay); // Less than the start of the specified day
                        case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.lessThan(datePath, startOfNextDay); // Less than or equal to the end of the specified day (effectively less than start of next day)
                        case "IN":
                            List<java.util.Date> inDates = parseCsvDateList(value);
                            if (inDates.isEmpty()) return criteriaBuilder.disjunction();
                            Predicate[] dayPredicates = inDates.stream().map(d -> {
                                Calendar c = Calendar.getInstance();
                                c.setTime(d);
                                c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
                                Date dayStart = c.getTime();
                                c.add(Calendar.DAY_OF_MONTH, 1);
                                Date dayEnd = c.getTime();
                                return criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(datePath, dayStart),
                                        criteriaBuilder.lessThan(datePath, dayEnd)
                                );
                            }).toArray(Predicate[]::new);
                            return criteriaBuilder.or(dayPredicates);
                        case "NOT_IN": case "OUT":
                            List<java.util.Date> notInDates = parseCsvDateList(value);
                            if (notInDates.isEmpty()) return criteriaBuilder.conjunction();
                            Predicate[] notDayPredicates = notInDates.stream().map(d -> {
                                Calendar c = Calendar.getInstance();
                                c.setTime(d);
                                c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
                                Date dayStart = c.getTime();
                                c.add(Calendar.DAY_OF_MONTH, 1);
                                Date dayEnd = c.getTime();
                                return criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(datePath, dayStart),
                                        criteriaBuilder.lessThan(datePath, dayEnd)
                                );
                            }).toArray(Predicate[]::new);
                            return criteriaBuilder.not(criteriaBuilder.or(notDayPredicates));
                        default: throw new IllegalArgumentException("Unsupported date operator: " + operator);
                    }
                } else if (Number.class.isAssignableFrom(fieldType)) {
                    if (fieldType == Long.class) {
                        Expression<Long> longPath = path.as(Long.class);
                        Long longValue = Long.parseLong(value);
                        switch (operator) {
                            case "EQUALS": case "EQ": return criteriaBuilder.equal(longPath, longValue);
                            case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(longPath, longValue);
                            case "GREATER_THAN": case "GT": return criteriaBuilder.greaterThan(longPath, longValue);
                            case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.greaterThanOrEqualTo(longPath, longValue);
                            case "LESS_THAN": case "LT": return criteriaBuilder.lessThan(longPath, longValue);
                            case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.lessThanOrEqualTo(longPath, longValue);
                            case "IN":
                                List<Long> inLongs = parseCsvLongList(value);
                                return inLongs.isEmpty() ? criteriaBuilder.disjunction() : longPath.in(inLongs);
                            case "NOT_IN": case "OUT":
                                List<Long> notInLongs = parseCsvLongList(value);
                                return notInLongs.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(longPath.in(notInLongs));
                            default: throw new IllegalArgumentException("Unsupported Long operator: " + operator);
                        }
                    } else if (fieldType == Integer.class) {
                        Expression<Integer> intPath = path.as(Integer.class);
                        Integer intValue = Integer.parseInt(value);
                        switch (operator) {
                            case "EQUALS": case "EQ": return criteriaBuilder.equal(intPath, intValue);
                            case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(intPath, intValue);
                            case "GREATER_THAN": case "GT": return criteriaBuilder.greaterThan(intPath, intValue);
                            case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.greaterThanOrEqualTo(intPath, intValue);
                            case "LESS_THAN": case "LT": return criteriaBuilder.lessThan(intPath, intValue);
                            case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.lessThanOrEqualTo(intPath, intValue);
                            case "IN":
                                List<Integer> inInts = parseCsvIntegerList(value);
                                return inInts.isEmpty() ? criteriaBuilder.disjunction() : intPath.in(inInts);
                            case "NOT_IN": case "OUT":
                                List<Integer> notInInts = parseCsvIntegerList(value);
                                return notInInts.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(intPath.in(notInInts));
                            default: throw new IllegalArgumentException("Unsupported Integer operator: " + operator);
                        }
                    } else {
                        throw new IllegalArgumentException("Unsupported Number type: " + fieldType);
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported field type for search: " + fieldType);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for field '" + field + "': " + value, e);
            } catch (IllegalArgumentException e) { // Catches parseUtilDate errors or other explicit illegal args
                throw new IllegalArgumentException("Error processing search part '" + part + "': " + e.getMessage(), e);
            } catch (Exception e) { // Catch-all for unexpected errors during predicate building
                System.err.println("Unexpected error processing CaThucHanh search part '" + part + "': " + e.getMessage());
                e.printStackTrace(); // Log appropriately
                throw new RuntimeException("Unexpected error during search predicate construction for part: " + part, e);
            }
        };
    }


    // --- Helper Methods for Parsing (copied/adapted from MonHocService example) ---
    private java.util.Date parseUtilDate(String dateString) throws IllegalArgumentException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty.");
        }
        try {
            // Consider making SimpleDateFormat a ThreadLocal or a new instance each time for thread safety
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Strict parsing
            return sdf.parse(dateString.trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd. Found: '" + dateString + "'", e);
        }
    }

    private List<String> parseCsvStringList(String csv) {
        if (csv == null || csv.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Long> parseCsvLongList(String csv) {
        if (csv == null || csv.trim().isEmpty()) return Collections.emptyList();
        try {
            return Arrays.stream(csv.split(","))
                    .map(s -> Long.parseLong(s.trim()))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV list for Long: " + csv, e);
        }
    }
    private List<Integer> parseCsvIntegerList(String csv) {
        if (csv == null || csv.trim().isEmpty()) return Collections.emptyList();
        try {
            return Arrays.stream(csv.split(","))
                    .map(s -> Integer.parseInt(s.trim()))
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in CSV list for Integer: " + csv, e);
        }
    }

    private List<java.util.Date> parseCsvDateList(String csv) {
        if (csv == null || csv.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .map(s -> parseUtilDate(s.trim())) // reuses the existing parseUtilDate
                .collect(Collectors.toList());
    }
}