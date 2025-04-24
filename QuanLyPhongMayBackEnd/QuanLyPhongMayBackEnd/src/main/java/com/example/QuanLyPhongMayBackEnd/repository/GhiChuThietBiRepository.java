package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuThietBi; // Changed entity
import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;     // Changed entity
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface GhiChuThietBiRepository extends JpaRepository<GhiChuThietBi, Long> { // Changed entity

    List<GhiChuThietBi> findByNgayBaoLoi(@Temporal(TemporalType.DATE) Date ngayBaoLoi);
    List<GhiChuThietBi> findByNgaySua( @Temporal(TemporalType.DATE) Date ngaySua);

    // Find by ThietBi ID
    List<GhiChuThietBi> findByThietBi_MaThietBi(Long maThietBi); // Changed method name and parameter

    // Find by ThietBi ID, ordered
    List<GhiChuThietBi> findByThietBi_MaThietBiOrderByNgayBaoLoiDesc(Long maThietBi); // Changed method name and parameter

    // Optimized query to fetch latest note with related details for a specific ThietBi
    @Query("SELECT gc FROM GhiChuThietBi gc " + // Changed entity GhiChuThietBi
            "LEFT JOIN FETCH gc.thietBi tb " +      // Changed relation thietBi, alias tb
            "LEFT JOIN FETCH tb.loaiThietBi ltb " + // Fetch LoaiThietBi via ThietBi
            "LEFT JOIN FETCH tb.phongMay pm " +     // Fetch PhongMay via ThietBi
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl " +
            "WHERE gc.thietBi.maThietBi = :maThietBi " + // Changed condition field and param name
            "ORDER BY gc.ngayBaoLoi DESC")
    List<GhiChuThietBi> findLatestByThietBiWithDetails(@Param("maThietBi") Long maThietBi); // Changed method and param name

    // Find the top (latest by PK) note for a given ThietBi object
    Optional<GhiChuThietBi> findTopByThietBiOrderByMaGhiChuTBDesc(ThietBi thietBi); // Changed method name, PK field, and parameter type
}
