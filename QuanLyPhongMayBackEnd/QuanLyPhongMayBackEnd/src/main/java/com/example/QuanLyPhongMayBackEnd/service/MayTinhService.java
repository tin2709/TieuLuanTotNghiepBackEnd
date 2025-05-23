// MayTinhService.java
package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.MayTinhDTO;
import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import com.example.QuanLyPhongMayBackEnd.repository.GhiChuMayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.MayTinhRepository;
import com.example.QuanLyPhongMayBackEnd.repository.PhongMayRepository;
import jakarta.persistence.EntityNotFoundException; // Import for exception
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict; // For cache invalidation
import org.springframework.cache.annotation.CachePut;  // For cache update
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;    // For combining cache operations
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MayTinhService {

    @Autowired
    private MayTinhRepository mayTinhRepository;

    @Autowired
    private PhongMayRepository phongMayRepository;
    @Autowired
    private GhiChuMayTinhRepository ghiChuMayTinhRepository;
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
    @Transactional(readOnly = true) // Good practice for read operations, ensures session is open
    public List<MayTinhDTO> layDSMayTinhDTO(String token) {
        if (!isUserLoggedIn(token)) {
            // logger.warn("Unauthorized attempt to fetch MayTinh list with token: {}", token); // Optional logging
            return Collections.emptyList(); // Return empty list for invalid token
        }

        // logger.info("Fetching all MayTinh entities"); // Optional logging
        List<MayTinh> mayTinhEntities = mayTinhRepository.findAll();

        if (mayTinhEntities.isEmpty()) {
            // logger.info("No MayTinh entities found in the database."); // Optional logging
            return Collections.emptyList(); // No computers found
        }

        // Use Stream API to map entities to DTOs and fetch latest note for each
        // NOTE: This approach has the N+1 query problem. For large datasets, optimize this.
        List<MayTinhDTO> dtos = mayTinhEntities.stream()
                .map(mayTinhEntity -> {
                    // 1. Map basic MayTinh details
                    MayTinhDTO dto = mapToMayTinhDTO(mayTinhEntity);

                    // 2. Fetch the latest GhiChu for this specific MayTinh
                    Optional<GhiChuMayTinh> latestGhiChuOpt = ghiChuMayTinhRepository
                            .findTopByMayTinhOrderByMaGhiChuMTDesc(mayTinhEntity);

                    // 3. Set the noiDungGhiChu if found
                    latestGhiChuOpt.ifPresent(latestGhiChu ->
                            dto.setNoiDungGhiChu(latestGhiChu.getNoiDung())
                    );
                    // If not present, dto.noiDungGhiChu remains null (default)

                    return dto;
                })
                .collect(Collectors.toList());

        // logger.info("Successfully fetched and mapped {} MayTinhDTOs", dtos.size()); // Optional logging
        return dtos;
    }

    // Phương thức lấy danh sách tất cả máy tính
    // Add @Transactional(readOnly = true) for performance optimization on reads
    @Cacheable(value = "maytinhDTO", key = "#maMay")
    @Transactional(readOnly = true)
    public MayTinhDTO layMayTinhDTOTheoMa(Long maMay, String token) {
        if (!isUserLoggedIn(token)) {
            return null;
        }

        Optional<MayTinh> mayTinhOptional = mayTinhRepository.findById(maMay);

        if (mayTinhOptional.isPresent()) {
            MayTinh mayTinhEntity = mayTinhOptional.get();
            MayTinhDTO dto = mapToMayTinhDTO(mayTinhEntity); // Map base fields

            Optional<GhiChuMayTinh> latestGhiChuOpt = ghiChuMayTinhRepository
                    .findTopByMayTinhOrderByMaGhiChuMTDesc(mayTinhEntity);

            if (latestGhiChuOpt.isPresent()) {
                GhiChuMayTinh latestGhiChu = latestGhiChuOpt.get();
                dto.setNoiDungGhiChu(latestGhiChu.getNoiDung()); // <--- Use RENAMED setter
            } else {
                dto.setNoiDungGhiChu(null); // <--- Use RENAMED setter
            }

            return dto;
        } else {
            return null;
        }
    }

    // mapToMayTinhDTO helper remains the same as it doesn't handle the GhiChu field
    private MayTinhDTO mapToMayTinhDTO(MayTinh entity) {
        if (entity == null) {
            return null;
        }
        MayTinhDTO dto = new MayTinhDTO();
        dto.setMaMay(entity.getMaMay());
        dto.setTenMay(entity.getTenMay());
        dto.setTrangThai(entity.getTrangThai());
        dto.setMoTa(entity.getMoTa());
        dto.setNgayLapDat(entity.getNgayLapDat());
        dto.setNgayCapNhat(entity.getNgayCapNhat());

        PhongMay phongMay = entity.getPhongMay();
        if (phongMay != null) {
            dto.setMaPhong(phongMay.getMaPhong());
            dto.setTenPhong(phongMay.getTenPhong());
        } else {
            dto.setMaPhong(null);
            dto.setTenPhong(null);
        }
        // noiDungGhiChu is set in the main method, not here.
        return dto;
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
    @Transactional // Đảm bảo tất cả các cập nhật thành công hoặc tất cả thất bại
    @Caching(evict = {
            @CacheEvict(value = "maytinhs", allEntries = true), // Xóa cache danh sách tất cả máy tính
            // Cân nhắc: Xóa cache từng máy cụ thể nếu cần hiệu năng cao hơn,
            // nhưng xóa toàn bộ cache 'maytinh' đơn giản hơn khi cập nhật nhiều.
            @CacheEvict(value = "maytinh", allEntries = true)
    })
    public List<MayTinh> capNhatTrangThaiNhieuMay(List<Long> maMayTinhList, List<String> trangThaiList, String token) {
        if (!isUserLoggedIn(token)) {
            // Ném ra lỗi rõ ràng hơn thay vì chỉ trả về null/empty list
            throw new AccessDeniedException("Unauthorized: Invalid or expired token.");
        }

        if (maMayTinhList == null || trangThaiList == null || maMayTinhList.size() != trangThaiList.size()) {
            throw new IllegalArgumentException("Danh sách mã máy và danh sách trạng thái phải cùng kích thước và không được null.");
        }

        if (maMayTinhList.isEmpty()) {
            return new ArrayList<>(); // Không có gì để cập nhật, trả về danh sách rỗng
        }

        List<MayTinh> updatedMayTinhList = new ArrayList<>();

        for (int i = 0; i < maMayTinhList.size(); i++) {
            Long maMay = maMayTinhList.get(i);
            String newTrangThai = trangThaiList.get(i);

            // Validate newTrangThai if needed (e.g., check against allowed statuses)
            // if (!isValidStatus(newTrangThai)) {
            //     throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newTrangThai + " cho máy " + maMay);
            // }

            // Tìm máy tính theo mã
            MayTinh mayTinh = mayTinhRepository.findById(maMay)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy máy tính với mã: " + maMay));

            // Cập nhật trạng thái
            mayTinh.setTrangThai(newTrangThai);
            // @UpdateTimestamp sẽ tự động cập nhật ngayChinhSua

            // Lưu lại (trong ngữ cảnh @Transactional, việc lưu sẽ được quản lý)
            MayTinh savedMayTinh = mayTinhRepository.save(mayTinh);
            updatedMayTinhList.add(savedMayTinh);
        }

        return updatedMayTinhList; // Trả về danh sách các máy đã được cập nhật
    }
}