// MayTinhService.java
package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.MayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import jakarta.persistence.EntityNotFoundException; // Import for exception
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict; // For cache invalidation
import org.springframework.cache.annotation.CachePut;  // For cache update
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;    // For combining cache operations
import org.springframework.stereotype.Service;

import java.util.Date; // Import Date
import java.util.List;
import java.util.Optional;

@Service
public class MayTinhService {

    @Autowired
    private MayTinhRepository mayTinhRepository;

    @Autowired
    private PhongMayRepository phongMayRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    // Phương thức lấy máy tính theo mã
    @Cacheable(value = "maytinh", key = "#maMay") // Cache individual MayTinh
    public MayTinh layMayTinhTheoMa(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            // Consider throwing an AuthenticationException or returning ResponseEntity from controller
            return null; // Token không hợp lệ
        }
        Optional<MayTinh> mayTinhOptional = mayTinhRepository.findById(maMay);
        return mayTinhOptional.orElse(null);  // Trả về null nếu không tìm thấy
    }

    // Phương thức lấy máy tính theo trạng thái
    public List<MayTinh> findByTrangThai(String trangThai, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return mayTinhRepository.findByTrangThai(trangThai);
    }

    // Phương thức lấy danh sách máy tính theo mã phòng
    public List<MayTinh> layDSMayTinhTheoMaPhong(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return mayTinhRepository.findByPhongMay_MaPhong(maPhong);
    }

    // Phương thức lấy danh sách tất cả máy tính
    @Cacheable(value = "maytinhs") // Cache the list of all MayTinh
    public List<MayTinh> layDSMayTinh(String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        return mayTinhRepository.findAll();
    }

    // Phương thức xóa máy tính theo mã
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "maytinhs", allEntries = true), // Invalidate list cache
            @CacheEvict(value = "maytinh", key = "#maMay")      // Invalidate single item cache
    })
    public void xoa(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            // Consider throwing an exception
            return; // Token không hợp lệ
        }
        if (!mayTinhRepository.existsById(maMay)) {
            throw new EntityNotFoundException("Không tìm thấy máy tính với mã: " + maMay);
        }
        mayTinhRepository.deleteById(maMay);
    }

    // Phương thức lưu máy tính (CREATE)
    @Transactional
    @CacheEvict(value = "maytinhs", allEntries = true) // Invalidate list cache on create
    public MayTinh luu(MayTinh mayTinh, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        // Ensure PhongMay is valid
        PhongMay phongMay = getPhongMayById(mayTinh.getPhongMay().getMaPhong(), token);
        if (phongMay == null) {
            // Throw exception or handle error appropriately
            throw new EntityNotFoundException("Không tìm thấy phòng máy với mã: " + mayTinh.getPhongMay().getMaPhong());
        }
        mayTinh.setPhongMay(phongMay); // Associate with the managed PhongMay entity

        // ngayLapDat should be set by the controller or @PrePersist
        // If not set before, @PrePersist will handle it.
        // @UpdateTimestamp will set ngayChinhSua on the first save.

        return mayTinhRepository.save(mayTinh);
    }

    // Phương thức lấy PhongMay theo maPhong (Consider caching PhongMay too)
    public PhongMay getPhongMayById(Long maPhong, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        Optional<PhongMay> phongMayOptional = phongMayRepository.findById(maPhong);
        return phongMayOptional.orElse(null);  // Trả về null nếu không tìm thấy
    }

    // Phương thức cập nhật máy tính (UPDATE)
    @Transactional
    @Caching(
            put = { @CachePut(value = "maytinh", key = "#mayTinhDetails.maMay") }, // Update single item cache
            evict = { @CacheEvict(value = "maytinhs", allEntries = true) }       // Invalidate list cache
    )
    public MayTinh capNhatMayTinh(MayTinh mayTinhDetails, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ or throw exception
        }

        // 1. Find the existing MayTinh by ID
        MayTinh existingMayTinh = mayTinhRepository.findById(mayTinhDetails.getMaMay())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy máy tính với mã: " + mayTinhDetails.getMaMay()));

        // 2. Check and update PhongMay if necessary
        PhongMay phongMay = getPhongMayById(mayTinhDetails.getPhongMay().getMaPhong(), token);
        if (phongMay == null) {
            throw new EntityNotFoundException("Không tìm thấy phòng máy với mã: " + mayTinhDetails.getPhongMay().getMaPhong());
        }
        existingMayTinh.setPhongMay(phongMay); // Associate with the potentially new PhongMay

        // 3. Update other fields (except ngayLapDat)
        existingMayTinh.setTenMay(mayTinhDetails.getTenMay());
        existingMayTinh.setTrangThai(mayTinhDetails.getTrangThai());
        existingMayTinh.setMoTa(mayTinhDetails.getMoTa());
        // DO NOT update existingMayTinh.setNgayLapDat(...) here. It's handled by `updatable = false`.

        // 4. Save the updated entity. @UpdateTimestamp will automatically update ngayChinhSua.
        return mayTinhRepository.save(existingMayTinh);
    }
}