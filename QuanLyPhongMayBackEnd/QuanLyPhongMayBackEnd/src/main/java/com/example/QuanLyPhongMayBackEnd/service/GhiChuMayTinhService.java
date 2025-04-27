package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuMayTinhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuMayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GhiChuMayTinhService {

    @Autowired
    private GhiChuMayTinhRepository ghiChuMayTinhRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    private boolean isUserLoggedIn(String token) {
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
            return ; // Token không hợp lệ
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
    public GhiChuMayTinh capNhat(GhiChuMayTinh ghiChuMayTinh,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.save(ghiChuMayTinh);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoNgaySua(Date ngaySua,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.findByNgaySua(ngaySua);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoNgayBaoLoi(Date ngayBaoLoi,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.findByNgayBaoLoi(ngayBaoLoi);
    }

    public List<GhiChuMayTinh> layDSGhiChuTheoMayTinh(Long maMay,String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return ghiChuMayTinhRepository.findByMayTinh_MaMay(maMay);
    }

    public GhiChuMayTinh layGhiChuGanNhatTheoMayTinh(Long maMay,String token) {
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
    @Transactional(readOnly = true) // Ensure session is open for mapping potentially lazy fields (though JOIN FETCH helps)
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

    /**
     * Maps GhiChuMayTinh entity (including related entities) to GhiChuMayTinhDTO.
     * Assumes GhiChuMayTinh entity has relationships to MayTinh, PhongMay, and TaiKhoan.
     */


}
