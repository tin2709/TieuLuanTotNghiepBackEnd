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

import java.text.ParseException;
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
    public List<GhiChuMayTinhDTO> layDSGhiChuMayTinh(@RequestParam String token){
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
    @PostMapping("/LuuNhieuGhiChuMayTinh")
    public ResponseEntity<Object> luuNhieuGhiChuMayTinh(
            @RequestParam String noiDung,
            @RequestParam String maMay,
            @RequestParam Long maPhong,
            @RequestParam Long maTaiKhoanBaoLoi,
            @RequestParam(required = false) Long maTaiKhoanSuaLoi,
            @RequestParam String token
    ) {
        try {
            // 1. Parse input strings into lists
            // Hàm parseQuotedCsvString được thiết kế để xử lý định dạng "\"Nội dung 1\",\"Nội dung 2\""
            List<String> noiDungList = ghiChuMayTinhService.parseQuotedCsvString(noiDung);
            List<Long> maMayList = ghiChuMayTinhService.parseCsvLongString(maMay);

            // 2. Validate input sizes
            if (noiDungList.size() != maMayList.size()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Số lượng nội dung (" + noiDungList.size() + ") và mã máy tính (" + maMayList.size() + ") phải khớp nhau.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            if (noiDungList.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Danh sách nội dung và mã máy tính không được rỗng.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }


            // 3. Get common references (do this once before the loop)
            // Sử dụng Long cho getReferenceById dựa trên kiểu của @RequestParam
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }
            // Lưu ý: EntityNotFoundException cho các tham chiếu này sẽ được bắt bởi khối catch bên dưới.

            // 4. Process each item, create entities, and save
            List<GhiChuMayTinhDTO> savedDTOs = new ArrayList<>();

            // Vòng lặp này sẽ gán nội dung thứ i cho mã máy thứ i
            for (int i = 0; i < maMayList.size(); i++) {
                Long currentMaMay = maMayList.get(i);
                String currentNoiDung = noiDungList.get(i);

                // Lấy tham chiếu cho máy tính hiện tại (cần thực hiện trong vòng lặp)
                MayTinh mayTinhRef = mayTinhRepository.getReferenceById(currentMaMay);
                // Lưu ý: EntityNotFoundException cho Máy Tính cụ thể này cũng sẽ được bắt.

                // Tạo và thiết lập thực thể GhiChuMayTinh cho cặp nội dung/mã máy hiện tại
                GhiChuMayTinh ghiChuMayTinh = new GhiChuMayTinh();
                ghiChuMayTinh.setNoiDung(currentNoiDung);
                ghiChuMayTinh.setMayTinh(mayTinhRef);
                ghiChuMayTinh.setPhongMay(phongMayRef); // Sử dụng tham chiếu chung
                ghiChuMayTinh.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef); // Sử dụng tham chiếu chung
                ghiChuMayTinh.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef); // Sử dụng tham chiếu chung (có thể null)
                ghiChuMayTinh.setNgayBaoLoi(new Date()); // Đặt ngày/giờ hiện tại

                // 5. Lưu thực thể bằng service
                GhiChuMayTinh savedGhiChu = ghiChuMayTinhService.luu(ghiChuMayTinh, token); // Giả định service xử lý logic lưu và xác thực token

                // 6. Ánh xạ thực thể đã lưu sang DTO và thêm vào danh sách
                GhiChuMayTinhDTO dto = new GhiChuMayTinhDTO();
                dto.setMaGhiChuMT(savedGhiChu.getMaGhiChuMT());
                dto.setNoiDung(savedGhiChu.getNoiDung());
                dto.setNgayBaoLoi(savedGhiChu.getNgayBaoLoi());
                dto.setNgaySua(savedGhiChu.getNgaySua()); // Ban đầu sẽ là null

                // Lấy ID và tùy chọn tên từ các tham chiếu (proxies)
                if (savedGhiChu.getMayTinh() != null) {
                    dto.setMaMay(savedGhiChu.getMayTinh().getMaMay());
                    // dto.setTenMay(savedGhiChu.getMayTinh().getTenMay()); // Tùy chọn lấy tên
                }
                if (savedGhiChu.getPhongMay() != null) {
                    dto.setMaPhong(savedGhiChu.getPhongMay().getMaPhong());
                    // dto.setTenPhong(savedGhiChu.getPhongMay().getTenPhong()); // Tùy chọn lấy tên
                }
                if (savedGhiChu.getTaiKhoanBaoLoi() != null) {
                    dto.setMaTaiKhoanBaoLoi(savedGhiChu.getTaiKhoanBaoLoi().getMaTK());
                    // dto.setTenTaiKhoanBaoLoi(savedGhiChu.getTaiKhoanBaoLoi().getTenDangNhap()); // Tùy chọn lấy tên đăng nhập
                }
                if (savedGhiChu.getTaiKhoanSuaLoi() != null) {
                    dto.setMaTaiKhoanSuaLoi(savedGhiChu.getTaiKhoanSuaLoi().getMaTK());
                    // dto.setTenTaiKhoanSuaLoi(savedGhiChu.getTaiKhoanSuaLoi().getTenDangNhap()); // Tùy chọn lấy tên đăng nhập
                }

                savedDTOs.add(dto);
            }


            return new ResponseEntity<>(savedDTOs, HttpStatus.CREATED);

        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Định dạng mã máy tính không hợp lệ. Vui lòng cung cấp danh sách các số nguyên ngăn cách bởi dấu phẩy.");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Không tìm thấy một hoặc nhiều thực thể liên quan (Máy Tính, Phòng Máy hoặc Tài Khoản) với ID được cung cấp.");

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi lưu danh sách ghi chú máy tính: " + e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/CapNhatNhieuGhiChuMayTinh")
    public ResponseEntity<Object> capNhatNhieuGhiChuMayTinh(
            @RequestParam String maGhiChuMT,
            @RequestParam String noiDung,
            @RequestParam String maMay,
            @RequestParam Long maPhong,
            @RequestParam Long maTaiKhoanBaoLoi,
            @RequestParam(required = false) Long maTaiKhoanSuaLoi,
            @RequestParam String token
    ) {
        try {
            // 1. Parse input strings into lists
            List<Long> maGhiChuMTList = ghiChuMayTinhService.parseCsvLongString(maGhiChuMT);
            List<String> noiDungList = ghiChuMayTinhService.parseQuotedCsvString(noiDung);
            List<Long> maMayList = ghiChuMayTinhService.parseCsvLongString(maMay);

            // 2. Validate input sizes
            if (maGhiChuMTList.size() != noiDungList.size() || maGhiChuMTList.size() != maMayList.size()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Số lượng mã ghi chú (" + maGhiChuMTList.size() + "), nội dung (" + noiDungList.size() + "), và mã máy tính (" + maMayList.size() + ") phải khớp nhau.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            if (maGhiChuMTList.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Danh sách mã ghi chú, nội dung, và mã máy tính không được rỗng.");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }


            // 3. Get common references (do this once before the loop)
            // Sử dụng Long cho getReferenceById dựa trên kiểu của @RequestParam
            PhongMay phongMayRef = phongMayRepository.getReferenceById(maPhong);
            TaiKhoan taiKhoanBaoLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanBaoLoi));
            TaiKhoan taiKhoanSuaLoiRef = null;
            if (maTaiKhoanSuaLoi != null) {
                taiKhoanSuaLoiRef = taiKhoanRepository.getReferenceById(String.valueOf(maTaiKhoanSuaLoi));
            }
            // Lưu ý: EntityNotFoundException cho các tham chiếu này sẽ được bắt bởi khối catch bên dưới.

            // 4. Process each item, fetch existing, update, and save
            List<GhiChuMayTinhDTO> updatedDTOs = new ArrayList<>();
            Date now = new Date(); // Get current time once for all updates

            // Loop through the lists based on index
            for (int i = 0; i < maGhiChuMTList.size(); i++) {
                Long currentMaGhiChuMT = maGhiChuMTList.get(i);
                String currentNoiDung = noiDungList.get(i);
                Long currentMaMay = maMayList.get(i);

                // Fetch the existing entity for the current ghiChuMT
                // Assuming layGhiChuTheoMa handles token validation and returns null or throws
                GhiChuMayTinh existingGhiChu = ghiChuMayTinhService.layGhiChuTheoMa(currentMaGhiChuMT, token);

                if (existingGhiChu == null) {

                    throw new EntityNotFoundException("Không tìm thấy Ghi Chú Máy Tính với ID: " + currentMaGhiChuMT);
                }
                // Get reference for the current machine (needs to be done in the loop)
                MayTinh mayTinhRef = mayTinhRepository.getReferenceById(currentMaMay);
                // Note: EntityNotFoundException for this specific MayTinh will also be caught.


                // Update the fetched entity with current item data and common references
                existingGhiChu.setNoiDung(currentNoiDung);
                existingGhiChu.setMayTinh(mayTinhRef);
                existingGhiChu.setPhongMay(phongMayRef); // Use common reference
                existingGhiChu.setTaiKhoanBaoLoi(taiKhoanBaoLoiRef); // Use common reference
                existingGhiChu.setTaiKhoanSuaLoi(taiKhoanSuaLoiRef); // Use common reference (can be null)

                // --- Conditional Date Update Logic (Replicated) ---
                if (maTaiKhoanSuaLoi != null) {
                    // If fixer ID is present for the batch, update the 'fixed date' for this item
                    existingGhiChu.setNgaySua(now);
                    // Do NOT touch ngayBaoLoi
                } else {
                    // If fixer ID is NOT present for the batch, update the 'report date' for this item
                    existingGhiChu.setNgayBaoLoi(now); // Replicating original logic
                    // Do NOT touch ngaySua
                }
                // ------------------------------------


                // 5. Call the service to update the entity
                // Assuming capNhat handles persistence and token/permission checks
                GhiChuMayTinh updatedGhiChu = ghiChuMayTinhService.capNhat(existingGhiChu, token);

                // 6. Map the updated entity to DTO and add to list
                GhiChuMayTinhDTO dto = ghiChuMayTinhService.mapToDTO(updatedGhiChu); // Reuse mapToDTO

                updatedDTOs.add(dto);
            }

            // 7. Return the list of updated DTOs
            return new ResponseEntity<>(updatedDTOs, HttpStatus.OK); // Use OK (200) for successful update

        } catch (NumberFormatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Định dạng ID không hợp lệ trong danh sách (mã ghi chú, mã máy). Vui lòng cung cấp danh sách các số nguyên ngăn cách bởi dấu phẩy.");
            // Log the error if needed
            // e.g., Sentry.captureException(e);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            // Make error message more specific
            errorResponse.put("message", "Không tìm thấy một hoặc nhiều thực thể liên quan (Ghi Chú Máy Tính, Máy Tính, Phòng Máy hoặc Tài Khoản) với ID được cung cấp khi cập nhật: " + e.getMessage());
            // Log the specific entity not found if possible from the exception
            // e.g., Sentry.captureException(e);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Catch any other unexpected errors during processing or saving
            // Sentry.captureException(e); // Example Sentry logging
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Có lỗi xảy ra khi cập nhật danh sách ghi chú máy tính: " + e.getMessage());
            // Consider logging the full stack trace for debugging
            // e.printStackTrace();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/CapNhatNguoiSuaVaThoiGianSua")
    public ResponseEntity<Object> capNhatNoiDungVaNguoiSua( // Tên phương thức rõ ràng hơn
                                                            @RequestParam Long maGhiChuMT,
                                                            @RequestParam String ngaySuaStr,
                                                            @RequestParam String thoiGianBatDau,
                                                            @RequestParam String thoiGianKetThuc,
                                                            @RequestParam Long maTKSuaLoi, // Đảm bảo client gửi đúng kiểu Long
                                                            @RequestParam String token
    ) {
        try {
            // Gọi phương thức service đã cập nhật
            GhiChuMayTinh updatedGhiChuEntity = ghiChuMayTinhService.capNhatNoiDungVaNguoiSua(
                    maGhiChuMT,
                    ngaySuaStr,
                    thoiGianBatDau,
                    thoiGianKetThuc,
                    maTKSuaLoi,
                    token
            );

            // Map entity đã cập nhật sang DTO bằng phương thức trong service
            GhiChuMayTinhDTO dto = ghiChuMayTinhService.mapToDTO(updatedGhiChuEntity);

            // Log thông tin (tùy chọn)
            System.out.println("Ghi chú " + maGhiChuMT + ": Nội dung được cập nhật thông tin lịch sửa bởi TK " + maTKSuaLoi);

            // Trả về DTO trong response thành công
            return ResponseEntity.ok(dto);

        } catch (EntityNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404
        } catch (IllegalArgumentException e) { // Bắt lỗi thiếu tham số hoặc logic từ service
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400
        } catch (SecurityException e) { // Bắt lỗi xác thực/quyền
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401
        } catch (Exception e) { // Bắt các lỗi khác
            // Log lỗi chi tiết
            System.err.println("Lỗi hệ thống khi cập nhật nội dung ghi chú: " + e.getMessage());
            e.printStackTrace(); // In stack trace ra console log của server
            // Sentry.captureException(e); // Hoặc gửi lên Sentry/hệ thống logging khác

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi hệ thống không mong muốn xảy ra. Vui lòng thử lại sau."); // Thông báo chung chung cho client
            // errorResponse.put("detail", e.getMessage()); // Cân nhắc chỉ log chi tiết ở server
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}
