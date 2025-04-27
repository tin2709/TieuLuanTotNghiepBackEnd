package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuThietBi;
import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Import this
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
// Add JpaSpecificationExecutor<GhiChuThietBi>
public interface GhiChuThietBiRepository extends JpaRepository<GhiChuThietBi, Long>, JpaSpecificationExecutor<GhiChuThietBi> {

    List<GhiChuThietBi> findByNgayBaoLoi(@Temporal(TemporalType.DATE) Date ngayBaoLoi);
    List<GhiChuThietBi> findByNgaySua( @Temporal(TemporalType.DATE) Date ngaySua);

    // Find by ThietBi ID
    List<GhiChuThietBi> findByThietBi_MaThietBi(Long maThietBi);

    // Find by ThietBi ID, ordered
    List<GhiChuThietBi> findByThietBi_MaThietBiOrderByNgayBaoLoiDesc(Long maThietBi);

    // Optimized query to fetch latest note with related details for a specific ThietBi
    @Query("SELECT gc FROM GhiChuThietBi gc " +
            "LEFT JOIN FETCH gc.thietBi tb " +
            "LEFT JOIN FETCH tb.loaiThietBi ltb " + // Fetch LoaiThietBi via ThietBi
            "LEFT JOIN FETCH tb.phongMay pm " +     // Fetch PhongMay via ThietBi
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl " +
            "WHERE gc.thietBi.maThietBi = :maThietBi " +
            "ORDER BY gc.ngayBaoLoi DESC")
    List<GhiChuThietBi> findLatestByThietBiWithDetails(@Param("maThietBi") Long maThietBi);

    // Find the top (latest by PK) note for a given ThietBi object
    Optional<GhiChuThietBi> findTopByThietBiOrderByMaGhiChuTBDesc(ThietBi thietBi);

    // It seems you don't have findAllWithDetails for GhiChuThietBi yet, add it if needed for layDSGhiChu
    @Query("SELECT gc FROM GhiChuThietBi gc " +
            "LEFT JOIN FETCH gc.thietBi tb " +
            "LEFT JOIN FETCH tb.loaiThietBi ltb " +
            "LEFT JOIN FETCH tb.phongMay pm " +
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi")
    List<GhiChuThietBi> findAllWithDetails(); // Added this for consistency if layDSGhiChu uses it


}