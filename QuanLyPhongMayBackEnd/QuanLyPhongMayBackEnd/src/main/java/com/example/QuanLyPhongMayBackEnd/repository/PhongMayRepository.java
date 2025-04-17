package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhongMayRepository extends JpaRepository<PhongMay, Long>, JpaSpecificationExecutor<PhongMay> {
    public List<PhongMay> findByTrangThai(String trangThai);

    public List<PhongMay> findByTang_MaTang(Long maTang);
    // Phương thức mới sử dụng FETCH JOIN để tải trước mayTinhs
    @Query("SELECT pm FROM PhongMay pm LEFT JOIN FETCH pm.mayTinhs")
    List<PhongMay> findAllPhongMayWithMayTinhs();
}
