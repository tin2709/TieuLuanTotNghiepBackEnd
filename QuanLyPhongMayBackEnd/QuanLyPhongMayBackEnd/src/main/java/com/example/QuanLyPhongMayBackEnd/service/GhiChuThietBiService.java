package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuThietBiDTO; // Changed DTO
import com.example.QuanLyPhongMayBackEnd.entity.*; // Import necessary entities
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuThietBiRepository; // Changed Repository
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GhiChuThietBiService {

    @Autowired
    private GhiChuThietBiRepository ghiChuThietBiRepository; // Changed Repository
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TaiKhoanService taiKhoanService; // Keep TaiKhoanService for auth

    // Helper for authentication check (remains the same)
    public boolean isUserLoggedIn(String token) {
        // Implement robust token validation/authentication here
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Get note by ID
    public GhiChuThietBi layGhiChuTheoMa(Long maGhiChuTB, String token) { // Changed param name
        if (!isUserLoggedIn(token)) {
            // Consider throwing an AuthenticationException or similar
            return null;
        }
        Optional<GhiChuThietBi> kq = ghiChuThietBiRepository.findById(maGhiChuTB);
        return kq.orElse(null); // Return null if not found
    }

    // Delete note by ID
    @Transactional
    public void xoa(Long maGhiChuTB, String token) { // Changed param name
        if (!isUserLoggedIn(token)) {
            // Consider throwing an AuthenticationException or similar
            return;
        }
        if (!ghiChuThietBiRepository.existsById(maGhiChuTB)) {
            // Optional: throw NotFoundException
            return;
        }
        ghiChuThietBiRepository.deleteById(maGhiChuTB);
    }

    // Delete notes by ThietBi ID
    @Transactional
    public void xoaTheoMaThietBi(Long maThietBi, String token) { // Changed method name and param
        if (!isUserLoggedIn(token)) {
            return;
        }
        List<GhiChuThietBi> dsGhiChuThietBi = ghiChuThietBiRepository.findByThietBi_MaThietBi(maThietBi);
        ghiChuThietBiRepository.deleteAll(dsGhiChuThietBi); // More efficient bulk delete
    }

    // Save a single note
    @Transactional
    public GhiChuThietBi luu(GhiChuThietBi ghiChuThietBi, String token) {
        if (!isUserLoggedIn(token)) {
            return null;
        }
        // Add potential validation or pre-processing logic here
        return ghiChuThietBiRepository.save(ghiChuThietBi);
    }

    // Get all notes (Consider pagination for large datasets)
    public List<GhiChuThietBi> layDSGhiChu(String token) {
        if (!isUserLoggedIn(token)) {
            return new ArrayList<>(); // Return empty list instead of null
        }
        return ghiChuThietBiRepository.findAll();
    }

    // Map List Entity to List DTO
    public List<GhiChuThietBiDTO> mapToDTOList(List<GhiChuThietBi> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::mapToGhiChuThietBiDTO) // Reuse the detailed mapping method
                .collect(Collectors.toList());
    }


    // Update a single note
    @Transactional
    public GhiChuThietBi capNhat(GhiChuThietBi ghiChuThietBi, String token) {
        if (!isUserLoggedIn(token)) {
            return null;
        }
        // Check if entity exists before saving (optional, save does upsert)
        if (ghiChuThietBi.getMaGhiChuTB() == null || !ghiChuThietBiRepository.existsById(ghiChuThietBi.getMaGhiChuTB())) {
            // Consider throwing NotFoundException
            return null;
        }
        return ghiChuThietBiRepository.save(ghiChuThietBi);
    }

    // Find notes by fixed date
    public List<GhiChuThietBi> layDSGhiChuTheoNgaySua(Date ngaySua, String token) {
        if (!isUserLoggedIn(token)) {
            return new ArrayList<>();
        }
        return ghiChuThietBiRepository.findByNgaySua(ngaySua);
    }

    // Find notes by report date
    public List<GhiChuThietBi> layDSGhiChuTheoNgayBaoLoi(Date ngayBaoLoi, String token) {
        if (!isUserLoggedIn(token)) {
            return new ArrayList<>();
        }
        return ghiChuThietBiRepository.findByNgayBaoLoi(ngayBaoLoi);
    }

    // Find notes by ThietBi ID
    public List<GhiChuThietBi> layDSGhiChuTheoThietBi(Long maThietBi, String token) { // Changed method name and param
        if (!isUserLoggedIn(token)) {
            return new ArrayList<>();
        }
        return ghiChuThietBiRepository.findByThietBi_MaThietBi(maThietBi);
    }

    // Get the latest note (entity) for a ThietBi
    public GhiChuThietBi layGhiChuGanNhatTheoThietBi(Long maThietBi, String token) { // Changed method name and param
        if (!isUserLoggedIn(token)) {
            return null;
        }
        List<GhiChuThietBi> dsGhiChuThietBi = ghiChuThietBiRepository.findByThietBi_MaThietBiOrderByNgayBaoLoiDesc(maThietBi);
        return dsGhiChuThietBi.isEmpty() ? null : dsGhiChuThietBi.get(0);
    }

    // Map Entity to simpler DTO (less details, matching original GhiChuMayTinhService)
    public GhiChuThietBiDTO mapToDTO(GhiChuThietBi entity) {
        if (entity == null) {
            return null;
        }
        GhiChuThietBiDTO dto = new GhiChuThietBiDTO();
        dto.setMaGhiChuTB(entity.getMaGhiChuTB());
        dto.setNoiDung(entity.getNoiDung());
        dto.setNgayBaoLoi(entity.getNgayBaoLoi());
        dto.setNgaySua(entity.getNgaySua());

        if (entity.getThietBi() != null) {
            dto.setMaThietBi(entity.getThietBi().getMaThietBi());
            // dto.setTenThietBi(entity.getThietBi().getTenThietBi()); // Optional
        }
        if (entity.getLoaiThietBi() != null) {
            dto.setMaLoai(entity.getLoaiThietBi().getMaLoai());
            // dto.setTenLoai(entity.getLoaiThietBi().getTenLoai());// Optional
        }
        if (entity.getPhongMay() != null) {
            dto.setMaPhong(entity.getPhongMay().getMaPhong());
            // dto.setTenPhong(entity.getPhongMay().getTenPhong()); // Optional
        }
        if (entity.getTaiKhoanBaoLoi() != null) {
            dto.setMaTaiKhoanBaoLoi(entity.getTaiKhoanBaoLoi().getMaTK());
            // dto.setTenTaiKhoanBaoLoi(entity.getTaiKhoanBaoLoi().getTenDangNhap()); // Optional
        }
        if (entity.getTaiKhoanSuaLoi() != null) {
            dto.setMaTaiKhoanSuaLoi(entity.getTaiKhoanSuaLoi().getMaTK());
            // dto.setTenTaiKhoanSuaLoi(entity.getTaiKhoanSuaLoi().getTenDangNhap()); // Optional
        }
        return dto;
    }

    // Get the latest note DTO with details for a ThietBi
    @Transactional(readOnly = true)
    public GhiChuThietBiDTO layGhiChuGanNhatDTOTheoThietBi(Long maThietBi, String token) { // Changed method name and param
        if (!isUserLoggedIn(token)) {
            return null;
        }

        List<GhiChuThietBi> orderedList = ghiChuThietBiRepository.findLatestByThietBiWithDetails(maThietBi);

        if (orderedList.isEmpty()) {
            return null;
        }

        GhiChuThietBi latestGhiChu = orderedList.get(0);
        return mapToGhiChuThietBiDTO(latestGhiChu); // Use the detailed mapping method
    }

    // Helper mapping method with more details (matching original GhiChuMayTinhService 'mapToGhiChuMayTinhDTO')
    public GhiChuThietBiDTO mapToGhiChuThietBiDTO(GhiChuThietBi entity) {
        if (entity == null) {
            return null;
        }
        GhiChuThietBiDTO dto = new GhiChuThietBiDTO();
        dto.setMaGhiChuTB(entity.getMaGhiChuTB());
        dto.setNoiDung(entity.getNoiDung());
        dto.setNgayBaoLoi(entity.getNgayBaoLoi());
        dto.setNgaySua(entity.getNgaySua());

        ThietBi thietBi = entity.getThietBi();
        if (thietBi != null) {
            dto.setMaThietBi(thietBi.getMaThietBi());
            dto.setTenThietBi(thietBi.getTenThietBi()); // Include tenThietBi

            LoaiThietBi loai = thietBi.getLoaiThietBi(); // Get Loai from ThietBi
            if (loai != null) {
                dto.setMaLoai(loai.getMaLoai());
                dto.setTenLoai(loai.getTenLoai()); // Include tenLoai
            }

            PhongMay phongMay = thietBi.getPhongMay(); // Get PhongMay from ThietBi
            if (phongMay != null) {
                dto.setMaPhong(phongMay.getMaPhong());
                dto.setTenPhong(phongMay.getTenPhong()); // Include tenPhong
            }
        }

        // Explicitly map PhongMay if it's directly on GhiChuThietBi *and* not derived from ThietBi
        // (Based on GhiChuThietBi entity having its own ma_phong JoinColumn)
        PhongMay directPhongMay = entity.getPhongMay();
        if (directPhongMay != null && dto.getMaPhong() == null) { // Only if not already set via ThietBi
            dto.setMaPhong(directPhongMay.getMaPhong());
            // dto.setTenPhong(directPhongMay.getTenPhong()); // Optionally set name too
        }

        // Explicitly map LoaiThietBi if it's directly on GhiChuThietBi *and* not derived
        LoaiThietBi directLoai = entity.getLoaiThietBi();
        if (directLoai != null && dto.getMaLoai() == null) { // Only if not already set via ThietBi
            dto.setMaLoai(directLoai.getMaLoai());
            dto.setTenLoai(directLoai.getTenLoai());
        }


        TaiKhoan tkBaoLoi = entity.getTaiKhoanBaoLoi();
        if (tkBaoLoi != null) {
            dto.setMaTaiKhoanBaoLoi(tkBaoLoi.getMaTK());
            dto.setTenTaiKhoanBaoLoi(tkBaoLoi.getTenDangNhap()); // Include username
        }

        TaiKhoan tkSuaLoi = entity.getTaiKhoanSuaLoi();
        if (tkSuaLoi != null) {
            dto.setMaTaiKhoanSuaLoi(tkSuaLoi.getMaTK());
            dto.setTenTaiKhoanSuaLoi(tkSuaLoi.getTenDangNhap()); // Include username
        }

        return dto;
    }

    // --- Helper functions for parsing CSV (remain unchanged) ---
    public List<String> parseQuotedCsvString(String csvString) {
        List<String> values = new ArrayList<>();
        if (csvString == null || csvString.trim().isEmpty()) {
            return values;
        }
        String trimmedString = csvString.trim();
        if (trimmedString.startsWith("\"") && trimmedString.endsWith("\"") && trimmedString.length() >= 2) {
            trimmedString = trimmedString.substring(1, trimmedString.length() - 1);
        }
        String[] parts = trimmedString.split("\",\"");
        for (String part : parts) {
            values.add(part.trim());
        }
        if (values.size() == 1 && values.get(0).isEmpty() && csvString.trim().equals("\"\"")) {
            return new ArrayList<>();
        }
        return values;
    }

    public List<Long> parseCsvLongString(String csvString) {
        List<Long> ids = new ArrayList<>();
        if (csvString == null || csvString.trim().isEmpty()) {
            return ids;
        }
        String[] parts = csvString.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                ids.add(Long.parseLong(trimmedPart)); // Throws NumberFormatException if invalid
            }
        }
        return ids;
    }
    @Transactional
    public GhiChuThietBi capNhatNoiDungVaNguoiSuaThietBi(
            Long maGhiChuTB,
            String ngaySuaStr,
            String thoiGianBatDauStr,
            String thoiGianKetThucStr,
            Long maTKSuaLoi,
            String token
    ) throws EntityNotFoundException, IllegalArgumentException, SecurityException {

        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Token không hợp lệ hoặc người dùng chưa đăng nhập.");
        }

        // Validate input strings
        if (ngaySuaStr == null || ngaySuaStr.trim().isEmpty() ||
                thoiGianBatDauStr == null || thoiGianBatDauStr.trim().isEmpty() ||
                thoiGianKetThucStr == null || thoiGianKetThucStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Ngày sửa, thời gian bắt đầu và thời gian kết thúc không được để trống.");
        }

        // Fetch GhiChuThietBi entity
        GhiChuThietBi ghiChuThietBi = ghiChuThietBiRepository.findById(maGhiChuTB)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Ghi Chú Thiết Bị với ID: " + maGhiChuTB));

        // Fetch TaiKhoan entity for the fixer
        TaiKhoan taiKhoanSuaLoi = taiKhoanRepository.findById(String.valueOf(maTKSuaLoi))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Tài Khoản sửa lỗi với ID: " + maTKSuaLoi));

        // Prepare the scheduling information string
        String noiDungHienTai = ghiChuThietBi.getNoiDung() != null ? ghiChuThietBi.getNoiDung() : "";
        // Add a newline if existing content is not empty, for better formatting
        String separator = noiDungHienTai.isEmpty() ? "" : "\n";
        String thongTinSuaChua = separator + "(Sẽ được sửa vào ngày " + ngaySuaStr.trim() +
                " từ " + thoiGianBatDauStr.trim() +
                " đến " + thoiGianKetThucStr.trim() + ")";
        String noiDungMoi = noiDungHienTai + thongTinSuaChua;

        // Update the entity
        ghiChuThietBi.setNoiDung(noiDungMoi);
        ghiChuThietBi.setTaiKhoanSuaLoi(taiKhoanSuaLoi);
        // *** Crucially, do NOT update ghiChuThietBi.setNgaySua(...) here ***

        // Save and return the updated entity
        return ghiChuThietBiRepository.save(ghiChuThietBi);
    }
    @Transactional(readOnly = true)
    public List<GhiChuThietBiDTO> timKiemGhiChuThietBiByAdmin(String search, String token) {
        if (!isUserLoggedIn(token)) {
            return Collections.emptyList();
        }

        Specification<GhiChuThietBi> spec = buildGctbSpecification(search); // Changed method name
        if (spec == null) {
            System.err.println("Invalid search query format resulted in null specification: " + search);
            return Collections.emptyList();
        }

        List<GhiChuThietBi> results;
        try {
            results = ghiChuThietBiRepository.findAll(spec);
        } catch (Exception e) {
            System.err.println("Error executing search specification for GhiChuThietBi: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        return results.stream()
                .map(this::mapToGhiChuThietBiDTO) // Use the detailed mapper
                .collect(Collectors.toList());
    }

    private Specification<GhiChuThietBi> buildGctbSpecification(String search) { // Changed method name
        if (search == null || search.trim().isEmpty()) {
            return Specification.where(null);
        }

        String[] searchParts = search.split(";");
        Specification<GhiChuThietBi> finalSpec = Specification.where(null);

        for (String part : searchParts) {
            if (part == null || part.trim().isEmpty()) continue;

            Specification<GhiChuThietBi> partSpec = parseGctbSearchPart(part.trim()); // Changed method name
            if (partSpec != null) {
                finalSpec = finalSpec.and(partSpec);
            } else {
                System.err.println("Invalid GhiChuThietBi search part skipped: " + part);
                // Optionally: return null; // To fail entire search on one bad part
            }
        }
        return finalSpec;
    }

    private Specification<GhiChuThietBi> parseGctbSearchPart(String part) { // Changed method name
        String[] tokens = part.split(":", 3);
        if (tokens.length != 3) {
            System.err.println("Invalid GhiChuThietBi search part format: " + part);
            return null;
        }

        String field = tokens[0].trim();
        String operator = tokens[1].trim().toUpperCase();
        String value = tokens[2].trim();

        if (value.isEmpty() && !operator.equals("IS_NULL") && !operator.equals("IS_NOT_NULL")) {
            System.err.println("Empty value for operator " + operator + " in GhiChuThietBi part: " + part);
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            try {
                // Define Joins specific to GhiChuThietBi and its common relations
                Join<GhiChuThietBi, TaiKhoan> tkBaoLoiJoin = root.join("taiKhoanBaoLoi", JoinType.LEFT);
                Join<GhiChuThietBi, TaiKhoan> tkSuaLoiJoin = root.join("taiKhoanSuaLoi", JoinType.LEFT);
                // Joins needed for fields related to ThietBi:
                Join<GhiChuThietBi, ThietBi> thietBiJoin = root.join("thietBi", JoinType.LEFT);
                Join<ThietBi, LoaiThietBi> loaiJoin = thietBiJoin.join("loaiThietBi", JoinType.LEFT); // Join from ThietBi
                Join<ThietBi, PhongMay> phongJoin = thietBiJoin.join("phongMay", JoinType.LEFT); // Join from ThietBi

                Path<?> path; // Path to the field being queried

                // Map field names to correct paths using joins
                switch (field) {
                    // Fields directly on GhiChuThietBi
                    case "noiDung": path = root.get("noiDung"); break;
                    case "ngayBaoLoi": path = root.get("ngayBaoLoi"); break;
                    case "ngaySua": path = root.get("ngaySua"); break;

                    // Fields via TaiKhoan joins
                    case "tenTKBL": path = tkBaoLoiJoin.get("tenDangNhap"); break;
                    case "maTKBL": path = tkBaoLoiJoin.get("maTK"); break;
                    case "tenTKSL": path = tkSuaLoiJoin.get("tenDangNhap"); break;
                    case "maTKSL": path = tkSuaLoiJoin.get("maTK"); break;

                    // Fields via ThietBi join
                    case "maThietBi": path = thietBiJoin.get("maThietBi"); break;
                    case "tenThietBi": path = thietBiJoin.get("tenThietBi"); break;
                    // Potentially other fields on ThietBi like 'moTa', 'cauHinh', 'trangThai'...

                    // Fields via LoaiThietBi join (from ThietBi)
                    case "maLoai": path = loaiJoin.get("maLoai"); break;
                    case "tenLoai": path = loaiJoin.get("tenLoai"); break;

                    // Fields via PhongMay join (from ThietBi)
                    case "maPhong": path = phongJoin.get("maPhong"); break;
                    case "tenPhong": path = phongJoin.get("tenPhong"); break;

                    default:
                        System.err.println("Invalid GhiChuThietBi search field: " + field);
                        return criteriaBuilder.disjunction(); // Always false
                }

                // --- Operator logic (copied and verified, should work generically) ---

                if ("IS_NULL".equals(operator)) return criteriaBuilder.isNull(path);
                if ("IS_NOT_NULL".equals(operator)) return criteriaBuilder.isNotNull(path);

                Class<?> fieldType = path.getJavaType();

                if (fieldType == String.class) {
                    Expression<String> stringPath = path.as(String.class);
                    switch (operator) {
                        case "EQUALS": case "EQ": return criteriaBuilder.equal(stringPath, value);
                        case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(stringPath, value);
                        case "LIKE": return criteriaBuilder.like(stringPath, "%" + value + "%");
                        case "NOT_LIKE": return criteriaBuilder.notLike(stringPath, "%" + value + "%");
                        case "STARTS_WITH": return criteriaBuilder.like(stringPath, value + "%");
                        case "ENDS_WITH": return criteriaBuilder.like(stringPath, "%" + value);
                        case "IN":
                            List<String> inValues = parseCsvStringList(value);
                            return inValues.isEmpty() ? criteriaBuilder.disjunction() : stringPath.in(inValues);
                        case "NOT_IN": case "OUT":
                            List<String> notInValues = parseCsvStringList(value);
                            return notInValues.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(stringPath.in(notInValues));
                        default: throw new IllegalArgumentException("Unsupported string operator: " + operator);
                    }
                } else if (java.util.Date.class.isAssignableFrom(fieldType)) {
                    Expression<Date> datePath = path.as(Date.class);
                    if ("BETWEEN".equals(operator) || "NOT_BETWEEN".equals(operator)) {
                        String[] dateParts = value.split(",", 2);
                        if (dateParts.length != 2) throw new IllegalArgumentException("BETWEEN/NOT_BETWEEN requires two dates (yyyy-MM-dd,yyyy-MM-dd).");
                        Date startDate = parseDate(dateParts[0].trim());
                        Date endDate = parseDate(dateParts[1].trim());
                        if (startDate.after(endDate)) throw new IllegalArgumentException("Start date after end date for BETWEEN/NOT_BETWEEN.");

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate); cal.add(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        Date endExclusive = cal.getTime();

                        cal.setTime(startDate); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                        Date startInclusive = cal.getTime();

                        Predicate betweenPred = criteriaBuilder.and( criteriaBuilder.greaterThanOrEqualTo(datePath, startInclusive), criteriaBuilder.lessThan(datePath, endExclusive) );
                        return "BETWEEN".equals(operator) ? betweenPred : criteriaBuilder.not(betweenPred);
                    }

                    Date dateValue = parseDate(value);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateValue); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                    Date startOfDay = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    Date startOfNextDay = cal.getTime();

                    switch (operator) {
                        case "EQUALS": case "EQ": return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay), criteriaBuilder.lessThan(datePath, startOfNextDay));
                        case "NOT_EQUALS": case "NE": return criteriaBuilder.not(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay), criteriaBuilder.lessThan(datePath, startOfNextDay)));
                        case "GREATER_THAN": case "GT": return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDay);
                        case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay);
                        case "LESS_THAN": case "LT": return criteriaBuilder.lessThan(datePath, startOfDay);
                        case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.lessThan(datePath, startOfNextDay);
                        case "IN": // Simplified IN for Dates (modify if whole day matching needed)
                            List<Date> inDates = parseCsvDateList(value);
                            return inDates.isEmpty() ? criteriaBuilder.disjunction() : datePath.in(inDates);
                        case "NOT_IN": case "OUT": // Simplified NOT IN for Dates
                            List<Date> notInDates = parseCsvDateList(value);
                            return notInDates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(datePath.in(notInDates));
                        default: throw new IllegalArgumentException("Unsupported date operator: " + operator);
                    }
                } else if (Number.class.isAssignableFrom(fieldType)) {
                    Expression<Number> numberPath = path.as(Number.class);
                    // Assuming Long for IDs
                    try {
                        // Add BETWEEN/NOT_BETWEEN for numbers here if needed
                        // if ("BETWEEN".equals(operator) || "NOT_BETWEEN".equals(operator)) { ... }

                        Long longValue = Long.parseLong(value); // For single value ops
                        switch (operator) {
                            case "EQUALS": case "EQ": return criteriaBuilder.equal(numberPath, longValue);
                            case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(numberPath, longValue);
                            case "GREATER_THAN": case "GT": return criteriaBuilder.gt(numberPath, longValue);
                            case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.ge(numberPath, longValue);
                            case "LESS_THAN": case "LT": return criteriaBuilder.lt(numberPath, longValue);
                            case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.le(numberPath, longValue);
                            case "IN":
                                List<Long> inLongs = parseCsvLongList(value);
                                return inLongs.isEmpty() ? criteriaBuilder.disjunction() : numberPath.in(inLongs);
                            case "NOT_IN": case "OUT":
                                List<Long> notInLongs = parseCsvLongList(value);
                                return notInLongs.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(numberPath.in(notInLongs));
                            default: throw new IllegalArgumentException("Unsupported number operator: " + operator);
                        }
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Invalid number format for value '" + value + "'", nfe);
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported field type for search: " + fieldType);
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Error processing GhiChuThietBi search part '" + part + "': " + e.getMessage());
                return criteriaBuilder.disjunction(); // False predicate on error
            } catch (Exception e) {
                System.err.println("Unexpected error processing GhiChuThietBi search part '" + part + "': " + e.getMessage());
                e.printStackTrace();
                return criteriaBuilder.disjunction();
            }
        };
    }

    // --- Helper Methods for Parsing (Copied from GhiChuMayTinhService) ---
    private Date parseDate(String dateString) throws IllegalArgumentException {
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

    private List<String> parseCsvStringList(String csvString) {
        if (csvString == null || csvString.trim().isEmpty()) { return Collections.emptyList();}
        return Arrays.stream(csvString.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    // parseCsvLongList is already defined above
    private List<Long> parseCsvLongList(String csvString) throws IllegalArgumentException {
        if (csvString == null || csvString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> numbers = new ArrayList<>();
        String[] parts = csvString.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                try {
                    numbers.add(Long.parseLong(trimmedPart));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format in list: '" + trimmedPart + "'", e);
                }
            }
        }
        return numbers;
    }
    private List<Date> parseCsvDateList(String csvString) throws IllegalArgumentException {
        if (csvString == null || csvString.trim().isEmpty()) { return Collections.emptyList();}
        List<Date> dates = new ArrayList<>();
        String[] parts = csvString.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                // Reuse single date parser which throws exception on error
                dates.add(parseDate(trimmedPart));
            }
        }
        return dates;
    }
}