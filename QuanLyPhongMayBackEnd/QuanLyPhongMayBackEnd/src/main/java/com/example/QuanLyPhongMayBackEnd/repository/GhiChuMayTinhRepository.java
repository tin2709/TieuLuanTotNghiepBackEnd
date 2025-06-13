// Trong com.example.QuanLyPhongMayBackEnd.repository/GhiChuMayTinhRepository.java
package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.TemporalType; // Correct import for Jakarta Persistence

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface GhiChuMayTinhRepository extends JpaRepository<GhiChuMayTinh, Long>, JpaSpecificationExecutor<GhiChuMayTinh> {

    // Existing methods - Keep as is
    List<GhiChuMayTinh> findByNgayBaoLoi(@Temporal(TemporalType.DATE) Date ngayBaoLoi);
    List<GhiChuMayTinh> findByNgaySua(@Temporal(TemporalType.DATE) Date ngaySua);
    List<GhiChuMayTinh> findByMayTinh_MaMay(Long maMay);
    List<GhiChuMayTinh> findByMayTinh_MaMayOrderByNgayBaoLoiDesc(Long maMay);

    // Phương thức tìm ghi chú gần nhất cho một máy tính (dựa trên MaGhiChuMT)
    // Lưu ý: "gần nhất" ở đây là theo ID, không phải ngày báo lỗi.
    // Nếu bạn cần "gần nhất" theo NgayBaoLoi, phương thức findLatestByMayTinhWithDetails sẽ phù hợp hơn.
    Optional<GhiChuMayTinh> findTopByMayTinhOrderByMaGhiChuMTDesc(MayTinh mayTinh);


    // Phương thức lấy ghi chú gần nhất cho một máy tính CÙNG VỚI CÁC THÔNG TIN CHI TIẾT (JOIN FETCH)
    // Dùng cho việc hiển thị ghi chú gần nhất (LayGhiChuGanNhatDTOTheoMayTinh)
    @Query("SELECT gc FROM GhiChuMayTinh gc " +
            "JOIN FETCH gc.mayTinh mt " +          // Máy tính là bắt buộc, dùng JOIN FETCH
            "LEFT JOIN FETCH mt.phongMay pm " +     // PhongMay thường được truy cập qua MayTinh
            "LEFT JOIN FETCH gc.phongMay directPm " + // (Nếu GhiChuMayTinh có liên kết trực tiếp với PhongMay)
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl " +
            "WHERE gc.mayTinh.maMay = :maMay " +
            "ORDER BY gc.ngayBaoLoi DESC")
    List<GhiChuMayTinh> findLatestByMayTinhWithDetails(@Param("maMay") Long maMay);

    // *** PHƯƠNG THỨC CHÍNH ĐỂ TÌM GHI CHÚ CHƯA ĐƯỢC SỬA (FIXED) ***
    // Nó được định nghĩa bằng @Query vì quá phức tạp cho tên phương thức tự động của Spring Data JPA.
    // Tìm ghi chú gần nhất (theo ngayBaoLoi) cho một máy tính CÓ ngaySua LÀ NULL (chưa được sửa xong)
    // Bao gồm JOIN FETCH để tải đầy đủ các thông tin liên quan, tránh N+1 khi truy cập chúng trong Service
    @Query("SELECT gc FROM GhiChuMayTinh gc " +
            "JOIN FETCH gc.mayTinh mt " +          // Cần join MayTinh để lọc theo maMay
            "LEFT JOIN FETCH mt.phongMay pm " +     // Fetch PhongMay qua MayTinh
            "LEFT JOIN FETCH gc.phongMay directPm " + // Fetch PhongMay trực tiếp nếu có
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl " +
            "WHERE gc.mayTinh.maMay = :maMay AND gc.ngaySua IS NULL " + // Điều kiện then chốt: ngaySua là NULL
            "ORDER BY gc.ngayBaoLoi DESC")
    // Trả về List<GhiChuMayTinh> để service có thể kiểm tra isEmpty() và lấy phần tử đầu tiên.
    List<GhiChuMayTinh> findLatestUnfixedByMayTinhMaMayWithDetails(@Param("maMay") Long maMay);


    // Phương thức để fetch tất cả GhiChuMayTinh cùng với các thực thể liên quan
    // Hữu ích cho việc hiển thị danh sách chi tiết mà không gây N+1 (layDSGhiChu)
    @Query("SELECT gc FROM GhiChuMayTinh gc " +
            "LEFT JOIN FETCH gc.mayTinh mt " +
            "LEFT JOIN FETCH mt.phongMay pm " + // Lấy phòng máy thông qua MayTinh
            "LEFT JOIN FETCH gc.phongMay directPm " + // Và/hoặc trực tiếp từ GhiChuMayTinh
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl")
    List<GhiChuMayTinh> findAllWithDetails();
}