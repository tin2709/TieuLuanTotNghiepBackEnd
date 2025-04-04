// src/main/java/com/example/QuanLyPhongMayBackEnd/service/ThietBiService.java
package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.LoaiThietBi;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay; // Import PhongMay if needed for mapping
import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;
import com.example.QuanLyPhongMayBackEnd.repository.LoaiThietBiRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository; // Import PhongMayRepository if needed for validation
import com.example.QuanLyPhongMayBackEnd.repository.ThietBiRepository;
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Import Collectors

@Service
public class ThietBiService {

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    // Optional: Inject PhongMayRepository if you need to validate maPhong exists
    // @Autowired
    // private PhongMayRepository phongMayRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    private LoaiThietBi getLoaiThietBiByIdOrThrow(Long maLoai) {
        return loaiThietBiRepository.findById(maLoai)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại thiết bị với mã: " + maLoai));
    }




    @Transactional
    @Caching(evict = { // Evict DTO cache on create
            @CacheEvict(value = {"thietbis", "thietbiDTOs", "thietbisByPhong"}, allEntries = true),
            @CacheEvict(value = "thietbi", allEntries = true) // Evict single item cache more broadly or handle specific key if possible
    })
    public ThietBi luuThietBi(ThietBi thietBi, Long maLoai, String token) { // Note: Doesn't handle assigning to PhongMay here yet
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }

        LoaiThietBi loaiThietBi = getLoaiThietBiByIdOrThrow(maLoai);
        thietBi.setLoaiThietBi(loaiThietBi);

        // You might want to add logic here or in a different method to set thietBi.setPhongMay(...)

        try {
            return thietBiRepository.save(thietBi);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi lưu thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "thietbi", key = "#thietBiDetails.maThietBi") },
            evict = { // Evict list and DTO caches on update
                    @CacheEvict(value = {"thietbis", "thietbiDTOs", "thietbisByPhong"}, allEntries = true)
            }
    )
    public ThietBi capNhatThietBi(ThietBi thietBiDetails, Long maLoai, String token) { // Note: Doesn't handle updating PhongMay here yet
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }

        ThietBi existingThietBi = thietBiRepository.findById(thietBiDetails.getMaThietBi())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thiết bị với mã: " + thietBiDetails.getMaThietBi()));

        LoaiThietBi loaiThietBi = getLoaiThietBiByIdOrThrow(maLoai);

        existingThietBi.setTenThietBi(thietBiDetails.getTenThietBi());
        existingThietBi.setTrangThai(thietBiDetails.getTrangThai());
        existingThietBi.setMoTa(thietBiDetails.getMoTa());
        existingThietBi.setLoaiThietBi(loaiThietBi);
        // Add logic to update PhongMay if needed existingThietBi.setPhongMay(...)

        try {
            return thietBiRepository.save(existingThietBi);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi cập nhật thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Caching(evict = { // Evict relevant caches on delete
            @CacheEvict(value = {"thietbis", "thietbiDTOs", "thietbisByPhong"}, allEntries = true),
            @CacheEvict(value = "thietbi", key = "#maThietBi")
    })
    public void xoaThietBi(Long maThietBi, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        if (!thietBiRepository.existsById(maThietBi)) {
            throw new EntityNotFoundException("Không tìm thấy thiết bị với mã: " + maThietBi);
        }
        try {
            thietBiRepository.deleteById(maThietBi);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi xoá thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Caching(evict = { // Evict broadly on multi-delete
            @CacheEvict(value = {"thietbis", "thietbiDTOs", "thietbisByPhong"}, allEntries = true),
            @CacheEvict(value = "thietbi", allEntries = true)
    })
    public void xoaNhieuThietBi(List<Long> maThietBiList, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            List<ThietBi> toDelete = thietBiRepository.findAllById(maThietBiList);
            if (toDelete.size() != maThietBiList.size()) {
                // Log or handle IDs not found if necessary
                System.err.println("Warning: Some ThietBi IDs not found during multi-delete.");
            }
            if (!toDelete.isEmpty()) {
                thietBiRepository.deleteAllInBatch(toDelete);
            }
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi xoá nhiều thiết bị: " + e.getMessage(), e);
        }
    }

    // READ ALL - Original, returns basic ThietBi entities
    @Cacheable(value = "thietbis")
    @Transactional(readOnly = true) // Good practice for read operations
    public List<ThietBi> layDSThietBi(String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            // Consider if findAllWithDetails() is better default if clients often need related data
            return thietBiRepository.findAll();
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi lấy danh sách thiết bị: " + e.getMessage(), e);
        }
    }

    // READ ONE
    @Cacheable(value = "thietbi", key = "#maThietBi")
    @Transactional(readOnly = true)
    public ThietBi layThietBiTheoMa(Long maThietBi, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        // Fetch with details might be better if clients usually need Loai/PhongMay info
        // return thietBiRepository.findByIdWithDetails(maThietBi) // Need to add findByIdWithDetails in Repo
        return thietBiRepository.findById(maThietBi)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thiết bị với mã: " + maThietBi));
    }

    // Find by Status
    // Add Caching if desired
    @Transactional(readOnly = true)
    public List<ThietBi> findByTrangThai(String trangThai, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        return thietBiRepository.findByTrangThai(trangThai);
    }

    // --- NEW Method: Get devices by Room ID ---
    @Cacheable(value = "thietbisByPhong", key = "#maPhong + (#maLoai != null ? '-' + #maLoai : '-all')")
    @Transactional(readOnly = true)
    // Return type remains List<ThietBi> as per your previous correction
    public List<ThietBi> layDSThietBiTheoPhong(Long maPhong, Long maLoai, String token) { // Parameter changed to Long maLoai
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }

        // Optional: Validate maPhong exists
        // if (!phongMayRepository.existsById(maPhong)) {
        //     throw new EntityNotFoundException("Không tìm thấy phòng máy với mã: " + maPhong);
        // }

        List<ThietBi> thietBiList;
        try {
            // Check if maLoai filter is provided (is not null)
            if (maLoai != null) {
                // Fetch ThietBi entities matching room and type ID using the new repo method
                thietBiList = thietBiRepository.findByPhongMayMaPhongAndLoaiThietBiMaLoaiWithDetails(maPhong, maLoai);
            } else {
                // No filter by maLoai, get all devices in the room with details
                thietBiList = thietBiRepository.findByPhongMayMaPhongWithDetails(maPhong);
            }

            // Directly return the fetched list (which is List<ThietBi>)
            if (thietBiList == null) {
                return Collections.emptyList();
            }
            return thietBiList;

        } catch (Exception e) {
            Sentry.captureException(e);
            // Log the error with more details if possible
            String filterInfo = (maLoai != null) ? " and type ID " + maLoai : ""; // Updated log info
            System.err.println("Error fetching devices for room " + maPhong + filterInfo + ": " + e.getMessage());
            throw new RuntimeException("Lỗi khi lấy danh sách thiết bị theo phòng" + filterInfo + ": " + e.getMessage(), e);
        }
    }
    @Transactional
    @Caching(evict = { // Evict caches that might contain these devices or lists
            @CacheEvict(value = {"thietbis", "thietbiDTOs", "thietbisByPhong"}, allEntries = true),
            @CacheEvict(value = "thietbi", allEntries = true) // Evict individual items broadly
    })
    public List<ThietBi> capNhatTrangThaiNhieuThietBi(List<Long> maThietBiList, List<String> trangThaiList, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }

        // 1. Validate Input List Sizes
        if (CollectionUtils.isEmpty(maThietBiList) || CollectionUtils.isEmpty(trangThaiList) || maThietBiList.size() != trangThaiList.size()) {
            throw new IllegalArgumentException("Danh sách mã thiết bị và trạng thái phải có cùng kích thước và không được rỗng.");
        }

        // 2. Fetch Devices
        List<ThietBi> devicesToUpdate = thietBiRepository.findAllById(maThietBiList);

        // 3. Validate Fetched Devices
        if (devicesToUpdate.size() != maThietBiList.size()) {
            // Find missing IDs for a better error message
            List<Long> foundIds = devicesToUpdate.stream().map(ThietBi::getMaThietBi).collect(Collectors.toList());
            List<Long> missingIds = maThietBiList.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            throw new EntityNotFoundException("Không tìm thấy các thiết bị với mã: " + missingIds);
        }

        // 4. Create a Map for efficient status lookup
        Map<Long, String> statusMap = new HashMap<>();
        for (int i = 0; i < maThietBiList.size(); i++) {
            statusMap.put(maThietBiList.get(i), trangThaiList.get(i));
        }

        // 5. Update Status for each fetched device
        for (ThietBi device : devicesToUpdate) {
            String newStatus = statusMap.get(device.getMaThietBi());
            // Optional: Add validation for allowed status values here if needed
            // e.g., if (!isValidStatus(newStatus)) { throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newStatus); }
            device.setTrangThai(newStatus);
            // ngayCapNhat will be updated automatically by @PreUpdate lifecycle callback in ThietBi entity
        }

        // 6. Save all updated devices in batch
        try {
            return thietBiRepository.saveAll(devicesToUpdate);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi cập nhật hàng loạt trạng thái thiết bị: " + e.getMessage(), e);
        }
    }



}