package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.Tang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TangRepository extends JpaRepository<Tang, Long> {

    public List<Tang> findByToaNha_MaToaNha(Long maToaNha);

    public	Long countByToaNha_MaToaNha(Long maToaNha);

}
