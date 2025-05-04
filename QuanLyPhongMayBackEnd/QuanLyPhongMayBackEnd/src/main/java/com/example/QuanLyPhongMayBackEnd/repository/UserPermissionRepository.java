package com.example.QuanLyPhongMayBackEnd.repository; // Hoặc package repository của bạn

import com.example.QuanLyPhongMayBackEnd.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional // Cần thiết cho các thao tác delete bằng query
    void deleteByTaiKhoan_MaTKAndResourceAndAction(Long userId, String resource, String action);
}