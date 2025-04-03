package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.LoaiThietBi;
import com.example.QuanLyPhongMayBackEnd.repository.LoaiThietBiRepository;
import io.sentry.Sentry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LoaiThietBiService {

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    // Placeholder for token validation - Replace with your actual logic
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    @Transactional // Good practice for write operations
    public LoaiThietBi luuLoaiThietBi(LoaiThietBi loaiThietBi, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            // Add any validation logic for loaiThietBi if needed
            return loaiThietBiRepository.save(loaiThietBi);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi lưu loại thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    public LoaiThietBi capNhatLoaiThietBi(LoaiThietBi loaiThietBiDetails, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            LoaiThietBi existingLoai = loaiThietBiRepository.findById(loaiThietBiDetails.getMaLoai())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại thiết bị với mã: " + loaiThietBiDetails.getMaLoai()));

            // Update fields
            existingLoai.setTenLoai(loaiThietBiDetails.getTenLoai());
            // Add other fields to update if necessary

            return loaiThietBiRepository.save(existingLoai);
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw specific exception
        }
        catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi cập nhật loại thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void xoaLoaiThietBi(Long maLoai, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        if (!loaiThietBiRepository.existsById(maLoai)) {
            throw new EntityNotFoundException("Không tìm thấy loại thiết bị với mã: " + maLoai);
        }
        // Optional: Add check here if there are associated ThietBi records
        // before allowing deletion, depending on business rules.
        // Example: if (thietBiRepository.existsByLoaiThietBi_MaLoai(maLoai)) {
        //             throw new IllegalStateException("Không thể xoá loại thiết bị vì còn thiết bị thuộc loại này.");
        //         }
        try {
            loaiThietBiRepository.deleteById(maLoai);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi xoá loại thiết bị: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void xoaNhieuLoaiThietBi(List<Long> maLoaiList, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            // Optional: Add checks for associated ThietBi before deleting multiple
            loaiThietBiRepository.deleteAllById(maLoaiList); // More efficient than looping
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi xoá nhiều loại thiết bị: " + e.getMessage(), e);
        }
    }

    public List<LoaiThietBi> layDSLoaiThietBi(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return loaiThietBiRepository.findAll();
    }

    public LoaiThietBi layLoaiThietBiTheoMa(Long maLoai, String token) {
        if (!isUserLoggedIn(token)) {
            throw new SecurityException("Unauthorized: Invalid Token");
        }
        try {
            return loaiThietBiRepository.findById(maLoai)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại thiết bị với mã: " + maLoai));
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("Lỗi khi lấy loại thiết bị theo mã: " + e.getMessage(), e);
        }
    }
}