package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.PhongMayDTO;
import com.example.QuanLyPhongMayBackEnd.DTO.QRDTO;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import com.example.QuanLyPhongMayBackEnd.security.JwtUtil;
import com.example.QuanLyPhongMayBackEnd.service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
public class PhongMayController {

    @Autowired
    private PhongMayService phongMayService;
    @Autowired
    private TangService tangService;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private ToaNhaService toaNhaService;
    @Autowired
    private MayTinhService mayTinhService;
    @Autowired
    private JwtUtil jwtUtil;
    private static final Set<String> usedTokens = new HashSet<>();
    private static final long TOKEN_EXPIRY_TIME_MS = TimeUnit.MINUTES.toMillis(1);

    @GetMapping("/request-token")
    public ResponseEntity<String> getRequestToken() {
        String token = UUID.randomUUID().toString();
        return ResponseEntity.ok(token);
    }
    @PostMapping("/LuuPhongMay") // Giữ nguyên endpoint và RequestParam cũ, thêm requestToken
    public ResponseEntity<?> luu(
            @RequestParam String tenPhong,
            @RequestParam int soMay,
            @RequestParam String moTa,
            @RequestParam String trangThai,
            @RequestParam Long maTang,
            @RequestParam String token, // Token JWT vẫn giữ nguyên
            @RequestParam("requestToken") String requestToken // Thêm requestToken như RequestParam
    ) {

        if (usedTokens.contains(requestToken)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Yêu cầu trùng lặp. Vui lòng chờ một khoảng thời gian trước khi thử lại.");
        }

        synchronized (usedTokens) {
            if (usedTokens.contains(requestToken)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Yêu cầu trùng lặp. Vui lòng chờ một khoảng thời gian trước khi thử lại.");
            }
            usedTokens.add(requestToken);
        }

        PhongMay phongMay = new PhongMay();
        phongMay.setTenPhong(tenPhong);
        phongMay.setSoMay(soMay);
        phongMay.setMoTa(moTa);
        phongMay.setTrangThai(trangThai);

        Tang tang = tangService.layTangTheoMa(maTang, token); // Sử dụng token JWT để xác thực
        if (tang == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy tầng với mã " + maTang);
        }
        phongMay.setTang(tang);

        try {
            PhongMay savedPhongMay = phongMayService.luu(phongMay, token, requestToken); // Truyền requestToken vào service
            return ResponseEntity.ok(savedPhongMay);
        } catch (PhongMayService.DuplicateRequestException e) {
            synchronized (usedTokens) {
                usedTokens.remove(requestToken);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Yêu cầu trùng lặp. Vui lòng chờ một khoảng thời gian trước khi thử lại.");
        }
        catch (Exception e) {
            synchronized (usedTokens) {
                usedTokens.remove(requestToken);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server: " + e.getMessage() + ". Vui lòng thử lại sau.");
        } finally {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            synchronized (usedTokens) {
                                usedTokens.remove(requestToken);
                            }
                        }
                    },
                    TOKEN_EXPIRY_TIME_MS
            );
        }
    }


    @GetMapping("/DSPhongMay")
    public List<PhongMay> layDSPhongMay(@RequestParam String token) {
        // Cập nhật trạng thái phòng máy trước khi lấy danh sách
//        phongMayService.capNhatTrangThaiPhongMayTheoThoiGianThuc();

        // Lấy danh sách phòng máy (chức năng hiện tại)
        return phongMayService.layDSPhongMay(token);
    }

    @GetMapping("/DSPhongMaytheoTrangThai/{trangThai}")
    public List<PhongMay> getPhongMaysByTrangThai(@PathVariable String trangThai, @RequestParam String token) {
        // Handle token validation if necessary
        return phongMayService.findByTrangThai(trangThai,token);
    }

    @GetMapping("/PhongMay")
    public PhongMay layPhongMayTheoMa(@RequestParam Long maPhong, @RequestParam String token) {
        // Handle token validation if necessary
        return phongMayService.layPhongMayTheoMa(maPhong, token);
    }


    @DeleteMapping("/XoaPhongMay")
    @Transactional
    public String xoa(@RequestParam Long maPhong, @RequestParam String token) {
        // Validate token, if necessary, using a utility method
        if (!phongMayService.isUserLoggedIn(token)) {
            return "Token không hợp lệ";
        }

        // Proceed to delete related entities
        phongMayService.xoa(maPhong, token);
        return "Đã xoá phòng máy với mã " + maPhong;
    }


    @PostMapping("/CapNhatPhongMay")
    public ResponseEntity<?> capNhatTheoMa(
            @RequestParam Long maPhong,
            @RequestParam String tenPhong,
            @RequestParam int soMay,
            @RequestParam String moTa,
            @RequestParam String trangThai,
            @RequestParam Long maTang,
            @RequestParam Integer version, // Add version parameter
            @RequestParam String token) {

        if (!phongMayService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            PhongMay updatedPhongMay = phongMayService.capNhatTheoMa(maPhong, tenPhong, soMay, moTa, trangThai, maTang, version, token);
            if (updatedPhongMay == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedPhongMay, HttpStatus.OK);
        } catch (IllegalStateException e) { // Catch IllegalStateException
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage()); // Get the exception message
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // Return 409 Conflict
        }
        catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/XoaNhieuPhongMay")
    @Transactional
    public String xoaNhieuPhongMay(@RequestParam List<Long> maPhongList, @RequestParam String token) {
        // Validate token, if necessary, using a utility method
        if (!phongMayService.isUserLoggedIn(token)) {
            return "Token không hợp lệ";
        }

        // Proceed to delete related entities for each room
        for (Long maPhong : maPhongList) {
            phongMayService.xoa(maPhong, token);
        }

        return "Đã xoá " + maPhongList.size() + " phòng máy";
    }

    @GetMapping("/searchPhongMay")
    public ResponseEntity<Map<String, Object>> searchPhongMay(@RequestParam String keyword, @RequestParam String token) {
        if (!phongMayService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // Validate the keyword
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // Perform the search
        List<PhongMayDTO> results = phongMayService.timKiemPhongMay(keyword, token);

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("size", results.size());

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/importPhongMay")
    public String importPhongMaysFromCSV(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        if (!phongMayService.isUserLoggedIn(token)) {
            return "Token không hợp lệ!";
        }
        try {
            phongMayService.importCSVFile(file, token);
            return "Import dữ liệu thành công!";
        } catch (IOException e) {
            return "Có lỗi xảy ra khi xử lý file CSV: " + e.getMessage();
        } catch (Exception e) {
            return "Có lỗi xảy ra trong quá trình import: " + e.getMessage();
        }
    }


    @GetMapping("/phong-may-thong-ke")
    public ResponseEntity<List<QRDTO>> layDanhSachPhongMayThongKe(@RequestParam String token) {
        if (!phongMayService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<QRDTO> danhSachPhongMay = phongMayService.layDanhSachPhongMayVaThongKe(token);
        return new ResponseEntity<>(danhSachPhongMay, HttpStatus.OK);
    }
    @GetMapping("/DSMayTinhTheoPhong")
    public ResponseEntity<List<MayTinh>> layDSMayTinhTheoPhong(
            @RequestParam Long maPhong,
            @RequestParam String token) {

        // Lấy tên người dùng từ token
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            // Nếu có lỗi khi lấy tên người dùng từ token, ghi log và trả về lỗi
            phongMayService.writeLog(null, "layDSMayTinhTheoPhong - Error getting username from token: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Kiểm tra tính hợp lệ của token
        if (!phongMayService.isUserLoggedIn(token)) {
            phongMayService.writeLog(username, "layDSMayTinhTheoPhong - User not logged in. Access denied for maPhong: " + maPhong);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // Trả về lỗi UNAUTHORIZED nếu token không hợp lệ
        }

        try {
            // Gọi service để lấy danh sách máy tính theo phòng
            List<MayTinh> mayTinhList = mayTinhService.layDSMayTinhTheoMaPhong(maPhong, token);

            // Ghi log kết quả
            phongMayService.writeLog(username, "layDSMayTinhTheoPhong - Success. maPhong: " + maPhong + ", Found " + (mayTinhList != null ? mayTinhList.size() : "null") + " computers.");

            // Nếu danh sách máy tính không rỗng, trả về danh sách
            if (mayTinhList != null && !mayTinhList.isEmpty()) {
                return new ResponseEntity<>(mayTinhList, HttpStatus.OK);
            } else {
                // Nếu không có máy tính, trả về thông báo no content
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            // Ghi log lỗi và trả về lỗi server error nếu có ngoại lệ
            phongMayService.writeLog(username, "layDSMayTinhTheoPhong - Error fetching computers: " + e.getMessage() + ". maPhong: " + maPhong);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/thong-ke-may-tinh-theo-thoi-gian")
    public ResponseEntity<List<Map<String, Object>>> layThongKeMayTinhTheoThoiGian(@RequestParam String token) {
        if (!phongMayService.isUserLoggedIn(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Map<String, Object>> thongKe = phongMayService.thongKeMayTinhTheoThoiGian(token);
        return new ResponseEntity<>(thongKe, HttpStatus.OK);
    }





}