package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Keep if you use delete methods

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    // Tìm theo user_id (Khóa chính của TaiKhoan là maTK)
    List<UserPermission> findByTaiKhoan_MaTK(Long userId);

    // Kiểm tra tồn tại theo user, resource, action
    boolean existsByTaiKhoan_MaTKAndResourceAndAction(Long userId, String resource, String action);

    // Tìm một quyền cụ thể
    Optional<UserPermission> findByTaiKhoan_MaTKAndResourceAndAction(Long userId, String resource, String action);

    // Xóa một quyền cụ thể
    @Transactional
    void deleteByTaiKhoan_MaTKAndResourceAndAction(Long userId, String resource, String action);

    // --- NEW: Find all permissions for a list of user IDs ---
    List<UserPermission> findByTaiKhoan_MaTKIn(List<Long> userIds);
    // --- END NEW ---
}