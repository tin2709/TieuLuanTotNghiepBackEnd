package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.MonHocDTO;
import com.example.QuanLyPhongMayBackEnd.entity.MonHoc;
import com.example.QuanLyPhongMayBackEnd.repository.MonHocRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException; // Thêm import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Đổi sang Spring @Transactional


import java.text.ParseException;
import java.text.SimpleDateFormat;
// import java.sql.Date; // Không nên dùng java.sql.Date cho việc parse từ String
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonHocService {

    @Autowired
    private MonHocRepository monHocRepository;
    @Autowired
    private TaiKhoanService taiKhoanService; // Assuming TaiKhoanService is available for isUserLoggedIn

    public boolean isUserLoggedIn(String token) {
        // Assuming TaiKhoanService has a method like this:
        // Replace with your actual authentication check
        Map<String, Object> loginStatus = taiKhoanService.checkUserLoginStatus(token);
        return loginStatus != null && "success".equals(loginStatus.get("status"));
    }

    @Cacheable(value = "monhoc", key = "#maMon")
    public MonHoc layMonHocTheoMa(Long maMon,String token) {
        if (!isUserLoggedIn(token)) {
            // Cân nhắc ném AccessDeniedException thay vì trả về null
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return null;
        }
        return monHocRepository.findById(maMon).orElse(null);
    }

    @Cacheable(value = "monhocs_all") // Đổi tên cache key để phân biệt
    public List<MonHoc> layDSMonHoc(String token) {
        if (!isUserLoggedIn(token)) {
            // throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
            return null;
        }
        return monHocRepository.findAll();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "monhocs_all", allEntries = true),
            @CacheEvict(value = "monhoc", key = "#maMon")
    })
    public void xoa(Long maMon, String token) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }
        if (!monHocRepository.existsById(maMon)) {
            throw new EntityNotFoundException("Không tìm thấy môn học với mã: " + maMon);
        }
        try {
            monHocRepository.deleteById(maMon);
        } catch (EmptyResultDataAccessException e) { // Bắt lỗi cụ thể hơn nếu có
            throw new EntityNotFoundException("Không tìm thấy môn học với mã: " + maMon + " để xóa.");
        }
    }

    @Transactional
    @Caching(
            evict = { @CacheEvict(value = "monhocs_all", allEntries = true) }
    )
    public MonHoc luu(MonHoc monHoc, String token) {
        if (!isUserLoggedIn(token)) {
            return null;
        }
        if (monHoc.getNgayKetThuc() != null && monHoc.getNgayBatDau() != null &&
                monHoc.getNgayKetThuc().before(monHoc.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu.");
        }
        return monHocRepository.save(monHoc);
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "monhoc", key = "#monHocDetails.maMon") },
            evict = { @CacheEvict(value = "monhocs_all", allEntries = true) }
    )
    public MonHoc capNhatMonHoc(MonHoc monHocDetails, String token) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }

        MonHoc existingMonHoc = monHocRepository.findById(monHocDetails.getMaMon())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy môn học với mã: " + monHocDetails.getMaMon()));

        if (monHocDetails.getNgayKetThuc() != null && monHocDetails.getNgayBatDau() != null &&
                monHocDetails.getNgayKetThuc().before(monHocDetails.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu.");
        }

        existingMonHoc.setTenMon(monHocDetails.getTenMon());
        existingMonHoc.setNgayBatDau(monHocDetails.getNgayBatDau());
        existingMonHoc.setNgayKetThuc(monHocDetails.getNgayKetThuc());
        existingMonHoc.setSoBuoi(monHocDetails.getSoBuoi());

        return monHocRepository.save(existingMonHoc);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "monhocs_all", allEntries = true),
            @CacheEvict(value = "monhoc", allEntries = true)
    })
    public int xoaNhieuMonHoc(List<Long> maMonList, String token) {
        if (!isUserLoggedIn(token)) {
            throw new AccessDeniedException("Token không hợp lệ hoặc đã hết hạn.");
        }
        if (maMonList == null || maMonList.isEmpty()) {
            return 0;
        }

        int soLuongDaXoa = 0;
        List<Long> khongTimThay = new ArrayList<>();

        for (Long maMon : maMonList) {
            if (monHocRepository.existsById(maMon)) {
                monHocRepository.deleteById(maMon);
                soLuongDaXoa++;
            } else {
                khongTimThay.add(maMon);
            }
        }

        if (!khongTimThay.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy các môn học với mã: " + khongTimThay.toString());
        }
        return soLuongDaXoa;
    }


    public List<MonHocDTO> timKiemMonHoc(String keyword, String token) {
        if (!isUserLoggedIn(token)) {
            return new ArrayList<>();
        }

        String[] parts = keyword.split(":", 3);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Định dạng keyword không hợp lệ. Mong muốn: 'column:operator:value' hoặc 'column:operator:'.");
        }

        String column = parts[0].trim();
        String operator = parts[1].trim().toUpperCase();
        String value = (parts.length == 3) ? parts[2].trim() : null;

        List<String> validColumns = Arrays.asList("tenMon", "ngayBatDau", "ngayKetThuc", "soBuoi", "maMon");
        if (!validColumns.contains(column)) {
            throw new IllegalArgumentException("Tên cột không hợp lệ: " + column);
        }

        Specification<MonHoc> specification = (root, query, cb) -> {
            try {
                switch (operator) {
                    case "EQUALS":
                        if (value == null) return cb.isNull(root.get(column));
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc")) return cb.equal(root.get(column).as(java.sql.Date.class), parseSqlDate(value));
                        if (column.equals("soBuoi") || column.equals("maMon")) return cb.equal(root.get(column), Integer.parseInt(value));
                        return cb.equal(root.get(column), value);
                    case "NOT_EQUALS":
                        if (value == null) return cb.isNotNull(root.get(column));
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc")) return cb.notEqual(root.get(column).as(java.sql.Date.class), parseSqlDate(value));
                        if (column.equals("soBuoi")|| column.equals("maMon")) return cb.notEqual(root.get(column), Integer.parseInt(value));
                        return cb.notEqual(root.get(column), value);
                    case "LIKE":
                        return cb.like(cb.lower(root.get(column)), "%" + value.toLowerCase() + "%");
                    case "NOT_LIKE":
                        return cb.notLike(cb.lower(root.get(column)), "%" + value.toLowerCase() + "%");
                    case "STARTS_WITH":
                        return cb.like(cb.lower(root.get(column)), value.toLowerCase() + "%");
                    case "ENDS_WITH":
                        return cb.like(cb.lower(root.get(column)), "%" + value.toLowerCase());
                    case "GREATER_THAN":
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc")) return cb.greaterThan(root.get(column).as(java.sql.Date.class), parseSqlDate(value));
                        return cb.greaterThan(root.get(column).as(Integer.class), Integer.parseInt(value));
                    case "GREATER_THAN_OR_EQUAL":
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc")) return cb.greaterThanOrEqualTo(root.get(column).as(java.sql.Date.class), parseSqlDate(value));
                        return cb.greaterThanOrEqualTo(root.get(column).as(Integer.class), Integer.parseInt(value));
                    case "LESS_THAN":
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc")) return cb.lessThan(root.get(column).as(java.sql.Date.class), parseSqlDate(value));
                        return cb.lessThan(root.get(column).as(Integer.class), Integer.parseInt(value));
                    case "LESS_THAN_OR_EQUAL":
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc")) return cb.lessThanOrEqualTo(root.get(column).as(java.sql.Date.class), parseSqlDate(value));
                        return cb.lessThanOrEqualTo(root.get(column).as(Integer.class), Integer.parseInt(value));
                    case "BETWEEN":
                        String[] dates = value.split(",");
                        if(dates.length != 2) throw new IllegalArgumentException("BETWEEN cần 2 giá trị ngày.");
                        if (column.equals("ngayBatDau") || column.equals("ngayKetThuc"))
                            return cb.between(root.get(column).as(java.sql.Date.class), parseSqlDate(dates[0]), parseSqlDate(dates[1]));
                        throw new IllegalArgumentException("BETWEEN chỉ hỗ trợ cho kiểu ngày.");
                    case "IS_NULL":
                        return cb.isNull(root.get(column));
                    case "IS_NOT_NULL":
                        return cb.isNotNull(root.get(column));
                    default:
                        throw new IllegalArgumentException("Toán tử không hợp lệ: " + operator);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Giá trị không phải là số hợp lệ cho cột " + column + ": " + value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Định dạng ngày không hợp lệ cho giá trị '" + value + "'. Sử dụng yyyy-MM-dd.");
            }
        };

        List<MonHoc> results = monHocRepository.findAll(specification);
        return results.stream()
                .map(this::mapToMonHocDTO)
                .collect(Collectors.toList());
    }

    // Helper method for the old timKiemMonHoc
    private java.sql.Date parseSqlDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(dateStr);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ: '" + dateStr + "'. Sử dụng yyyy-MM-dd.", e);
        }
    }

    // --- New methods for searchMonHocByAdmin ---
    @Transactional(readOnly = true)
    public List<MonHocDTO> timKiemMonHocByAdmin(String search, String token) {
        if (!isUserLoggedIn(token)) {
            return Collections.emptyList();
        }

        Specification<MonHoc> spec = buildMonHocSpecification(search);
        if (spec == null) {
            System.err.println("Invalid search query format resulted in null specification: " + search);
            return Collections.emptyList();
        }

        List<MonHoc> results;
        try {
            results = monHocRepository.findAll(spec);
        } catch (Exception e) {
            System.err.println("Error executing search specification for MonHoc: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        return results.stream()
                .map(this::mapToMonHocDTO)
                .collect(Collectors.toList());
    }

    private MonHocDTO mapToMonHocDTO(MonHoc monHoc) {
        return new MonHocDTO(
                monHoc.getMaMon(),
                monHoc.getTenMon(),
                monHoc.getNgayBatDau(),
                monHoc.getNgayKetThuc(),
                monHoc.getSoBuoi()
        );
    }

    private Specification<MonHoc> buildMonHocSpecification(String search) {
        if (search == null || search.trim().isEmpty()) {
            return Specification.where(null);
        }

        String[] searchParts = search.split(";");
        Specification<MonHoc> finalSpec = Specification.where(null);

        for (String part : searchParts) {
            if (part == null || part.trim().isEmpty()) continue;

            Specification<MonHoc> partSpec = parseMonHocSearchPart(part.trim());
            if (partSpec != null) {
                finalSpec = finalSpec.and(partSpec);
            } else {
                System.err.println("Invalid search part for MonHoc skipped due to parsing error: " + part);
                // return null; // Fail fast option
            }
        }
        return finalSpec;
    }

    private Specification<MonHoc> parseMonHocSearchPart(String part) {
        String[] tokens = part.split(":", 3);

        if (tokens.length != 3) {
            System.err.println("Invalid MonHoc search part format (expected field:operator:value): " + part);
            return null;
        }

        String field = tokens[0].trim();
        String operator = tokens[1].trim().toUpperCase();
        String value = tokens[2].trim();

        if (value.isEmpty() && !operator.equals("IS_NULL") && !operator.equals("IS_NOT_NULL")) {
            System.err.println("Empty value provided for operator " + operator + " which requires a value, in MonHoc part: " + part);
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            try {
                Path<?> path;
                switch (field) {
                    case "maMon": path = root.get("maMon"); break;
                    case "tenMon": path = root.get("tenMon"); break;
                    case "ngayBatDau": path = root.get("ngayBatDau"); break;
                    case "ngayKetThuc": path = root.get("ngayKetThuc"); break;
                    case "soBuoi": path = root.get("soBuoi"); break;
                    default:
                        System.err.println("Invalid MonHoc search field specified: " + field);
                        return criteriaBuilder.disjunction(); // Always false
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
                        default:
                            System.err.println("Unsupported string operator for MonHoc: " + operator + " for field " + field);
                            return criteriaBuilder.disjunction();
                    }
                } else if (java.util.Date.class.isAssignableFrom(fieldType)) {
                    Expression<java.util.Date> datePath = path.as(java.util.Date.class);
                    if ("BETWEEN".equals(operator) || "NOT_BETWEEN".equals(operator)) {
                        String[] dateParts = value.split(",", 2);
                        if (dateParts.length != 2) throw new IllegalArgumentException("BETWEEN/NOT_BETWEEN requires two dates (yyyy-MM-dd,yyyy-MM-dd). Found: " + value);
                        java.util.Date startDate = parseUtilDate(dateParts[0].trim());
                        java.util.Date endDate = parseUtilDate(dateParts[1].trim());
                        if (startDate.after(endDate)) throw new IllegalArgumentException("Start date cannot be after end date. Found: " + value);

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        java.util.Date startOfNextDayFromEnd = cal.getTime();

                        cal.setTime(startDate);
                        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        java.util.Date startOfStartDate = cal.getTime();

                        Predicate betweenPredicate = criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(datePath, startOfStartDate),
                                criteriaBuilder.lessThan(datePath, startOfNextDayFromEnd)
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
                        case "EQUALS": case "EQ":
                            return criteriaBuilder.and(
                                    criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay),
                                    criteriaBuilder.lessThan(datePath, startOfNextDay)
                            );
                        case "NOT_EQUALS": case "NE":
                            return criteriaBuilder.or(
                                    criteriaBuilder.lessThan(datePath, startOfDay),
                                    criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDay)
                            );
                        case "GREATER_THAN": case "GT": return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDay);
                        case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay);
                        case "LESS_THAN": case "LT": return criteriaBuilder.lessThan(datePath, startOfDay);
                        case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.lessThan(datePath, startOfNextDay);
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
                        default:
                            System.err.println("Unsupported date operator for MonHoc: " + operator + " for field " + field);
                            return criteriaBuilder.disjunction();
                    }
                } else if (Number.class.isAssignableFrom(fieldType)) {
                    // Handle maMon (Long) and soBuoi (Integer)
                    if (fieldType == Long.class) {
                        Expression<Long> longPath = path.as(Long.class);
                        Long longValue;
                        try {
                            longValue = Long.parseLong(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid number format for Long field '" + field + "': " + value, e);
                        }
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
                            default:
                                System.err.println("Unsupported Long operator for MonHoc: " + operator + " for field " + field);
                                return criteriaBuilder.disjunction();
                        }
                    } else if (fieldType == Integer.class) {
                        Expression<Integer> intPath = path.as(Integer.class);
                        Integer intValue;
                        try {
                            intValue = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid number format for Integer field '" + field + "': " + value, e);
                        }
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
                            default:
                                System.err.println("Unsupported Integer operator for MonHoc: " + operator + " for field " + field);
                                return criteriaBuilder.disjunction();
                        }
                    } else {
                        System.err.println("Unsupported Number type for MonHoc: " + fieldType + " for field " + field);
                        return criteriaBuilder.disjunction();
                    }
                } else {
                    System.err.println("Unsupported field type for MonHoc search: " + fieldType + " for field " + field);
                    return criteriaBuilder.disjunction();
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error processing MonHoc search part '" + part + "': " + e.getMessage());
                return criteriaBuilder.disjunction();
            } catch (Exception e) {
                System.err.println("Unexpected error processing MonHoc search part '" + part + "': " + e.getMessage());
                e.printStackTrace();
                return criteriaBuilder.disjunction();
            }
        };
    }

    // --- Helper Methods for Parsing (copied/adapted from GhiChuMayTinh example) ---

    // Using java.util.Date for consistency with GhiChuMayTinh and advanced date logic
    private java.util.Date parseUtilDate(String dateString) throws IllegalArgumentException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty.");
        }
        try {
            SimpleDateFormat threadSafeDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            threadSafeDateFormat.setLenient(false);
            return threadSafeDateFormat.parse(dateString.trim());
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