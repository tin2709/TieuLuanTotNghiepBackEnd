package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MayTinhRepository extends JpaRepository<MayTinh, Long> {
    public List<MayTinh> findByPhongMay_MaPhong(Long maPhong);
    public List<MayTinh> findByTrangThai(String trangThai);
}
