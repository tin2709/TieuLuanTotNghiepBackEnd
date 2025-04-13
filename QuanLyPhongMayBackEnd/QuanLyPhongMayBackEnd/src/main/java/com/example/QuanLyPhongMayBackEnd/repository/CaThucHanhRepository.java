package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CaThucHanhRepository extends JpaRepository<CaThucHanh, Long>, JpaSpecificationExecutor<CaThucHanh> {
    public List<CaThucHanh> findByNgayThucHanh(@Temporal(TemporalType.DATE) Date ngayThucHanh);
    public List<CaThucHanh> findByMonHoc_MaMon(Long maMon);
    public List<CaThucHanh> findByPhongMay_MaPhong(Long maPhong);
    public List<CaThucHanh> findByGiaoVien_HoTen(String hoTen); // Corrected to HoTen to match GiaoVien entity field

//    @Query("SELECT c FROM CaThucHanh c WHERE c.${column} LIKE :value")
//    List<CaThucHanh> searchByColumn(@Param("column") String column, @Param("value") String value);









}
