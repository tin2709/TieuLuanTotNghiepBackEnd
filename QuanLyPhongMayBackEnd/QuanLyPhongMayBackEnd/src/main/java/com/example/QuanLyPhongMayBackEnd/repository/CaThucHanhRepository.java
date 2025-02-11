package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CaThucHanhRepository extends JpaRepository<CaThucHanh, Long> {
    public List<CaThucHanh> findByNgayThucHanh(@Temporal(TemporalType.DATE) Date ngayThucHanh);
    public List<CaThucHanh> findByMonHoc_MaMon(Long maMon);
    public List<CaThucHanh> findByPhongMay_MaPhong(Long maPhong);
}
