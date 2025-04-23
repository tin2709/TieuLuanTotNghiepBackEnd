package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.GhiChuMayTinhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.repository.MayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.service.GhiChuMayTinhService;
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
public class GhiChuMayTinhController {
    @Autowired
    private GhiChuMayTinhService ghiChuMayTinhService;
    @Autowired
    private MayTinhRepository mayTinhRepository;
    @Autowired
    private PhongMayRepository phongMayRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // API lưu GhiChuMayTinh
    @PostMapping("/LuuGhiChuMayTinh")
    public ResponseEntity<Object> luuGhiChuMayTinh(
            @RequestParam String noiDung,
            @RequestParam Long maMay,
            @RequestParam Long maPhong,
            @RequestParam Long maTaiKhoanBaoLoi,
            @RequestParam(required = false) Long maTaiKhoanSuaLoi,
            @RequestParam String token
    ) {
        try {
            // 1. Get references
            MayTinh mayTinhRef = mayTinhRepository.getReferenceById(maMay);
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }

            // 2. Create and setup entity
            GhiChuMayTinh ghiChuMayTinh = new GhiChuMayTinh();
            // ... set fields on ghiChuMayTinh ...
            ghiChuMayTinh.setNoiDung(noiDung);
            ghiChuMayTinh.setMayTinh(mayTinhRef);
            ghiChuMayTinh.setPhongMay(phongMayRef);
            ghiChuMayTinh.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef);
            ghiChuMayTinh.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef);
            ghiChuMayTinh.setNgayBaoLoi(new Date());


            // 3. Save the entity
            GhiChuMayTinh savedGhiChu = ghiChuMayTinhService.luu(ghiChuMayTinh, token);

            // --- MAPPING TO DTO ---
            // 4. Create the DTO to return
            GhiChuMayTinhDTO dto = new GhiChuMayTinhDTO();
            dto.setMaGhiChuMT(savedGhiChu.getMaGhiChuMT());
            dto.setNoiDung(savedGhiChu.getNoiDung());
            dto.setNgayBaoLoi(savedGhiChu.getNgayBaoLoi());
            dto.setNgaySua(savedGhiChu.getNgaySua()); // Will be null initially

            // Get IDs and potentially names from the references (proxies)
            // Accessing the getter WILL trigger lazy loading if necessary
            if (savedGhiChu.getMayTinh() != null) {
                dto.setMaMay(savedGhiChu.getMayTinh().getMaMay());
                // dto.setTenMay(savedGhiChu.getMayTinh().getTenMay()); // Optionally fetch name
            }
            if (savedGhiChu.getPhongMay() != null) {
                dto.setMaPhong(savedGhiChu.getPhongMay().getMaPhong());
                // dto.setTenPhong(savedGhiChu.getPhongMay().getTenPhong()); // Optionally fetch name
            }
            if (savedGhiChu.getTaiKhoanBaoLoi() != null) {
                dto.setMaTaiKhoanBaoLoi(savedGhiChu.getTaiKhoanBaoLoi().getMaTK());
                // dto.setTenTaiKhoanBaoLoi(savedGhiChu.getTaiKhoanBaoLoi().getTenDangNhap()); // Optionally fetch username
            }
            if (savedGhiChu.getTaiKhoanSuaLoi() != null) {
                dto.setMaTaiKhoanSuaLoi(savedGhiChu.getTaiKhoanSuaLoi().getMaTK());
                // dto.setTenTaiKhoanSuaLoi(savedGhiChu.getTaiKhoanSuaLoi().getTenDangNhap()); // Optionally fetch username
            }
            // ----------------------


            return new ResponseEntity<>(dto, HttpStatus.CREATED); // Return the DTO

        } catch (EntityNotFoundException e) {
            // ... error handling ...
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy thực thể liên quan (Máy Tính, Phòng Máy hoặc Tài Khoản) với ID được cung cấp.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // ... error handling ...
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu ghi chú máy tính: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // API lấy danh sách GhiChuMayTinh
    @GetMapping("/DSGhiChuMayTinh")
    public List<GhiChuMayTinh> layDSGhiChuMayTinh(@RequestParam String token){
        return ghiChuMayTinhService.layDSGhiChu(token);
    }

    // API lấy danh sách GhiChuMayTinh theo ngày sửa
    @GetMapping("/DSGhiChuMayTinhTheoNgaySua/{ngaySua}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoNgaySua(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngaySua,
            @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoNgaySua(ngaySua, token);
        return new ResponseEntity<>(dsGhiChuMayTinh, HttpStatus.OK);
    }

    // API lấy danh sách GhiChuMayTinh theo ngày báo lỗi
    @GetMapping("/DSGhiChuMayTinhTheoNgayBaoLoi/{ngayBaoLoi}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoNgayBaoLoi(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBaoLoi,
            @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoNgayBaoLoi(ngayBaoLoi, token);
        return new ResponseEntity<>(dsGhiChuMayTinh, HttpStatus.OK);
    }

    // API lấy GhiChuMayTinh theo mã
    @GetMapping("/GhiChuMayTinh/{maGhiChuMT}")
    public GhiChuMayTinh layGhiChuTheoMa(@RequestParam Long maGhiChuMT, @RequestParam String token){
        return ghiChuMayTinhService.layGhiChuTheoMa(maGhiChuMT, token);
    }

    // API lấy danh sách GhiChuMayTinh theo máy tính
    @GetMapping("/DSGhiChuMayTinhTheoMayTinh/{maMay}")
    public ResponseEntity<List<GhiChuMayTinh>> layDSGhiChuTheoMayTinh(@RequestParam Long maMay, @RequestParam String token) {
        List<GhiChuMayTinh> dsGhiChuMayTinh = ghiChuMayTinhService.layDSGhiChuTheoMayTinh(maMay, token);

        if (dsGhiChuMayTinh.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return new ResponseEntity<>(dsGhiChuMayTinh, HttpStatus.OK);
    }

    // API cập nhật GhiChuMayTinh
    @PutMapping("/CapNhatGhiChuMayTinh")
    public ResponseEntity<Object> capNhatGhiChuMayTinh(
            @RequestParam Long maGhiChuMT,
            @RequestParam String noiDung,
            @RequestParam Long maMay,
            @RequestParam Long maPhong,
            @RequestParam Long maTaiKhoanBaoLoi,
            @RequestParam(required = false) Long maTaiKhoanSuaLoi, // Optional fixer ID
            @RequestParam String token
    ) {
        try {
            // 1. Fetch the existing entity
            GhiChuMayTinh existingGhiChu = ghiChuMayTinhService.layGhiChuTheoMa(maGhiChuMT, token);

            // 2. Check if it exists
            if (existingGhiChu == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Không tìm thấy Ghi Chú Máy Tính với ID: " + maGhiChuMT);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            // 3. Get references for relationships
            MayTinh mayTinhRef = mayTinhRepository.getReferenceById(maMay);
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null; // Initialize as null

            // Conditionally get reference for fixer only if ID is provided
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }

            // 4. Update the standard fields
            existingGhiChu.setNoiDung(noiDung);
            existingGhiChu.setMayTinh(mayTinhRef);
            existingGhiChu.setPhongMay(phongMayRef);
            existingGhiChu.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef);
            existingGhiChu.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef); // Will be null if maTaiKhoanSuaLoi wasn't provided

            // --- Conditional Date Update Logic ---
            Date now = new Date(); // Get current time once
            if (maTaiKhoanSuaLoi != null) {
                // If fixer ID is present, update the 'fixed date'
                existingGhiChu.setNgaySua(now);
                // Do NOT touch ngayBaoLoi
            } else {
                // If fixer ID is NOT present, update the 'report date' (Semantic Caution Advised)
                existingGhiChu.setNgayBaoLoi(now);
                // Do NOT touch ngaySua (it remains whatever it was, potentially null)
            }
            // ------------------------------------

            // 5. Call the service to save
            GhiChuMayTinh updatedGhiChu = ghiChuMayTinhService.capNhat(existingGhiChu, token);

            // 6. Map to DTO
            GhiChuMayTinhDTO dto = ghiChuMayTinhService.mapToDTO(updatedGhiChu);

            return ResponseEntity.ok(dto);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy thực thể liên quan (Máy Tính, Phòng Máy hoặc Tài Khoản) với ID được cung cấp khi cập nhật.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Sentry.captureException(e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật ghi chú máy tính: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // API xóa GhiChuMayTinh
    @DeleteMapping("/XoaGhiChuMayTinh/{maGhiChuMT}")
    public String xoa(@PathVariable Long maGhiChuMT, @RequestParam String token){
        ghiChuMayTinhService.xoa(maGhiChuMT, token);
        return "Đã xoá ghi chú máy tính " + maGhiChuMT;
    }

    // API lấy ghi chú gần nhất theo máy tính
    @GetMapping("/GhiChuGanNhatTheoMayTinh") // Changed endpoint name slightly for clarity
    public ResponseEntity<GhiChuMayTinhDTO> layGhiChuGanNhatDTOTheoMayTinh(@RequestParam Long maMay, @RequestParam String token) {

        GhiChuMayTinhDTO latestDto = ghiChuMayTinhService.layGhiChuGanNhatDTOTheoMayTinh(maMay, token);

        if (latestDto != null) {
            // If DTO is found, return 200 OK with the DTO
            return new ResponseEntity<>(latestDto, HttpStatus.OK);
        } else {
            // If service returns null (not found or unauthorized based on service logic)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404
        }
    }
}
