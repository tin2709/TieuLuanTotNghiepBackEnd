package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuThietBiDTO; // Changed DTO
import com.example.QuanLyPhongMayBackEnd.entity.*; // Import necessary entities
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuThietBiRepository; // Changed Repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GhiChuThietBiService {

    @Autowired
    private GhiChuThietBiRepository ghiChuThietBiRepository; // Changed Repository
    @Autowired
    private TaiKhoanService taiKhoanService; // Keep TaiKhoanService for auth

    // Helper for authentication check (remains the same)
    private boolean isUserLoggedIn(String token) {
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
}