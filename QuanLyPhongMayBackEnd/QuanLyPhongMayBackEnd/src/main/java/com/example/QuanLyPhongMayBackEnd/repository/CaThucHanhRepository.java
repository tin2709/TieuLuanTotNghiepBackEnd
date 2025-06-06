package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.CaThucHanh;
import com.example.QuanLyPhongMayBackEnd.entity.PhongMay;
import jakarta.persistence.TemporalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
// import org.springframework.data.jpa.repository.Query; // Not strictly needed if using derived queries or Specification
import org.springframework.data.jpa.repository.Temporal;
// import org.springframework.data.repository.query.Param; // Not strictly needed for these methods
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface CaThucHanhRepository extends JpaRepository<CaThucHanh, Long>, JpaSpecificationExecutor<CaThucHanh> {

    // Finds CTH for a specific date (java.util.Date).
    // @Temporal(TemporalType.DATE) ensures only the date part is compared.
    List<CaThucHanh> findByNgayThucHanh(@Temporal(TemporalType.DATE) Date ngayThucHanh);

    // Finds CTH by the ID of the associated MonHoc.
    List<CaThucHanh> findByMonHoc_MaMon(Long maMon);

    // Finds CTH by the ID of the associated PhongMay.
    List<CaThucHanh> findByPhongMay_MaPhong(Long maPhong);

    // Finds CTH by an exact match of the GiaoVien's hoTen.
    List<CaThucHanh> findByGiaoVien_HoTen(String hoTen);

    // Finds CTH by GiaoVien's hoTen, ignoring case and matching if hoTen contains the search string.
    // This is the method used in the updated service for more flexible teacher name search.
    List<CaThucHanh> findByGiaoVien_HoTenIgnoreCaseContaining(String hoTen);

    // Finds CTH for a specific date (java.time.LocalDate) and a specific PhongMay entity.
    // Using java.time.LocalDate is generally preferred over java.util.Date.
    List<CaThucHanh> findByNgayThucHanhAndPhongMay(LocalDate ngayThucHanh, PhongMay phongMay);


}