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
    public List<GhiChuMayTinh> findByNgayBaoLoi(@Temporal(TemporalType.DATE) Date ngayBaoLoi);
    public List<GhiChuMayTinh> findByNgaySua( @Temporal(TemporalType.DATE) Date ngaySua);
    public List<GhiChuMayTinh> findByMayTinh_MaMay(Long maMay);
    public List<GhiChuMayTinh> findByMayTinh_MaMayOrderByNgayBaoLoiDesc(Long maMay);
    @Query("SELECT gc FROM GhiChuMayTinh gc " +
            "LEFT JOIN FETCH gc.mayTinh mt " +
            "LEFT JOIN FETCH mt.phongMay pm " + // Fetch PhongMay via MayTinh
            "LEFT JOIN FETCH gc.taiKhoanBaoLoi tkbl " +
            "LEFT JOIN FETCH gc.taiKhoanSuaLoi tksl " +
            "WHERE gc.mayTinh.maMay = :maMay " +
            "ORDER BY gc.ngayBaoLoi DESC")
    List<GhiChuMayTinh> findLatestByMayTinhWithDetails(@Param("maMay") Long maMay); // Fetch list ordered
    Optional<GhiChuMayTinh> findTopByMayTinhOrderByMaGhiChuMTDesc(MayTinh mayTinh);


}
