package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
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
public interface GhiChuMayTinhRepository extends JpaRepository<GhiChuMayTinh, Long> {

    // Your existing methods...
    List<GhiChuMayTinh> findByNgayBaoLoi(@Temporal(TemporalType.DATE) Date ngayBaoLoi);
    List<GhiChuMayTinh> findByNgaySua( @Temporal(TemporalType.DATE) Date ngaySua);
    List<GhiChuMayTinh> findByMayTinh_MaMay(Long maMay);
    List<GhiChuMayTinh> findByMayTinh_MaMayOrderByNgayBaoLoiDesc(Long maMay);

    @Query("SELECT gc FROM GhiChuMayTinh gc " +
            "LEFT JOIN FETCH gc.mayTinh mt " +
            // LEFT JOIN FETCH mt.phongMay pm  // Only if PhongMay is accessed VIA MayTinh
            "LEFT JOIN FETCH gc.phongMay pm " + // Direct fetch for phongMay in GhiChuMayTinh
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl " +
            "WHERE gc.mayTinh.maMay = :maMay " +
            "ORDER BY gc.ngayBaoLoi DESC")
    List<GhiChuMayTinh> findLatestByMayTinhWithDetails(@Param("maMay") Long maMay);

    Optional<GhiChuMayTinh> findTopByMayTinhOrderByMaGhiChuMTDesc(MayTinh mayTinh);

    // *** NEW METHOD ***
    @Query("SELECT gc FROM GhiChuMayTinh gc " +
            "LEFT JOIN FETCH gc.mayTinh " +          // Fetch MayTinh
            "LEFT JOIN FETCH gc.phongMay " +         // Fetch PhongMay (direct relation)
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi " +   // Fetch TaiKhoanBaoLoi
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi")     // Fetch TaiKhoanSuaLoi
    List<GhiChuMayTinh> findAllWithDetails(); // Method to fetch all GhiChuMayTinh with related entities

}