package com.example.QuanLyPhongMayBackEnd.service;
import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuMayTinhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuMayTinhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class GhiChuMayTinhService {

    @Autowired
    private GhiChuMayTinhRepository ghiChuMayTinhRepository;
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

    public List<GhiChuMayTinh> layDSGhiChu(String token) {
        if (!isUserLoggedIn(token)) {
            return null;
        }
        return ghiChuMayTinhRepository.findAll();
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
}
