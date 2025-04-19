package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.MayTinhDTO;
import com.example.QuanLyPhongMayBackEnd.DTO.PhongMayDTO;
import com.example.QuanLyPhongMayBackEnd.DTO.QRDTO;
import com.example.QuanLyPhongMayBackEnd.entity.*;
import com.example.QuanLyPhongMayBackEnd.repository.CaThucHanhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.MayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TangRepository;
import com.example.QuanLyPhongMayBackEnd.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.orm.ObjectOptimisticLockingFailureException; // Correct Import Here

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class PhongMayService {

    private static final Logger logger = LogManager.getLogger(PhongMayService.class);
    private static final String LOG_FILE_PATH = "F:/Note/log.txt";
    private static String currentDate = ""; // To keep track of the current date in memory

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MayTinhService mayTinhService;

    @Autowired
    private CaThucHanhService caThucHanhService;

    @Autowired
    private PhongMayRepository phongMayRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private GhiChuMayTinhService ghiChuMayTinhService;

    @Autowired
    private GhiChuPhongMayService ghiChuPhongMayService;

    @Autowired
    private TangRepository tangRepository;

    @Autowired
    private CaThucHanhRepository caThucHanhRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MayTinhRepository mayTinhRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Run every day at 00:00:00
    public void dailyLogSummary() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        String yesterdayDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int logCount = countLogsForDate(yesterdayDate);
        logger.info("Log summary for " + yesterdayDate + ": " + logCount + " log entries.");

        // Write the summary to the log file
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    logger.error("Error creating log file", e);
                    return; // Exit if we can't create the file
                }
            }
            out.println("Log summary for " + yesterdayDate + ": " + logCount + " log entries.");
        } catch (IOException e) {
            logger.error("Error writing log summary to file", e);
        }
    }
    private int countLogsForDate(String date) {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            return 0; // No log file, so no logs
        }

        int count = 0;
        try (Scanner scanner = new Scanner(logFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(date)) { // Count date header as a log entry
                    count++;
                } else if (line.matches("\\d{2}:\\d{2}:\\d{2} -.*")) {
                    count++; // Log entry (time - message)
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Log file not found", e); // Should not happen, we checked existence
        }
        return count;
    }


    public void writeLog(String username, String message) {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Check if the log file exists. Create it if it doesn't.
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                currentDate = ""; // Reset currentDate when creating a new file
            } catch (IOException e) {
                logger.error("Error creating log file", e);
                return; // Exit if we can't create the file
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            // Check if the date header needs to be written
            if (!currentDate.equals(formattedDate)) {
                // Check if the date is already present in the file
                if (!isDatePresentInLog(formattedDate)) {
                    out.println(formattedDate);
                }
                currentDate = formattedDate; // Update the in-memory current date
            }
            out.println(formattedTime + " - User: " + (username != null ? username : "Unknown") + " - " + message);
        } catch (IOException e) {
            logger.error("Error writing to log file", e);
        }
    }

    private boolean isDatePresentInLog(String date) {
        File logFile = new File(LOG_FILE_PATH);
        if (!logFile.exists()) {
            return false; // No log file, so date cannot be present
        }

        try (Scanner scanner = new Scanner(logFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equals(date)) {
                    return true; // Date found
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Log file not found", e); //Should not happen
            return false; // Return false in error
        }
        return false; // Date not found
    }



    public boolean isUserLoggedIn(String token) {
        String username = null;
        try{
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e){

        }
        boolean isLoggedIn = taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
        writeLog(username, "isUserLoggedIn: " + isLoggedIn);
        return isLoggedIn;
    }

    public PhongMay layPhongMayTheoMa(Long maPhong, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token); // Get username
        } catch (Exception e) {
            writeLog(null, "layPhongMayTheoMa - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "layPhongMayTheoMa - User not logged in.  maPhong: " + maPhong);
            return null; // Token không hợp lệ
        }
        PhongMay phongMay = null;
        Optional<PhongMay> kq = phongMayRepository.findById(maPhong);
        try {
            phongMay = kq.get();
            writeLog(username, "layPhongMayTheoMa - Success. maPhong: " + maPhong);
            return phongMay;
        } catch (Exception e) {
            writeLog(username, "layPhongMayTheoMa - Error: " + e.getMessage() + ". maPhong: " + maPhong);
            return phongMay;
        }
    }

    public PhongMay capNhatTheoMaTang(Long maTang, PhongMay phongMay, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "capNhatTheoMaTang - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "capNhatTheoMaTang - User not logged in. maTang: " + maTang);
            return null;
        }

        List<PhongMay> danhSachPhongMay = phongMayRepository.findByTang_MaTang(maTang);
        if (danhSachPhongMay.isEmpty()) {
            writeLog(username, "capNhatTheoMaTang - No rooms found for maTang: " + maTang);
            throw new IllegalArgumentException("Không tìm thấy phòng máy thuộc tầng với maTang: " + maTang);
        }

        PhongMay phongMayToUpdate = danhSachPhongMay.get(0);
        phongMayToUpdate.setTenPhong(phongMay.getTenPhong());
        phongMayToUpdate.setSoMay(phongMay.getSoMay());
        phongMayToUpdate.setMoTa(phongMay.getMoTa());
        phongMayToUpdate.setTrangThai(phongMay.getTrangThai());
        phongMayToUpdate.setTang(phongMay.getTang());

        try {
            PhongMay updatedPhongMay = phongMayRepository.save(phongMayToUpdate);
            writeLog(username, "capNhatTheoMaTang - Success. maTang: " + maTang);
            return updatedPhongMay;
        } catch (Exception e) {
            writeLog(username, "capNhatTheoMaTang - Error: " + e.getMessage() + ". maTang: " + maTang);
            throw e;  // Re-throw after logging.  Important!
        }
    }

    public List<PhongMay> findByTrangThai(String trangThai, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "findByTrangThai - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "findByTrangThai - User not logged in. trangThai: " + trangThai);
            return null;
        }

        try {
            List<PhongMay> result = phongMayRepository.findByTrangThai(trangThai);
            writeLog(username, "findByTrangThai - Success. trangThai: " + trangThai + ", Result size: " + result.size());
            return result;
        } catch (Exception e) {
            writeLog(username, "findByTrangThai - Error: " + e.getMessage()+ ". trangThai: " + trangThai);
            throw e;
        }
    }
    @Cacheable(value = "phongMays") // Lưu trữ kết quả trong cache với tên "phongMays"
    public List<PhongMay> layDSPhongMay(String token) {
        String username = null;
        long startTime = System.currentTimeMillis();  // Bắt đầu đo thời gian

        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            System.out.println("layDSPhongMay - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            System.out.println("layDSPhongMay - User not logged in.");
            return null;
        }

        try {
            List<PhongMay> result = phongMayRepository.findAll();
            long endTime = System.currentTimeMillis();  // Đo thời gian kết thúc

            System.out.println("layDSPhongMay - Success. Result size: " + result.size());
            System.out.println("layDSPhongMay - Time taken: " + (endTime - startTime) + " ms"); // In ra thời gian đã sử dụng

            return result;
        } catch (Exception ex) {
            long endTime = System.currentTimeMillis();  // Đo thời gian kết thúc khi có lỗi

            System.out.println("layDSPhongMay - Error: " + ex.getMessage());
            System.out.println("layDSPhongMay - Time taken: " + (endTime - startTime) + " ms"); // In ra thời gian đã sử dụng dù có lỗi

            throw ex;
        }
    }



    @Transactional
    public void xoa(Long maPhong, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "xoa - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "xoa - User not logged in. maPhong: " + maPhong);
            return;
        }
        try {
            List<MayTinh> danhSachMayTinh = mayTinhService.layDSMayTinhTheoMaPhong(maPhong, token);
            List<CaThucHanh> danhSachCaThucHanh = caThucHanhService.layDSCaThucHanhTheoMaPhong(maPhong, token);
            ghiChuPhongMayService.xoaByMaPhong(maPhong, token);

            for (MayTinh mayTinh : danhSachMayTinh) {
                ghiChuMayTinhService.xoaTheoMaMay(mayTinh.getMaMay(), token);
            }

            for (MayTinh mayTinh : danhSachMayTinh) {
                mayTinhService.xoa(mayTinh.getMaMay(), token);
            }

            for (CaThucHanh caThucHanh : danhSachCaThucHanh) {
                caThucHanhService.xoa(caThucHanh.getMaCa(), token);
            }

            phongMayRepository.deleteById(maPhong);
            entityManager.flush();
            entityManager.clear();
            writeLog(username, "xoa - Success. maPhong: " + maPhong);

        } catch (Exception e) {
            writeLog(username, "xoa - Error: " + e.getMessage()+ ". maPhong: " + maPhong);
            throw e;  // Re-throw after logging.
        }
    }

    public PhongMay luu(PhongMay phongMay, String token, String requestToken) { // Method signature vẫn giữ nguyên
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "luu - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "luu - User not logged in.");
            return null;
        }
        try {
            PhongMay savedPhongMay = phongMayRepository.save(phongMay);
            writeLog(username, "luu - Success. Room saved: " + savedPhongMay.getMaPhong() + ", Request Token (không dùng nữa): " + requestToken); // Cập nhật log
            return savedPhongMay;
        } catch (DataIntegrityViolationException e) {
            writeLog(username, "luu - Error saving room (duplicate entry - tenPhong & maTang): " + e.getMessage() + ", Request Token (không dùng nữa): " + requestToken); // Cập nhật log
            throw new DuplicateRequestException("Phòng máy với tên và tầng này đã tồn tại. Vui lòng kiểm tra lại.", e); // Thông báo lỗi cụ thể hơn
        }
        catch (PersistenceException e) {
            writeLog(username, "luu - Persistence error saving room: " + e.getMessage() + ", Request Token (không dùng nữa): " + requestToken); // Cập nhật log
            throw new RuntimeException("Lỗi khi lưu phòng máy vào database.", e);
        }
        catch (Exception e) {
            writeLog(username, "luu - Error saving room: " + e.getMessage() + ", Request Token (không dùng nữa): " + requestToken); // Cập nhật log
            throw new RuntimeException("Lỗi tạo phòng máy không xác định.", e);
        }
    }


    public static class DuplicateRequestException extends RuntimeException {
        public DuplicateRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }



    public PhongMay capNhatTheoMa(Long maPhong, String tenPhong, int soMay, String moTa, String trangThai, Long maTang, Integer version, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "capNhatTheoMa - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "capNhatTheoMa - User not logged in. maPhong: " + maPhong);
            return null;
        }

        Optional<PhongMay> phongMayDB = phongMayRepository.findById(maPhong);
        if (phongMayDB.isPresent()) {
            PhongMay phongMayCu = phongMayDB.get();

            // Optimistic Locking Check - Now using ObjectOptimisticLockingFailureException
            if (!phongMayCu.getVersion().equals(version)) {
                writeLog(username, "capNhatTheoMa - Optimistic Lock Exception: Version mismatch. maPhong: " + maPhong + ", DB Version: " + phongMayCu.getVersion() + ", Request Version: " + version);
                throw new IllegalStateException("Concurrent update detected. Please refresh and try again.");

            }

            phongMayCu.setTenPhong(tenPhong);
            phongMayCu.setSoMay(soMay);
            phongMayCu.setMoTa(moTa);
            phongMayCu.setTrangThai(trangThai);

            Optional<Tang> tangOptional = tangRepository.findById(maTang);
            if (!tangOptional.isPresent()) {
                writeLog(username, "capNhatTheoMa - Tang not found. maTang: " + maTang);
                return null; // Or throw an exception - better practice!
            }
            Tang tang = tangOptional.get();
            phongMayCu.setTang(tang);
            try {
                PhongMay updatedPhongMay = phongMayRepository.save(phongMayCu);
                writeLog(username, "capNhatTheoMa - Success. maPhong: " + maPhong);
                return updatedPhongMay;
            } catch (Exception e) {
                writeLog(username, "capNhatTheoMa - Error saving room: " + e.getMessage() + ". maPhong: " + maPhong);
                throw new RuntimeException("Room update failed. Database error.", e);
            }
        }
        writeLog(username, "capNhatTheoMa - Room not found. maPhong: " + maPhong);
        return null; // Or throw an exception - MUCH better practice
    }

    public List<PhongMay> layPhongMayTheoMaTang(Long maTang, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "layPhongMayTheoMaTang - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "layPhongMayTheoMaTang - User not logged in. maTang: " + maTang);
            return null;
        }

        try {
            List<PhongMay> result = phongMayRepository.findByTang_MaTang(maTang);
            writeLog(username, "layPhongMayTheoMaTang - Success. maTang: " + maTang + ", Result size: " + result.size());
            return result;
        } catch (Exception e) {
            writeLog(username, "layPhongMayTheoMaTang - Error: " + e.getMessage() + ". maTang: " + maTang);
            throw e; // Re-throw the exception
        }
    }


    public List<PhongMayDTO> timKiemPhongMay(String keyword, String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "timKiemPhongMay - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "timKiemPhongMay - User not logged in. keyword: " + keyword);
            return null;
        }

        String[] parts = keyword.split(":");
        if (parts.length != 2) {
            writeLog(username, "timKiemPhongMay - Invalid keyword format. keyword: " + keyword);
            return null;
        }

        String column = parts[0].trim();
        String value = parts[1].trim();

        List<String> validColumns = Arrays.asList("ten_phong", "so_may", "mo_ta", "trang_thai");
        if (!validColumns.contains(column)) {
            writeLog(username, "timKiemPhongMay - Invalid column name. column: " + column);
            return null;
        }

        String finalUsername = username;
        Specification<PhongMay> specification = (root, query, criteriaBuilder) -> {
            try {
                switch (column) {
                    case "ten_phong":
                        return criteriaBuilder.like(root.get("tenPhong"), "%" + value + "%");
                    case "so_may":
                        return criteriaBuilder.equal(root.get("soMay"), Integer.parseInt(value));
                    case "mo_ta":
                        return criteriaBuilder.like(root.get("moTa"), "%" + value + "%");
                    case "trang_thai":
                        return criteriaBuilder.like(root.get("trangThai"), "%" + value + "%");
                    default:
                        return null;
                }
            } catch (NumberFormatException e) {
                writeLog(finalUsername, "timKiemPhongMay - Invalid number format for column 'so_may'. value: " + value);
                return null; // Or throw an exception
            }
        };

        try {
            List<PhongMay> results = phongMayRepository.findAll(specification);
            List<PhongMayDTO> resultDTOs = results.stream()
                    .map(pm -> new PhongMayDTO(
                            pm.getMaPhong(),
                            pm.getTenPhong(),
                            pm.getSoMay(),
                            pm.getMoTa(),
                            pm.getTrangThai()
                    ))
                    .collect(Collectors.toList());
            writeLog(username, "timKiemPhongMay - Success. keyword: " + keyword + ", Result size: " + resultDTOs.size());
            return resultDTOs;
        } catch (Exception e) {
            writeLog(username, "timKiemPhongMay - Error: " + e.getMessage()+ ". keyword: " + keyword);
            throw e;
        }
    }
    public void importCSVFile(MultipartFile file, String token) throws IOException {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "importCSVFile - Error getting username from token: " + e.getMessage());
        }
        if (!isUserLoggedIn(token)) {
            writeLog(username, "importCSVFile - User not logged in.");
            return ;
        }

        String filePath = "F:/Note/filetestphongmay.csv";
        String fieldsTerminated = ",";
        String optionallyEnclosed = "\"";
        String linesTerminated = "\n";
        int ignoreRow = 1;

        String loadSql = "LOAD DATA INFILE '" + filePath.replace("\\", "/") + "' " +
                "INTO TABLE phong_may " +
                "FIELDS TERMINATED BY '" + fieldsTerminated + "' " +
                "OPTIONALLY ENCLOSED BY '" + optionallyEnclosed + "' " +
                "LINES TERMINATED BY '" + linesTerminated + "' " +
                "IGNORE " + ignoreRow + " ROWS " +
                "(ten_phong, so_may, mo_ta, trang_thai, ma_tang)";

        try {
            jdbcTemplate.execute(loadSql);
            writeLog(username, "importCSVFile - Success. File: " + file.getOriginalFilename());
        } catch (Exception e) {
            writeLog(username, "importCSVFile - Error: " + e.getMessage() + ". File: " + file.getOriginalFilename());
            throw new IOException("Có lỗi xảy ra trong quá trình import: " + e.getMessage(), e); // Include original exception
        }
    }


    public List<QRDTO> layDanhSachPhongMayVaThongKe(String token) {
        String username = null;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            writeLog(null, "layDanhSachPhongMayVaThongKe - Error getting username from token: " + e.getMessage());
        }

        if (!isUserLoggedIn(token)) {
            writeLog(username, "layDanhSachPhongMayVaThongKe - User not logged in.");
            return null;
        }
        try {
            // Use the modified repository method that doesn't fetch bags eagerly
            List<PhongMay> danhSachPhongMay = phongMayRepository.findAllPhongMayWithMayTinhsAndThietBis(); // Now fetches PhongMay only
            List<QRDTO> result = danhSachPhongMay.stream().map(phongMay -> {
                List<MayTinh> mayDangHoatDong = phongMay.getMayTinhs().stream() // Lazy load mayTinhs here
                        .filter(mayTinh -> "Đang hoạt động".equals(mayTinh.getTrangThai()))
                        .collect(Collectors.toList());

                List<MayTinh> mayDaHong = phongMay.getMayTinhs().stream() // Lazy load mayTinhs here
                        .filter(mayTinh -> "Đã hỏng".equals(mayTinh.getTrangThai()))
                        .collect(Collectors.toList());

                List<MayTinhDTO> mayDangHoatDongDTO = mayDangHoatDong.stream()
                        .map(mayTinh -> new MayTinhDTO(mayTinh.getMaMay(), mayTinh.getTenMay(), mayTinh.getTrangThai(), mayTinh.getMoTa()))
                        .collect(Collectors.toList());

                List<MayTinhDTO> mayDaHongDTO = mayDaHong.stream()
                        .map(mayTinh -> new MayTinhDTO(mayTinh.getMaMay(), mayTinh.getTenMay(), mayTinh.getTrangThai(), mayTinh.getMoTa()))
                        .collect(Collectors.toList());

                // Thống kê thiết bị
                List<ThietBi> thietBiDangHoatDong = phongMay.getThietBis().stream() // Lazy load thietBis here
                        .filter(thietBi -> "Đang hoạt động".equals(thietBi.getTrangThai()))
                        .collect(Collectors.toList());

                List<ThietBi> thietBiDaHong = phongMay.getThietBis().stream() // Lazy load thietBis here
                        .filter(thietBi -> "Đã hỏng".equals(thietBi.getTrangThai()))
                        .collect(Collectors.toList());

                // Lấy danh sách loại thiết bị duy nhất trong phòng
                List<String> loaiThietBiNames = phongMay.getThietBis().stream() // Lazy load thietBis here
                        .map(thietBi -> thietBi.getLoaiThietBi().getTenLoai())
                        .distinct() // Lấy tên loại thiết bị duy nhất
                        .collect(Collectors.toList());


                return new QRDTO(
                        phongMay.getTenPhong(),
                        mayDangHoatDong.size(),
                        mayDaHong.size(),
                        loaiThietBiNames, // Thêm danh sách loại thiết bị vào DTO
                        thietBiDangHoatDong.size(), // Thêm số lượng thiết bị đang hoạt động
                        thietBiDaHong.size() // Thêm số lượng thiết bị đã hỏng
                );
            }).collect(Collectors.toList());

            writeLog(username, "layDanhSachPhongMayVaThongKe - Success. Result size: " + result.size());
            return result;
        } catch (Exception e){
            writeLog(username, "layDanhSachPhongMayVaThongKe - Error: " + e.getMessage());
            throw e;
        }
    }
    public List<Map<String, Object>> thongKeMayTinhTheoThoiGian(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        if (!isUserLoggedIn(token)) {
            writeLog(username, "thongKeMayTinhTheoThoiGian - Unauthorized access attempt.");
            return null; // Or throw an exception
        }

        List<MayTinh> allMayTinhs = mayTinhRepository.findAll();
        if (allMayTinhs.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no computers found
        }

        Map<Date, Map<String, Integer>> dailyStats = new LinkedHashMap<>(); // Use LinkedHashMap to maintain order

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        allMayTinhs.forEach(mayTinh -> {
            Date lapDatDate = mayTinh.getNgayLapDat();
            if (lapDatDate != null) {
                Date dateOnly = null;
                try {
                    dateOnly = dateFormat.parse(dateFormat.format(lapDatDate)); // Get date-only part
                } catch (Exception e) {
                    e.printStackTrace(); // Handle parse exception, maybe log it
                    return; // Skip this entry if date parsing fails
                }

                Map<String, Integer> statusCounts = dailyStats.computeIfAbsent(dateOnly, k -> new HashMap<>());
                statusCounts.merge(mayTinh.getTrangThai(), 1, Integer::sum); // Increment count for status
            }
        });

        List<Map<String, Object>> chartData = new ArrayList<>();
        dailyStats.forEach((date, statusCounts) -> {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("time", date);
            dataPoint.put("Dang hoạt động", statusCounts.getOrDefault("Đang hoạt động", 0));
            dataPoint.put("Đã hỏng", statusCounts.getOrDefault("Đã hỏng", 0));
            dataPoint.put("Không hoạt động", statusCounts.getOrDefault("Không hoạt động", 0));
            chartData.add(dataPoint);
        });

        writeLog(username, "thongKeMayTinhTheoThoiGian - Statistics generated for " + chartData.size() + " days.");
        return chartData;
    }
//    @Scheduled(fixedRate = 900000) // Chạy mỗi 10 giây (for testing, change back to 60000 for 1 minute)
//    @Transactional // Add Transactional for EntityManager operations
//    public void capNhatTrangThaiPhongMayTheoThoiGianThuc() {
//        LocalDate homNay = LocalDate.now();
//        LocalTime thoiGianHienTai = LocalTime.now();
//        List<PhongMay> tatCaPhongMay = phongMayRepository.findAll();
//
//        for (PhongMay phongMay : tatCaPhongMay) {
//            boolean dangCoTiet = false;
//            List<CaThucHanh> caThucHanhHomNay = caThucHanhRepository.findByNgayThucHanhAndPhongMay(homNay, phongMay);
//
//            if (caThucHanhHomNay != null) {
//                for (CaThucHanh ca : caThucHanhHomNay) {
//                    ThoiGianBieu.KhoangThoiGian khoangThoiGianBatDau = ThoiGianBieu.getKhoangThoiGianTheoTiet(ca.getTietBatDau());
//                    ThoiGianBieu.KhoangThoiGian khoangThoiGianKetThuc = ThoiGianBieu.getKhoangThoiGianTheoTiet(ca.getTietKetThuc());
//
//                    if (khoangThoiGianBatDau != null && khoangThoiGianKetThuc != null) {
//                        if (thoiGianHienTai.isAfter(khoangThoiGianBatDau.getStartTime()) && thoiGianHienTai.isBefore(khoangThoiGianKetThuc.getEndTime())) {
//                            dangCoTiet = true;
//                            break;
//                        } else if (thoiGianHienTai.equals(khoangThoiGianBatDau.getStartTime()) || thoiGianHienTai.equals(khoangThoiGianKetThuc.getEndTime())) {
//                            dangCoTiet = true;
//                            break;
//                        }
//                    }
//                }
//            }
//
//            String trangThaiMoi;
//            if (dangCoTiet) {
//                trangThaiMoi = "Đang có tiết";
//            } else {
//                trangThaiMoi = "Trống";
//            }
//
//            if (!phongMay.getTrangThai().equals(trangThaiMoi)) {
//                String tenPhong = phongMay.getTenPhong(); // Get tenPhong for logging
//
//                // Direct database update using JPQL to avoid version increment
//                entityManager.createQuery("UPDATE PhongMay pm SET pm.trangThai = :trangThaiMoi WHERE pm.maPhong = :maPhong")
//                        .setParameter("trangThaiMoi", trangThaiMoi)
//                        .setParameter("maPhong", phongMay.getMaPhong())
//                        .executeUpdate();
//
//                System.out.println("Phòng máy " + tenPhong + " chuyển trạng thái: " + trangThaiMoi + " (No Version Increment)");
//            }
//        }
//    }



}