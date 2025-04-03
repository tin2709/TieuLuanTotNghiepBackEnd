package com.example.QuanLyPhongMayBackEnd.repository;

import com.example.QuanLyPhongMayBackEnd.entity.MayTinh;
import com.example.QuanLyPhongMayBackEnd.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // If you add custom methods returning lists

@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Long> {
    public List<ThietBi> findByTrangThai(String trangThai);
}