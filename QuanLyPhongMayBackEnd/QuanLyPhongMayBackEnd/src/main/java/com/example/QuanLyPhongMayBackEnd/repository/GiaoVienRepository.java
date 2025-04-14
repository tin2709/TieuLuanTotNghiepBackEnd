package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.GiaoVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiaoVienRepository extends JpaRepository<GiaoVien, String>, JpaSpecificationExecutor<GiaoVien> {

    // Custom method to delete GiaoVien by maGiaoVien
    void deleteByMaGiaoVien(Long maGiaoVien);
    @Query("SELECT g FROM GiaoVien g JOIN FETCH g.khoa") // JPQL query to fetch GiaoVien and join fetch Khoa
    List<GiaoVien> findAllWithKhoa();
}
