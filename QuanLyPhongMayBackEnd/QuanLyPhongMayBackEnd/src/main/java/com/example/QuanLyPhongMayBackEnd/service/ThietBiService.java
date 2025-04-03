package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.LoaiThietBi;
import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;
import com.example.QuanLyPhongMayBackEnd.repository.LoaiThietBiRepository;
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

import java.util.List;

@Service
public class ThietBiService {

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private TaiKhoanService taiKhoanService; // Assuming similar service for token check

    // Replicates token check logic from MayTinhService
    public boolean isUserLoggedIn(String token) {
        // Replace with your actual implementation if different
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Helper to get LoaiThietBi, throwing specific exception
    private LoaiThietBi getLoaiThietBiByIdOrThrow(Long maLoai) {
        return loaiThietBiRepository.findById(maLoai)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại thiết bị với mã: " + maLoai));
    }

    @Transactional
    @CacheEvict(value = "thietbis", allEntries = true) // Invalidate list cache on create
    public ThietBi luuThietBi(ThietBi thietBi, Long maLoai, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }

        LoaiThietBi loaiThietBi = getLoaiThietBiByIdOrThrow(maLoai); // Fetch and validate LoaiThietBi
        thietBi.setLoaiThietBi(loaiThietBi); // Associate with the managed type

        // @PrePersist in ThietBi entity handles ngayLapDat
        try {
            return thietBiRepository.save(thietBi);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi lưu thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Caching(
            put = { @CachePut(value = "thietbi", key = "#thietBiDetails.maThietBi") }, // Update single item cache
            evict = { @CacheEvict(value = "thietbis", allEntries = true) }       // Invalidate list cache
    )
    public ThietBi capNhatThietBi(ThietBi thietBiDetails, Long maLoai, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }

        // Fetch existing ThietBi
        ThietBi existingThietBi = thietBiRepository.findById(thietBiDetails.getMaThietBi())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thiết bị với mã: " + thietBiDetails.getMaThietBi()));

        // Fetch and validate the new (or potentially same) LoaiThietBi
        LoaiThietBi loaiThietBi = getLoaiThietBiByIdOrThrow(maLoai);

        // Update fields
        existingThietBi.setTenThietBi(thietBiDetails.getTenThietBi());
        existingThietBi.setTrangThai(thietBiDetails.getTrangThai());
        existingThietBi.setMoTa(thietBiDetails.getMoTa());
        existingThietBi.setLoaiThietBi(loaiThietBi); // Update association

        // @PreUpdate in ThietBi entity handles ngayCapNhat
        try {
            return thietBiRepository.save(existingThietBi);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi cập nhật thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "thietbis", allEntries = true), // Invalidate list cache
            @CacheEvict(value = "thietbi", key = "#maThietBi")      // Invalidate single item cache
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
    // Evict broadly on multi-delete
    @CacheEvict(value = {"thietbis", "thietbi"}, allEntries = true)
    public void xoaNhieuThietBi(List<Long> maThietBiList, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            // Optional: Fetch entities first to ensure they exist
            List<ThietBi> toDelete = thietBiRepository.findAllById(maThietBiList);
            if (toDelete.size() != maThietBiList.size()) {
                System.err.println("Warning: Some ThietBi IDs not found during multi-delete.");
            }
            if (!toDelete.isEmpty()) {
                thietBiRepository.deleteAllInBatch(toDelete); // Use batch for efficiency
            }
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi xoá nhiều thiết bị: " + e.getMessage(), e);
        }
    }

    @Cacheable(value = "thietbis") // Cache the list of all ThietBi
    public List<ThietBi> layDSThietBi(String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            return thietBiRepository.findAll();
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi lấy danh sách thiết bị: " + e.getMessage(), e);
        }
    }

    @Cacheable(value = "thietbi", key = "#maThietBi") // Cache individual ThietBi
    public ThietBi layThietBiTheoMa(Long maThietBi, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        return thietBiRepository.findById(maThietBi)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thiết bị với mã: " + maThietBi));
    }

    // Example: Add method similar to MayTinhService's findByTrangThai if needed
    public List<ThietBi> findByTrangThai(String trangThai, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        // Add Caching if desired for this query
        return thietBiRepository.findByTrangThai(trangThai);
    }
}