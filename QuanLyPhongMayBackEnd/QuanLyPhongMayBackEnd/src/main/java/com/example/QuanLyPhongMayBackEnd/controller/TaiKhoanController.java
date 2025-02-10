package com.example.QuanLyPhongMayBackEnd.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.service.TaiKhoanService;

@RestController
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    // API để lưu tài khoản
    @PostMapping("/taikhoan")
    public ResponseEntity<TaiKhoan> luuTaiKhoan(@RequestBody TaiKhoan taiKhoan) {
        TaiKhoan savedTaiKhoan = taiKhoanService.luu(taiKhoan);
        return new ResponseEntity<>(savedTaiKhoan, HttpStatus.CREATED);
    }

    // API để xóa tài khoản theo mã
    @DeleteMapping("/taikhoan/{maTK}")
    public ResponseEntity<String> xoaTaiKhoan(@PathVariable("maTK") String maTK) {
        taiKhoanService.xoa(maTK);
        return new ResponseEntity<>("Tài khoản với mã " + maTK + " đã được xóa.", HttpStatus.OK);
    }
}

