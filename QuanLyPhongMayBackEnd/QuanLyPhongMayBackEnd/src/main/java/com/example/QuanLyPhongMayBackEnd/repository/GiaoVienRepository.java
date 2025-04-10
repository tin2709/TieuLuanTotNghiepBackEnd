package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GiaoVienRepository extends JpaRepository<GiaoVien, String>, JpaSpecificationExecutor<GiaoVien> {

    // Custom method to delete GiaoVien by maGiaoVien
    void deleteByMaGiaoVien(Long maGiaoVien);
}
