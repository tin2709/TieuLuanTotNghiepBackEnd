package com.example.QuanLyPhongMayBackEnd.repository;


import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Long> {

    List<ThietBi> findByTrangThai(String trangThai);

    // Original simple find by room ID (might not be needed if you always use 'WithDetails')
    List<ThietBi> findByPhongMay_MaPhong(Long maPhong);

    // Fetch ThietBi with related entities eagerly
    @Query("SELECT tb FROM ThietBi tb LEFT JOIN FETCH tb.loaiThietBi LEFT JOIN FETCH tb.phongMay")
    List<ThietBi> findAllWithDetails();

    // Fetch ThietBi by room ID with details
    @Query("SELECT tb FROM ThietBi tb LEFT JOIN FETCH tb.loaiThietBi LEFT JOIN FETCH tb.phongMay pm WHERE pm.maPhong = :maPhong")
    List<ThietBi> findByPhongMayMaPhongWithDetails(@Param("maPhong") Long maPhong);

    // *** UPDATED: Find devices by room ID AND type ID, with details ***
    @Query("SELECT tb FROM ThietBi tb " +
            "LEFT JOIN FETCH tb.loaiThietBi ltb " + // Fetch LoaiThietBi
            "LEFT JOIN FETCH tb.phongMay pm " +     // Fetch PhongMay
            "WHERE pm.maPhong = :maPhong AND ltb.maLoai = :maLoai") // Filter by maLoai
    List<ThietBi> findByPhongMayMaPhongAndLoaiThietBiMaLoaiWithDetails( // Method name updated
                                                                        @Param("maPhong") Long maPhong,
                                                                        @Param("maLoai") Long maLoai); // Parameter type changed to Long

    // Remove or comment out the old method filtering by tenLoai if it exists:
    // List<ThietBi> findByPhongMayMaPhongAndLoaiThietBiTenLoaiWithDetails(...);

}