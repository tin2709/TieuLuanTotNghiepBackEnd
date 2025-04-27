package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuMayTinhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuMayTinhRepository;
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

import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;

@Service
public class GhiChuMayTinhService {

    @Autowired
    private GhiChuMayTinhRepository ghiChuMayTinhRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    public GhiChuMayTinh layGhiChuTheoMa(Long maGhiChuMT, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        GhiChuMayTinh ghiChuMayTinh = null;
        Optional<GhiChuMayTinh> kq = ghiChuMayTinhRepository.findById(maGhiChuMT);
        try {
            ghiChuMayTinh = kq.get();
            return ghiChuMayTinh;
        } catch (Exception e) {
            return ghiChuMayTinh;
        }
    }

    @Transactional
    public void xoa(Long maGhiChuMT, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        ghiChuMayTinhRepository.deleteById(maGhiChuMT);
    }

    @Transactional
    public void xoaTheoMaMay(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            return; // Token không hợp lệ
        }
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhRepository.findByMayTinh_MaMay(maMay);
        for (GhiChuMayTinh ghiChuMayTinh : dsGhiChuMayTinh) {
            ghiChuMayTinhRepository.delete(ghiChuMayTinh);
        }
    }

    public GhiChuMayTinh luu(GhiChuMayTinh ghiChuMayTinh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.save(ghiChuMayTinh);
    }

    @Transactional(readOnly = true) // Good practice for read-only operations
    public List<GhiChuMayTinhDTO> layDSGhiChu(String token) {
        if (!isUserLoggedIn(token)) {
            // Return an empty list for unauthorized access
            return Collections.emptyList();
            // Or: throw new UnauthorizedException("User is not authenticated");
        }

        // Use the repository method that fetches details
        List<GhiChuMayTinh> ghiChuList = ghiChuMayTinhRepository.findAllWithDetails();

        // Map the entities to DTOs
        return ghiChuList.stream()
                .map(this::convertToDto) // Use a helper method for mapping
                .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private GhiChuMayTinhDTO convertToDto(GhiChuMayTinh ghiChu) {
        GhiChuMayTinhDTO dto = new GhiChuMayTinhDTO();
        dto.setMaGhiChuMT(ghiChu.getMaGhiChuMT());
        dto.setNoiDung(ghiChu.getNoiDung());
        dto.setNgayBaoLoi(ghiChu.getNgayBaoLoi());
        dto.setNgaySua(ghiChu.getNgaySua()); // Will be null if not set

        // --- Map related entities (handle potential nulls) ---

        // MayTinh details
        MayTinh mayTinh = ghiChu.getMayTinh();
        if (mayTinh != null) {
            dto.setMaMay(mayTinh.getMaMay());
            dto.setTenMay(mayTinh.getTenMay()); // Assuming MayTinh has a 'tenMay' field
            // If PhongMay is ONLY linked via MayTinh (and not directly in GhiChuMayTinh)
            // PhongMay phongMayFromMayTinh = mayTinh.getPhongMay();
            // if (phongMayFromMayTinh != null) {
            //     dto.setMaPhong(phongMayFromMayTinh.getMaPhong());
            //     dto.setTenPhong(phongMayFromMayTinh.getTenPhong());
            // }
        }

        // PhongMay details (if directly linked in GhiChuMayTinh)
        PhongMay phongMay = ghiChu.getPhongMay();
        if (phongMay != null) {
            dto.setMaPhong(phongMay.getMaPhong());
            dto.setTenPhong(phongMay.getTenPhong()); // Assuming PhongMay has a 'tenPhong' field
        }

        // TaiKhoanBaoLoi details
        TaiKhoan baoLoiUser = ghiChu.getTaiKhoanBaoLoi();
        if (baoLoiUser != null) {
            dto.setMaTaiKhoanBaoLoi(baoLoiUser.getMaTK()); // Assuming TaiKhoan has 'maTaiKhoan'
            dto.setTenTaiKhoanBaoLoi(baoLoiUser.getTenDangNhap()); // Assuming TaiKhoan has 'tenTaiKhoan'
        }

        // TaiKhoanSuaLoi details
        TaiKhoan suaLoiUser = ghiChu.getTaiKhoanSuaLoi();
        if (suaLoiUser != null) {
            dto.setMaTaiKhoanSuaLoi(suaLoiUser.getMaTK());
            dto.setTenTaiKhoanSuaLoi(suaLoiUser.getTenDangNhap());
        }

        return dto;
    }

    public GhiChuMayTinh capNhat(GhiChuMayTinh ghiChuMayTinh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.save(ghiChuMayTinh);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoNgaySua(Date ngaySua, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.findByNgaySua(ngaySua);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoNgayBaoLoi(Date ngayBaoLoi, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.findByNgayBaoLoi(ngayBaoLoi);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoMayTinh(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.findByMayTinh_MaMay(maMay);
    }

    public GhiChuMayTinh layGhiChuGanNhatTheoMayTinh(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhRepository.findByMayTinh_MaMayOrderByNgayBaoLoiDesc(maMay);
        return dsGhiChuMayTinh.isEmpty() ? null : dsGhiChuMayTinh.get(0);
    }

    public GhiChuMayTinhDTO mapToDTO(GhiChuMayTinh entity) {
        if (entity == null) {
            return null;
        }
        GhiChuMayTinhDTO dto = new GhiChuMayTinhDTO();
        dto.setMaGhiChuMT(entity.getMaGhiChuMT());
        dto.setNoiDung(entity.getNoiDung());
        dto.setNgayBaoLoi(entity.getNgayBaoLoi()); // Keep original report date
        dto.setNgaySua(entity.getNgaySua());       // Reflect the new update date

        if (entity.getMayTinh() != null) {
            dto.setMaMay(entity.getMayTinh().getMaMay());
            // dto.setTenMay(entity.getMayTinh().getTenMay()); // Optional: fetch if needed
        }
        if (entity.getPhongMay() != null) {
            dto.setMaPhong(entity.getPhongMay().getMaPhong());
            // dto.setTenPhong(entity.getPhongMay().getTenPhong()); // Optional: fetch if needed
        }
        if (entity.getTaiKhoanBaoLoi() != null) {
            dto.setMaTaiKhoanBaoLoi(entity.getTaiKhoanBaoLoi().getMaTK());
            // dto.setTenTaiKhoanBaoLoi(entity.getTaiKhoanBaoLoi().getTenDangNhap()); // Optional: fetch if needed
        }
        if (entity.getTaiKhoanSuaLoi() != null) {
            dto.setMaTaiKhoanSuaLoi(entity.getTaiKhoanSuaLoi().getMaTK());
            // dto.setTenTaiKhoanSuaLoi(entity.getTaiKhoanSuaLoi().getTenDangNhap()); // Optional: fetch if needed
        }
        return dto;
    }

    @Transactional(readOnly = true)
    // Ensure session is open for mapping potentially lazy fields (though JOIN FETCH helps)
    public GhiChuMayTinhDTO layGhiChuGanNhatDTOTheoMayTinh(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Or throw exception
        }

        // Use the optimized query to fetch the latest with details
        List<GhiChuMayTinh> orderedList = ghiChuMayTinhRepository.findLatestByMayTinhWithDetails(maMay);

        if (orderedList.isEmpty()) {
            return null; // No notes found for this computer
        }

        // The first element is the latest because of ORDER BY DESC
        GhiChuMayTinh latestGhiChu = orderedList.get(0);

        // Map the latest entity to DTO
        return mapToGhiChuMayTinhDTO(latestGhiChu);
    }

    // Helper mapping method
    private GhiChuMayTinhDTO mapToGhiChuMayTinhDTO(GhiChuMayTinh entity) {
        if (entity == null) {
            return null;
        }
        GhiChuMayTinhDTO dto = new GhiChuMayTinhDTO();
        dto.setMaGhiChuMT(entity.getMaGhiChuMT());
        dto.setNoiDung(entity.getNoiDung());
        dto.setNgayBaoLoi(entity.getNgayBaoLoi());
        dto.setNgaySua(entity.getNgaySua());

        // Access related entities (should be fetched by JOIN FETCH)
        MayTinh mayTinh = entity.getMayTinh();
        if (mayTinh != null) {
            dto.setMaMay(mayTinh.getMaMay());
            dto.setTenMay(mayTinh.getTenMay()); // Include tenMay
            PhongMay phongMay = mayTinh.getPhongMay(); // Access PhongMay via MayTinh
            if (phongMay != null) {
                dto.setMaPhong(phongMay.getMaPhong());
                dto.setTenPhong(phongMay.getTenPhong()); // Include tenPhong
            }
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

    public List<String> parseQuotedCsvString(String csvString) {
        List<String> values = new ArrayList<>();
        if (csvString == null || csvString.trim().isEmpty()) {
            return values;
        }

        String trimmedString = csvString.trim();

        // Loại bỏ dấu ngoặc kép đầu và cuối nếu toàn bộ chuỗi được bọc
        // Ví dụ: "\"a\",\"b\"" -> "a\",\"b"
        if (trimmedString.startsWith("\"") && trimmedString.endsWith("\"") && trimmedString.length() >= 2) {
            trimmedString = trimmedString.substring(1, trimmedString.length() - 1);
        } else {
            // Nếu chuỗi không được bọc đúng hoặc không ở định dạng mong muốn,
            // parser đơn giản này có thể gặp vấn đề.
            // Tùy thuộc vào mức độ nghiêm ngặt bạn cần, bạn có thể ném lỗi ở đây.
            // System.err.println("Cảnh báo: Chuỗi đầu vào cho noiDung có thể không được bọc hoàn hảo: " + csvString);
        }

        // Chia nhỏ dựa trên chuỗi ngăn cách GIỮA các giá trị đã được bọc, là ","
        // Ví dụ: "a\",\"b\",\"c" -> ["a", "b", "c"]
        String[] parts = trimmedString.split("\",\"");

        // Thêm các phần vào danh sách, loại bỏ khoảng trắng ở đầu cuối mỗi phần
        for (String part : parts) {
            // Cẩn thận với trường hợp chuỗi rỗng "" sau khi split (ví dụ: input là "\"\",\"b\"")
            // part.trim() sẽ xử lý hầu hết các trường hợp khoảng trắng.
            values.add(part.trim());
        }

        // Xử lý trường hợp đặc biệt: input là "\"\"" -> list sẽ là [""]
        // Có thể muốn trả về list rỗng trong trường hợp này.
        if (values.size() == 1 && values.get(0).isEmpty() && csvString.trim().equals("\"\"")) {
            return new ArrayList<>();
        }


        return values;
    }

    /**
     * Hàm helper để parse một chuỗi chứa danh sách các số nguyên Long, ngăn cách bởi dấu phẩy.
     * Giả định định dạng như: "1,2,3"
     */
    public List<Long> parseCsvLongString(String csvString) {
        List<Long> ids = new ArrayList<>();
        if (csvString == null || csvString.trim().isEmpty()) {
            return ids;
        }
        String[] parts = csvString.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                // Long.parseLong sẽ ném NumberFormatException nếu không phải số hợp lệ,
                // được bắt bởi khối try-catch của phương thức gọi.
                ids.add(Long.parseLong(trimmedPart));
            }
        }
        return ids;
    }

    @Transactional
    public GhiChuMayTinh capNhatNoiDungVaNguoiSua(
            Long maGhiChuMT,
            String ngaySuaStr,
            String thoiGianBatDauStr,
            String thoiGianKetThucStr,
            Long maTKSuaLoi,
            String token
    ) throws EntityNotFoundException, IllegalArgumentException, SecurityException {

        if (!isUserLoggedIn(token)) { // Hoặc cơ chế kiểm tra token phù hợp
            throw new SecurityException("Token không hợp lệ hoặc người dùng chưa đăng nhập.");
        }

        // Kiểm tra các tham số đầu vào cho chuỗi nội dung
        if (ngaySuaStr == null || ngaySuaStr.trim().isEmpty() ||
                thoiGianBatDauStr == null || thoiGianBatDauStr.trim().isEmpty() ||
                thoiGianKetThucStr == null || thoiGianKetThucStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Ngày sửa, thời gian bắt đầu và thời gian kết thúc không được để trống.");
        }

        // Fetch GhiChuMayTinh entity
        GhiChuMayTinh ghiChuMayTinh = ghiChuMayTinhRepository.findById(maGhiChuMT)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Ghi Chú Máy Tính với ID: " + maGhiChuMT));

        // Fetch TaiKhoan entity cho người sửa lỗi
        // Giả sử ID Tài khoản là Long
        TaiKhoan taiKhoanSuaLoi = taiKhoanRepository.findById(String.valueOf(maTKSuaLoi))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Tài Khoản sửa lỗi với ID: " + maTKSuaLoi));

        // Lấy nội dung hiện tại và tạo chuỗi mới
        String noiDungHienTai = ghiChuMayTinh.getNoiDung() != null ? ghiChuMayTinh.getNoiDung() : "";
        String thongTinSuaChua = "\n(Sẽ được sửa vào ngày " + ngaySuaStr.trim() +
                " từ " + thoiGianBatDauStr.trim() +
                " đến " + thoiGianKetThucStr.trim() + ")";
        String noiDungMoi = noiDungHienTai + thongTinSuaChua;

        // Cập nhật entity
        ghiChuMayTinh.setNoiDung(noiDungMoi);
        ghiChuMayTinh.setTaiKhoanSuaLoi(taiKhoanSuaLoi);
        // Không cập nhật ghiChuMayTinh.setNgaySua(...)

        // Save và trả về entity đã cập nhật
        return ghiChuMayTinhRepository.save(ghiChuMayTinh);
    }

    @Transactional(readOnly = true)
    public List<GhiChuMayTinhDTO> timKiemGhiChuMayTinhByAdmin(String search, String token) {
        if (!isUserLoggedIn(token)) {
            return Collections.emptyList(); // Return empty list for unauthorized
        }

        Specification<GhiChuMayTinh> spec = buildGcmSpecification(search);
        if (spec == null) {
            System.err.println("Invalid search query format resulted in null specification: " + search);
            return Collections.emptyList(); // Return empty list for invalid format
        }

        List<GhiChuMayTinh> results;
        try {
            results = ghiChuMayTinhRepository.findAll(spec);
        } catch (Exception e) {
            // Catch potential exceptions during query execution (e.g., bad criteria)
            System.err.println("Error executing search specification: " + e.getMessage());
            // Optionally log the full stack trace
            e.printStackTrace();
            return Collections.emptyList(); // Return empty list on query error
        }


        // Use the detailed mapping method
        return results.stream()
                .map(this::mapToGhiChuMayTinhDTO)
                .collect(Collectors.toList());
    }

    private Specification<GhiChuMayTinh> buildGcmSpecification(String search) {
        if (search == null || search.trim().isEmpty()) {
            return Specification.where(null); // No criteria means find all (or none depending on interpretation)
        }

        String[] searchParts = search.split(";"); // Split conditions by ';' (AND)
        Specification<GhiChuMayTinh> finalSpec = Specification.where(null); // Start with an empty spec

        for (String part : searchParts) {
            if (part == null || part.trim().isEmpty()) continue; // Skip empty parts

            Specification<GhiChuMayTinh> partSpec = parseGcmSearchPart(part.trim());
            if (partSpec != null) {
                finalSpec = finalSpec.and(partSpec); // Combine with AND
            } else {
                // Log the invalid part
                System.err.println("Invalid search part skipped due to parsing error: " + part);
                // Decide whether to fail the whole search or just ignore the bad part.
                // Failing fast might be better: uncomment below to stop on first error.
                // return null;
            }
        }
        return finalSpec;
    }

    private Specification<GhiChuMayTinh> parseGcmSearchPart(String part) {
        String[] tokens = part.split(":", 3); // field:operator:value

        if (tokens.length != 3) {
            System.err.println("Invalid search part format (expected field:operator:value): " + part);
            return null; // Invalid syntax
        }

        String field = tokens[0].trim();
        String operator = tokens[1].trim().toUpperCase();
        String value = tokens[2].trim();

        // Check for empty value where it's not allowed
        if (value.isEmpty() && !operator.equals("IS_NULL") && !operator.equals("IS_NOT_NULL")) {
            System.err.println("Empty value provided for operator " + operator + " which requires a value, in part: " + part);
            return null; // Value required but not provided
        }


        return (root, query, criteriaBuilder) -> {
            try { // Add try-catch block inside lambda for better error isolation
                // Define Joins (use LEFT JOIN to handle optional relationships)
                Join<GhiChuMayTinh, MayTinh> mayTinhJoin = root.join("mayTinh", JoinType.LEFT);
                Join<GhiChuMayTinh, PhongMay> phongMayJoin = root.join("phongMay", JoinType.LEFT);
                Join<GhiChuMayTinh, TaiKhoan> tkBaoLoiJoin = root.join("taiKhoanBaoLoi", JoinType.LEFT);
                Join<GhiChuMayTinh, TaiKhoan> tkSuaLoiJoin = root.join("taiKhoanSuaLoi", JoinType.LEFT);

                Path<?> path; // Use Path<?> to hold the target field path

                // Determine the correct path based on the field name
                switch (field) {
                    case "noiDung": path = root.get("noiDung"); break;
                    case "tenMay": path = mayTinhJoin.get("tenMay"); break;
                    case "tenPhong": path = phongMayJoin.get("tenPhong"); break;
                    case "tenTKBL": path = tkBaoLoiJoin.get("tenDangNhap"); break;
                    case "tenTKSL": path = tkSuaLoiJoin.get("tenDangNhap"); break;
                    case "ngayBaoLoi": path = root.get("ngayBaoLoi"); break;
                    case "ngaySua": path = root.get("ngaySua"); break;
                    case "maMay": path = mayTinhJoin.get("maMay"); break;
                    case "maPhong": path = phongMayJoin.get("maPhong"); break;
                    case "maTKBL": path = tkBaoLoiJoin.get("maTK"); break;
                    case "maTKSL": path = tkSuaLoiJoin.get("maTK"); break;
                    default:
                        System.err.println("Invalid search field specified: " + field);
                        // Return a predicate that is always false for an invalid field
                        return criteriaBuilder.disjunction();
                }

                // --- Handle IS_NULL / IS_NOT_NULL first ---
                if ("IS_NULL".equals(operator)) {
                    return criteriaBuilder.isNull(path);
                }
                if ("IS_NOT_NULL".equals(operator)) {
                    return criteriaBuilder.isNotNull(path);
                }

                // --- Handle based on field type ---
                Class<?> fieldType = path.getJavaType();

                if (fieldType == String.class) {
                    Expression<String> stringPath = path.as(String.class);
                    switch (operator) {
                        case "EQUALS": case "EQ": return criteriaBuilder.equal(stringPath, value);
                        case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(stringPath, value);
                        // Consider adding LOWER for case-insensitive comparison if needed:
                        // case "EQ_IGNORE_CASE": return criteriaBuilder.equal(criteriaBuilder.lower(stringPath), value.toLowerCase());
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
                        default:
                            System.err.println("Unsupported string operator: " + operator + " for field " + field);
                            return criteriaBuilder.disjunction();
                    }
                    // ***** CORRECTED DATE CHECK *****
                } else if (java.util.Date.class.isAssignableFrom(fieldType)) {
                    // *******************************
                    Expression<Date> datePath = path.as(Date.class);
                    // BETWEEN / NOT_BETWEEN logic
                    if ("BETWEEN".equals(operator) || "NOT_BETWEEN".equals(operator)) {
                        String[] dateParts = value.split(",", 2);
                        if (dateParts.length != 2) {
                            throw new IllegalArgumentException("BETWEEN/NOT_BETWEEN requires two dates (yyyy-MM-dd,yyyy-MM-dd). Found: " + value);
                        }
                        Date startDate = parseDate(dateParts[0].trim()); // Throws IllegalArgumentException on error
                        Date endDate = parseDate(dateParts[1].trim());   // Throws IllegalArgumentException on error

                        if (startDate.after(endDate)) {
                            throw new IllegalArgumentException("Start date cannot be after end date for BETWEEN/NOT_BETWEEN. Found: " + value);
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        cal.set(Calendar.HOUR_OF_DAY, 0); // Start of the day AFTER the end date
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        Date startOfNextDayFromEnd = cal.getTime();

                        // Ensure start date is also at the start of the day for consistent comparison
                        cal.setTime(startDate);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        Date startOfStartDate = cal.getTime();


                        Predicate betweenPredicate = criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(datePath, startOfStartDate),      // Inclusive start
                                criteriaBuilder.lessThan(datePath, startOfNextDayFromEnd) // Exclusive end (includes whole end day)
                        );

                        return "BETWEEN".equals(operator) ? betweenPredicate : criteriaBuilder.not(betweenPredicate);
                        // Explicit OR logic for NOT_BETWEEN (handles nulls potentially better depending on DB):
                        // if ("BETWEEN".equals(operator)) {
                        //     return betweenPredicate;
                        // } else { // NOT_BETWEEN
                        //     return criteriaBuilder.or(
                        //         criteriaBuilder.lessThan(datePath, startOfStartDate),
                        //         criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDayFromEnd)
                        //     );
                        // }
                    }

                    // Single date operators
                    Date dateValue = parseDate(value); // Throws IllegalArgumentException on error

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateValue);
                    cal.set(Calendar.HOUR_OF_DAY, 0); // Start of the specified day
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date startOfDay = cal.getTime();

                    cal.add(Calendar.DAY_OF_MONTH, 1); // Start of the next day
                    Date startOfNextDay = cal.getTime();

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
                        // Or simply: return criteriaBuilder.not(criteriaBuilder.and(
                        //     criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay),
                        //     criteriaBuilder.lessThan(datePath, startOfNextDay)
                        // ));
                        case "GREATER_THAN": case "GT":
                            return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfNextDay); // On or after start of next day
                        case "GREATER_THAN_OR_EQUAL": case "GTE":
                            return criteriaBuilder.greaterThanOrEqualTo(datePath, startOfDay); // On or after start of specified day
                        case "LESS_THAN": case "LT":
                            return criteriaBuilder.lessThan(datePath, startOfDay); // Before start of specified day
                        case "LESS_THAN_OR_EQUAL": case "LTE":
                            return criteriaBuilder.lessThan(datePath, startOfNextDay); // Before start of next day
                        case "IN": // Date IN is tricky, matching exact timestamp or whole day?
                            List<Date> inDates = parseCsvDateList(value); // Throws IllegalArgumentException on error
                            // Simple timestamp IN (likely not what's usually wanted)
                            // return inDates.isEmpty() ? criteriaBuilder.disjunction() : datePath.in(inDates);
                            // For whole day IN (more complex):
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
                            List<Date> notInDates = parseCsvDateList(value); // Throws IllegalArgumentException on error
                            // Simple timestamp NOT IN
                            // return notInDates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(datePath.in(notInDates));
                            // For whole day NOT IN:
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
                            System.err.println("Unsupported date operator: " + operator + " for field " + field);
                            return criteriaBuilder.disjunction();
                    }
                } else if (Number.class.isAssignableFrom(fieldType)) {
                    Expression<Number> numberPath = path.as(Number.class);
                    // Add BETWEEN/NOT_BETWEEN for numbers if needed here
                    // ...

                    // Single value number operators
                    try {
                        Long longValue = Long.parseLong(value); // Use Long as common type for IDs
                        switch (operator) {
                            case "EQUALS": case "EQ": return criteriaBuilder.equal(numberPath, longValue);
                            case "NOT_EQUALS": case "NE": return criteriaBuilder.notEqual(numberPath, longValue);
                            case "GREATER_THAN": case "GT": return criteriaBuilder.gt(numberPath, longValue);
                            case "GREATER_THAN_OR_EQUAL": case "GTE": return criteriaBuilder.ge(numberPath, longValue);
                            case "LESS_THAN": case "LT": return criteriaBuilder.lt(numberPath, longValue);
                            case "LESS_THAN_OR_EQUAL": case "LTE": return criteriaBuilder.le(numberPath, longValue);
                            case "IN":
                                List<Long> inLongs = parseCsvLongList(value); // Throws IllegalArgumentException on error
                                return inLongs.isEmpty() ? criteriaBuilder.disjunction() : numberPath.in(inLongs);
                            case "NOT_IN": case "OUT":
                                List<Long> notInLongs = parseCsvLongList(value); // Throws IllegalArgumentException on error
                                return notInLongs.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.not(numberPath.in(notInLongs));
                            default:
                                System.err.println("Unsupported number operator: " + operator + " for field " + field);
                                return criteriaBuilder.disjunction();
                        }
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Invalid number format for value '" + value + "' in part: " + part, nfe);
                    }
                } else {
                    System.err.println("Unsupported field type for search: " + fieldType + " for field " + field);
                    return criteriaBuilder.disjunction(); // Unsupported type
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Error processing search part '" + part + "': " + e.getMessage());
                // Returning false predicate on any processing error within the lambda
                return criteriaBuilder.disjunction();
            } catch (Exception e) {
                // Catch unexpected errors during predicate creation
                System.err.println("Unexpected error processing search part '" + part + "': " + e.getMessage());
                e.printStackTrace(); // Log stack trace for debugging
                return criteriaBuilder.disjunction();
            }
        };
    }

    // --- Helper Methods for Parsing ---

    private Date parseDate(String dateString) throws IllegalArgumentException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty.");
        }
        try {
            // Create new instance each time - SimpleDateFormat is not thread-safe
            SimpleDateFormat threadSafeDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            threadSafeDateFormat.setLenient(false); // Important: Disallow invalid dates
            return threadSafeDateFormat.parse(dateString.trim());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd. Found: '" + dateString + "'", e);
        }
    }

    private List<String> parseCsvStringList(String csvString) {
        if (csvString == null || csvString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(csvString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

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
        if (csvString == null || csvString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Date> dates = new ArrayList<>();
        String[] parts = csvString.split(",");
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty()) {
                try {
                    dates.add(parseDate(trimmedPart)); // Reuse single date parser (throws exception)
                } catch (IllegalArgumentException e) {
                    // Re-throw with context about the list if needed, or let original exception propagate
                    throw new IllegalArgumentException("Invalid date format found in list: '" + trimmedPart + "' in '" + csvString + "'", e);
                }
            }
        }
        return dates;
    }


    /**
     * Maps GhiChuMayTinh entity (including related entities) to GhiChuMayTinhDTO.
     * Assumes GhiChuMayTinh entity has relationships to MayTinh, PhongMay, and TaiKhoan.
     */


}
