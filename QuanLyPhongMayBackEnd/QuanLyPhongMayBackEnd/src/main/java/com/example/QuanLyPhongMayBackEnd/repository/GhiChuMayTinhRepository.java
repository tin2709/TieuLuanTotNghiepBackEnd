package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GhiChuMayTinh;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GhiChuMayTinhRepository extends JpaRepository<GhiChuMayTinh, Long> {
    public List<GhiChuMayTinh> findByNgayBaoLoi(@Temporal(TemporalType.DATE) Date ngayBaoLoi);
    public List<GhiChuMayTinh> findByNgaySua( @Temporal(TemporalType.DATE) Date ngaySua);
    public List<GhiChuMayTinh> findByMayTinh_MaMay(Long maMay);
    public List<GhiChuMayTinh> findByMayTinh_MaMayOrderByNgayBaoLoiDesc(Long maMay);

}
