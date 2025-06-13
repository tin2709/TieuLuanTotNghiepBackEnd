package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuThietBiDTO; // Changed DTO
import com.example.QuanLyPhongMayBackEnd.entity.*; // Import necessary entities
import com.example.QuanLyPhongMayBackEnd.repository.LoaiThietBiRepository; // Added Repository
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.ThietBiRepository; // Changed Repository
import com.example.QuanLyPhongMayBackEnd.service.GhiChuThietBiService; // Changed Service
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin // Configure CORS as needed
public class GhiChuThietBiController {

    @Autowired
    private GhiChuThietBiService ghiChuThietBiService; // Changed Service
    @Autowired
    private ThietBiRepository thietBiRepository;     // Changed Repository
    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository; // Added Repository
    @Autowired
    private PhongMayRepository phongMayRepository;     // Keep PhongMay Repo
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;     // Keep TaiKhoan Repo

    // API to save a single ThietBi Note
    @PostMapping("/LuuGhiChuThietBi") // Changed endpoint
    public ResponseEntity<Object> luuGhiChuThietBi( // Changed method name
                                                    @RequestParam String noiDung,
                                                    @RequestParam Long maThietBi,           // Changed parameter
                                                    // @RequestParam Long maLoai,            // Optional: Can be derived from maThietBi
                                                    @RequestParam Long maPhong,
                                                    @RequestParam Long maTaiKhoanBaoLoi,
                                                    @RequestParam(required = false) Long maTaiKhoanSuaLoi,
                                                    @RequestParam String token
    ) {
        try {
            // 1. Get references
            ThietBi thietBiRef = thietBiRepository.getReferenceById(maThietBi);
            // Derive LoaiThietBi from ThietBi (Requires ThietBi to have LoaiThietBi loaded or accessible)
            // Ensure ThietBi entity has EAGER fetch for LoaiThietBi or handle potential LazyInitializationException
            LoaiThietBi loaiThietBiRef = thietBiRef.getLoaiThietBi();
            if (loaiThietBiRef == null) {
                // Handle case where ThietBi doesn't have a LoaiThietBi linked or fetch fails
                throw new EntityNotFoundException("Không thể tìm thấy Loại Thiết Bị liên kết với Thiết Bị ID: " + maThietBi);
            }
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }

            // 2. Create and setup entity
            GhiChuThietBi ghiChuThietBi = new GhiChuThietBi(); // Changed entity
            ghiChuThietBi.setNoiDung(noiDung);
            ghiChuThietBi.setThietBi(thietBiRef);         // Set ThietBi
            ghiChuThietBi.setLoaiThietBi(loaiThietBiRef); // Set derived LoaiThietBi
            ghiChuThietBi.setPhongMay(phongMayRef);
            ghiChuThietBi.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef);
            ghiChuThietBi.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef);
            ghiChuThietBi.setNgayBaoLoi(new Date());

            // 3. Save the entity using the service
            GhiChuThietBi savedGhiChu = ghiChuThietBiService.luu(ghiChuThietBi, token);

            // 4. Map to DTO
            GhiChuThietBiDTO dto = ghiChuThietBiService.mapToGhiChuThietBiDTO(savedGhiChu); // Use detailed mapping

            return new ResponseEntity<>(dto, HttpStatus.CREATED); // Return the DTO

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy thực thể liên quan (Thiết Bị, Loại TB, Phòng Máy hoặc Tài Khoản) với ID được cung cấp. Chi tiết: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu ghi chú thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API get list of GhiChuThietBi (Returns DTOs)
    @GetMapping("/DSGhiChuThietBi") // Changed endpoint
    public ResponseEntity<List<GhiChuThietBiDTO>> layDSGhiChuThietBi(@RequestParam String token) { // Changed return type
        List<GhiChuThietBi> entities = ghiChuThietBiService.layDSGhiChu(token);
        if (entities == null) { // Handle null from service (e.g., bad token)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>()); // Or FORBIDDEN
        }
        List<GhiChuThietBiDTO> dtos = ghiChuThietBiService.mapToDTOList(entities); // Use list mapping
        return ResponseEntity.ok(dtos);
    }

    // API get list by fixed date (Returns DTOs)
    @GetMapping("/DSGhiChuThietBiTheoNgaySua/{ngaySua}") // Changed endpoint
    public ResponseEntity<List<GhiChuThietBiDTO>> layDSGhiChuTheoNgaySua( // Changed return type
                                                                          @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
                                                                          @RequestParam String token) {
        List<GhiChuThietBi> entities = ghiChuThietBiService.layDSGhiChuTheoNgaySua(ngaySua, token);
        if (entities == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }
        List<GhiChuThietBiDTO> dtos = ghiChuThietBiService.mapToDTOList(entities);
        return ResponseEntity.ok(dtos);
    }

    // API get list by report date (Returns DTOs)
    @GetMapping("/DSGhiChuThietBiTheoNgayBaoLoi/{ngayBaoLoi}") // Changed endpoint
    public ResponseEntity<List<GhiChuThietBiDTO>> layDSGhiChuTheoNgayBaoLoi( // Changed return type
                                                                             @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
                                                                             @RequestParam String token) {
        List<GhiChuThietBi> entities = ghiChuThietBiService.layDSGhiChuTheoNgayBaoLoi(ngayBaoLoi, token);
        if (entities == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }
        List<GhiChuThietBiDTO> dtos = ghiChuThietBiService.mapToDTOList(entities);
        return ResponseEntity.ok(dtos);
    }

    // API get note by ID (Returns DTO)
    @GetMapping("/GhiChuThietBi/{maGhiChuTB}") // Changed endpoint and path variable name
    public ResponseEntity<GhiChuThietBiDTO> layGhiChuTheoMa( // Changed return type
                                                             @PathVariable Long maGhiChuTB, // Changed param name
                                                             @RequestParam String token) {
        GhiChuThietBi entity = ghiChuThietBiService.layGhiChuTheoMa(maGhiChuTB, token);
        if (entity == null) {
            // Could be not found or unauthorized
            return ResponseEntity.notFound().build();
        }
        GhiChuThietBiDTO dto = ghiChuThietBiService.mapToGhiChuThietBiDTO(entity); // Use detailed mapping
        return ResponseEntity.ok(dto);
    }

    // API get list by ThietBi ID (Returns DTOs)
    @GetMapping("/DSGhiChuThietBiTheoThietBi/{maThietBi}") // Changed endpoint and path variable
    public ResponseEntity<List<GhiChuThietBiDTO>> layDSGhiChuTheoThietBi( // Changed method name, param, return type
                                                                          @PathVariable Long maThietBi, // Changed param name
                                                                          @RequestParam String token) {
        List<GhiChuThietBi> entities = ghiChuThietBiService.layDSGhiChuTheoThietBi(maThietBi, token);
        if (entities == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }
        if (entities.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>()); // Return empty list if no notes found
        }
        List<GhiChuThietBiDTO> dtos = ghiChuThietBiService.mapToDTOList(entities);
        return ResponseEntity.ok(dtos);
    }

    // API update single GhiChuThietBi
    @PutMapping("/CapNhatGhiChuThietBi") // Changed endpoint
    public ResponseEntity<Object> capNhatGhiChuThietBi( // Changed method name
                                                        @RequestParam Long maGhiChuTB,           // Changed parameter
                                                        @RequestParam String noiDung,
                                                        @RequestParam Long maThietBi,           // Changed parameter
                                                        // @RequestParam Long maLoai,            // Optional: Derive from ThietBi
                                                        @RequestParam Long maPhong,
                                                        @RequestParam Long maTaiKhoanBaoLoi,
                                                        @RequestParam(required = false) Long maTaiKhoanSuaLoi,
                                                        @RequestParam String token
    ) {
        try {
            // 1. Fetch the existing entity
            GhiChuThietBi existingGhiChu = ghiChuThietBiService.layGhiChuTheoMa(maGhiChuTB, token);

            // 2. Check if it exists and if authorized (service handles auth)
            if (existingGhiChu == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy Ghi Chú Thiết Bị với ID: " + maGhiChuTB + " hoặc không có quyền truy cập.");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            // 3. Get references for relationships
            ThietBi thietBiRef = thietBiRepository.getReferenceById(maThietBi);
            LoaiThietBi loaiThietBiRef = thietBiRef.getLoaiThietBi(); // Derive from ThietBi
            if (loaiThietBiRef == null) {
                throw new EntityNotFoundException("Không thể tìm thấy Loại Thiết Bị liên kết với Thiết Bị ID: " + maThietBi);
            }
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }

            // 4. Update the fields
            existingGhiChu.setNoiDung(noiDung);
            existingGhiChu.setThietBi(thietBiRef);
            existingGhiChu.setLoaiThietBi(loaiThietBiRef); // Update derived Loai
            existingGhiChu.setPhongMay(phongMayRef);
            existingGhiChu.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef);
            existingGhiChu.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef);

            // --- Conditional Date Update Logic ---
            Date now = new Date();
            if (maTaiKhoanSuaLoi != null && existingGhiChu.getNgaySua() == null) { // Only set if not already set
                existingGhiChu.setNgaySua(now);
            }
            // Consider if ngayBaoLoi should be updated on general updates? Usually not.
            // else {
            //     existingGhiChu.setNgayBaoLoi(now); // Original logic, review if needed
            // }
            // ------------------------------------

            // 5. Call the service to save
            GhiChuThietBi updatedGhiChu = ghiChuThietBiService.capNhat(existingGhiChu, token);

            if (updatedGhiChu == null) { // Handle potential null from service (e.g., update failed authorization)
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Cập nhật thất bại, có thể do không có quyền.");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // Or INTERNAL_SERVER_ERROR
            }


            // 6. Map to DTO
            GhiChuThietBiDTO dto = ghiChuThietBiService.mapToGhiChuThietBiDTO(updatedGhiChu); // Use detailed mapping

            return ResponseEntity.ok(dto);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy thực thể liên quan (Thiết Bị, Loại TB, Phòng Máy hoặc Tài Khoản) với ID được cung cấp khi cập nhật. Chi tiết: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật ghi chú thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API delete GhiChuThietBi
    @DeleteMapping("/XoaGhiChuThietBi/{maGhiChuTB}") // Changed endpoint and path variable
    public ResponseEntity<Map<String, String>> xoa( // Changed return type for better response
                                                    @PathVariable Long maGhiChuTB, // Changed param name
                                                    @RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        try {
            // Service should handle auth check
            ghiChuThietBiService.xoa(maGhiChuTB, token);
            // Optional: check if deletion actually happened if service doesn't throw exception
            response.put("message", "Đã xoá ghi chú thiết bị " + maGhiChuTB);
            return ResponseEntity.ok(response);
        } catch (Exception e) { // Catch potential exceptions from service/repo
            Sentry.captureException(e);
            response.put("message", "Lỗi khi xóa ghi chú thiết bị ID " + maGhiChuTB + ": " + e.getMessage());
            // Determine appropriate status code based on exception type if possible
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }


    }

    // API get latest note DTO by ThietBi ID
    @GetMapping("/GhiChuThietBiGanNhatTheoThietBi") // Changed endpoint
    public ResponseEntity<GhiChuThietBiDTO> layGhiChuGanNhatDTOTheoThietBi( // Changed method name and param
                                                                            @RequestParam Long maThietBi, // Changed param name
                                                                            @RequestParam String token) {

        GhiChuThietBiDTO latestDto = ghiChuThietBiService.layGhiChuGanNhatDTOTheoThietBi(maThietBi, token);

        if (latestDto != null) {
            return ResponseEntity.ok(latestDto);
        } else {
            // Could be not found or unauthorized
            return ResponseEntity.notFound().build();
        }
    }

    // API save multiple GhiChuThietBi
    @PostMapping("/LuuNhieuGhiChuThietBi") // Changed endpoint
    public ResponseEntity<Object> luuNhieuGhiChuThietBi( // Changed method name
                                                         @RequestParam String noiDung,       // Expecting "\"Nội dung 1\",\"Nội dung 2\""
                                                         @RequestParam String maThietBi,     // Expecting "1,2,3" (Changed param name)
                                                         @RequestParam Long maPhong,
                                                         @RequestParam Long maTaiKhoanBaoLoi,
                                                         @RequestParam(required = false) Long maTaiKhoanSuaLoi,
                                                         @RequestParam String token
    ) {
        try {
            // 1. Parse input strings
            List<String> noiDungList = ghiChuThietBiService.parseQuotedCsvString(noiDung);
            List<Long> maThietBiList = ghiChuThietBiService.parseCsvLongString(maThietBi); // Changed var name

            // 2. Validate input sizes
            if (noiDungList.size() != maThietBiList.size()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Số lượng nội dung (" + noiDungList.size() + ") và mã thiết bị (" + maThietBiList.size() + ") phải khớp nhau.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            if (noiDungList.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Danh sách nội dung và mã thiết bị không được rỗng.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // 3. Get common references
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }

            // 4. Process each item
            List<GhiChuThietBiDTO> savedDTOs = new ArrayList<>();
            Date now = new Date();

            for (int i = 0; i < maThietBiList.size(); i++) {
                Long currentMaThietBi = maThietBiList.get(i);
                String currentNoiDung = noiDungList.get(i);

                // Get references for the current ThietBi and its LoaiThietBi
                ThietBi thietBiRef = thietBiRepository.getReferenceById(currentMaThietBi);
                LoaiThietBi loaiThietBiRef = thietBiRef.getLoaiThietBi();
                if (loaiThietBiRef == null) {
                    throw new EntityNotFoundException("Không thể tìm thấy Loại Thiết Bị liên kết với Thiết Bị ID: " + currentMaThietBi + " trong danh sách.");
                }


                GhiChuThietBi ghiChuThietBi = new GhiChuThietBi(); // Changed entity
                ghiChuThietBi.setNoiDung(currentNoiDung);
                ghiChuThietBi.setThietBi(thietBiRef);         // Set ThietBi
                ghiChuThietBi.setLoaiThietBi(loaiThietBiRef); // Set derived Loai
                ghiChuThietBi.setPhongMay(phongMayRef);
                ghiChuThietBi.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef);
                ghiChuThietBi.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef);
                ghiChuThietBi.setNgayBaoLoi(now); // Set report date

                // 5. Save using service
                GhiChuThietBi savedGhiChu = ghiChuThietBiService.luu(ghiChuThietBi, token);

                // 6. Map to DTO
                GhiChuThietBiDTO dto = ghiChuThietBiService.mapToGhiChuThietBiDTO(savedGhiChu); // Use detailed mapping
                savedDTOs.add(dto);
            }

            return new ResponseEntity<>(savedDTOs, HttpStatus.CREATED);

        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Định dạng mã thiết bị không hợp lệ. Vui lòng cung cấp danh sách các số nguyên ngăn cách bởi dấu phẩy.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy một hoặc nhiều thực thể liên quan (Thiết Bị, Loại TB, Phòng Máy hoặc Tài Khoản) với ID được cung cấp. Chi tiết: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu danh sách ghi chú thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API update multiple GhiChuThietBi
    @PutMapping("/CapNhatNhieuGhiChuThietBi") // Changed endpoint
    public ResponseEntity<Object> capNhatNhieuGhiChuThietBi( // Changed method name
                                                             @RequestParam String maGhiChuTB,   // Expecting "1,2,3" (Changed param name)
                                                             @RequestParam String noiDung,      // Expecting "\"Nội dung 1\",\"Nội dung 2\""
                                                             @RequestParam String maThietBi,    // Expecting "10,11,12" (Changed param name)
                                                             @RequestParam Long maPhong,
                                                             @RequestParam Long maTaiKhoanBaoLoi,
                                                             @RequestParam(required = false) Long maTaiKhoanSuaLoi,
                                                             @RequestParam String token
    ) {
        try {
            // 1. Parse input strings
            List<Long> maGhiChuTBList = ghiChuThietBiService.parseCsvLongString(maGhiChuTB);
            List<String> noiDungList = ghiChuThietBiService.parseQuotedCsvString(noiDung);
            List<Long> maThietBiList = ghiChuThietBiService.parseCsvLongString(maThietBi);

            // 2. Validate input sizes
            if (!(maGhiChuTBList.size() == noiDungList.size() && maGhiChuTBList.size() == maThietBiList.size())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Số lượng mã ghi chú (" + maGhiChuTBList.size() + "), nội dung (" + noiDungList.size() + "), và mã thiết bị (" + maThietBiList.size() + ") phải khớp nhau.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            if (maGhiChuTBList.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Danh sách mã ghi chú, nội dung, và mã thiết bị không được rỗng.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // 3. Get common references
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }

            // 4. Process each item
            List<GhiChuThietBiDTO> updatedDTOs = new ArrayList<>();
            Date now = new Date();

            for (int i = 0; i < maGhiChuTBList.size(); i++) {
                Long currentMaGhiChuTB = maGhiChuTBList.get(i);
                String currentNoiDung = noiDungList.get(i);
                Long currentMaThietBi = maThietBiList.get(i);

                // Fetch the existing entity
                GhiChuThietBi existingGhiChu = ghiChuThietBiService.layGhiChuTheoMa(currentMaGhiChuTB, token);
                if (existingGhiChu == null) {
                    throw new EntityNotFoundException("Không tìm thấy Ghi Chú Thiết Bị với ID: " + currentMaGhiChuTB + " hoặc không có quyền.");
                }

                // Get references for current ThietBi and Loai
                ThietBi thietBiRef = thietBiRepository.getReferenceById(currentMaThietBi);
                LoaiThietBi loaiThietBiRef = thietBiRef.getLoaiThietBi();
                if (loaiThietBiRef == null) {
                    throw new EntityNotFoundException("Không thể tìm thấy Loại Thiết Bị liên kết với Thiết Bị ID: " + currentMaThietBi + " trong danh sách.");
                }

                // Update the fetched entity
                existingGhiChu.setNoiDung(currentNoiDung);
                existingGhiChu.setThietBi(thietBiRef);
                existingGhiChu.setLoaiThietBi(loaiThietBiRef); // Update derived Loai
                existingGhiChu.setPhongMay(phongMayRef);
                existingGhiChu.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef);
                existingGhiChu.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef);

                // --- Conditional Date Update Logic ---
                if (maTaiKhoanSuaLoi != null && existingGhiChu.getNgaySua() == null) { // Only set if not already set
                    existingGhiChu.setNgaySua(now);
                }
                // else {
                //    existingGhiChu.setNgayBaoLoi(now); // Original logic, review if needed
                // }
                // ------------------------------------

                // 5. Update using service
                GhiChuThietBi updatedGhiChu = ghiChuThietBiService.capNhat(existingGhiChu, token);
                if (updatedGhiChu == null) {
                    // Handle error, maybe skip this item or throw an exception for the whole batch
                    System.err.println("Cập nhật thất bại cho Ghi Chú Thiết Bị ID: " + currentMaGhiChuTB);
                    continue; // Skip this item and proceed with others
                }

                // 6. Map to DTO
                GhiChuThietBiDTO dto = ghiChuThietBiService.mapToGhiChuThietBiDTO(updatedGhiChu); // Use detailed mapping
                updatedDTOs.add(dto);
            }

            return new ResponseEntity<>(updatedDTOs, HttpStatus.OK);

        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Định dạng ID không hợp lệ trong danh sách (mã ghi chú, mã thiết bị). Vui lòng cung cấp danh sách các số nguyên ngăn cách bởi dấu phẩy.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy một hoặc nhiều thực thể liên quan (Ghi Chú TB, Thiết Bị, Loại TB, Phòng Máy hoặc Tài Khoản) với ID được cung cấp khi cập nhật. Chi tiết: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật danh sách ghi chú thiết bị: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/CapNhatNguoiSuaVaThoiGianSuaThietBi") // Changed endpoint name
    public ResponseEntity<Object> capNhatNguoiSuaVaThoiGianSuaThietBi( // Changed method name
                                                                       @RequestParam Long maGhiChuTB, // Changed parameter name
                                                                       @RequestParam String ngaySuaStr,
                                                                       @RequestParam String thoiGianBatDau,
                                                                       @RequestParam String thoiGianKetThuc,
                                                                       @RequestParam Long maTKSuaLoi,
                                                                       @RequestParam String token
    ) {
        try {
            // Call the corresponding service method
            GhiChuThietBi updatedGhiChuEntity = ghiChuThietBiService.capNhatNoiDungVaNguoiSuaThietBi(
                    maGhiChuTB,
                    ngaySuaStr,
                    thoiGianBatDau,
                    thoiGianKetThuc,
                    maTKSuaLoi,
                    token
            );

            // Map the updated entity to DTO using the detailed mapping method
            GhiChuThietBiDTO dto = ghiChuThietBiService.mapToGhiChuThietBiDTO(updatedGhiChuEntity);

            // Log info (optional)
            System.out.println("Ghi chú Thiết Bị " + maGhiChuTB + ": Nội dung được cập nhật thông tin lịch sửa bởi TK " + maTKSuaLoi);

            // Return the DTO in the success response
            return ResponseEntity.ok(dto);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400
        } catch (SecurityException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401
        } catch (Exception e) {
            // Log the detailed error
            System.err.println("Lỗi hệ thống khi cập nhật nội dung ghi chú thiết bị: " + e.getMessage());
            e.printStackTrace(); // Print stack trace to server logs
            Sentry.captureException(e); // Send to Sentry or other logging system

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi hệ thống không mong muốn xảy ra. Vui lòng thử lại sau."); // Generic message for client
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping("/searchGhiChuThietBiByAdmin")
    public ResponseEntity<Map<String, Object>> searchGhiChuThietBiByAdmin(
            @RequestParam String keyword, // e.g., "tenThietBi:LIKE:Router;tenPhong:EQ:P301"
            @RequestParam String token) {

        // Authentication Check (delegate to service or check here)
        // Using TaiKhoanService directly for this example
        if (!ghiChuThietBiService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate search parameter
        if (keyword == null || keyword.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Search parameter cannot be empty.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Perform the search using the service method
            List<GhiChuThietBiDTO> results = ghiChuThietBiService.timKiemGhiChuThietBiByAdmin(keyword, token);

            Map<String, Object> response = new HashMap<>();
            response.put("results", results);
            response.put("size", results.size());

            // Determine status based on results
            HttpStatus status = (results == null || results.isEmpty()) ? HttpStatus.NO_CONTENT : HttpStatus.OK;

            return new ResponseEntity<>(response, status);

        } catch (IllegalArgumentException e) {
            // Catch specific exceptions thrown by parsing helpers in the service
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid search query format or value: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Catch unexpected errors during search execution
            Sentry.captureException(e);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "An internal error occurred during the search: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/CapNhatNhieuGhiChuThietBiKhiSuaXong")
    public ResponseEntity<Object> capNhatNhieuGhiChuThietBiKhiSuaXong(
            @RequestParam String maGhiChuTBIds, // Danh sách mã ghi chú thiết bị, ví dụ: "1,2,5,7"
            @RequestParam String userName,       // Username của người đã sửa lỗi
            @RequestParam String token
    ) {
        try {
            // 1. Parse the list of GhiChuThietBi IDs
            List<Long> idsToUpdate = ghiChuThietBiService.parseCsvLongString(maGhiChuTBIds);

            if (idsToUpdate.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Danh sách mã ghi chú thiết bị không được rỗng.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // 2. Call the service to perform the batch update
            // Service will handle finding TaiKhoan by username and updating notes
            Map<String, Object> serviceResult = ghiChuThietBiService.capNhatNhieuGhiChuThietBiKhiSuaXong(
                    idsToUpdate, userName, token);

            @SuppressWarnings("unchecked") // Safe cast as we know the type from service
            List<GhiChuThietBiDTO> updatedNotes = (List<GhiChuThietBiDTO>) serviceResult.get("updatedNotes");
            @SuppressWarnings("unchecked") // Safe cast
            List<String> failedUpdates = (List<String>) serviceResult.get("failedUpdates");

            // 3. Construct the response based on success/failure of batch updates
            if (updatedNotes.isEmpty() && !failedUpdates.isEmpty()) {
                // All updates failed
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không có ghi chú nào được cập nhật. Chi tiết lỗi: " + String.join("; ", failedUpdates));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            } else if (!failedUpdates.isEmpty()) {
                // Some succeeded, some failed (Partial Content)
                Map<String, Object> partialSuccessResponse = new HashMap<>();
                partialSuccessResponse.put("message", "Cập nhật hoàn tất với một số lỗi. Các ghi chú không được cập nhật: " + String.join("; ", failedUpdates));
                partialSuccessResponse.put("updatedNotes", updatedNotes);
                return new ResponseEntity<>(partialSuccessResponse, HttpStatus.PARTIAL_CONTENT); // HTTP 206
            } else {
                // All succeeded
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("message", "Cập nhật thành công các ghi chú đã chọn.");
                successResponse.put("updatedNotes", updatedNotes);
                return ResponseEntity.ok(successResponse); // HTTP 200
            }

        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Định dạng mã ghi chú thiết bị không hợp lệ. Vui lòng cung cấp danh sách các số nguyên ngăn cách bởi dấu phẩy.");
            System.err.println("NumberFormatException in capNhatNhieuGhiChuThietBiKhiSuaXong: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (SecurityException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            // This catch block handles the initial lookup for userName if not found by service
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            System.err.println("EntityNotFoundException in capNhatNhieuGhiChuThietBiKhiSuaXong: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật nhiều ghi chú thiết bị: " + e.getMessage());
            System.err.println("Unexpected Exception in capNhatNhieuGhiChuThietBiKhiSuaXong: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
