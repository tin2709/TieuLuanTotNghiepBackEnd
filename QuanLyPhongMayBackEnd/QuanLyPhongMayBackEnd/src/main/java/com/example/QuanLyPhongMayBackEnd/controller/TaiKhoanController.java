package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.service.TaiKhoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService taiKhoanService;
    @PostMapping("/luutaikhoan")

    // API để lưu tài khoản
    public String luuTaiKhoan(@Valid @RequestBody TaiKhoan taiKhoan, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        }
        taiKhoanService.luu(taiKhoan);
        return "Tài khoản đã được lưu thành công!";
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldname = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldname, errorMessage);

        });
        return errors;
    }


    // API để xóa tài khoản theo mã
    @DeleteMapping("/taikhoan/{maTK}")
    public ResponseEntity<String> xoaTaiKhoan(@PathVariable("maTK") String maTK) {
        taiKhoanService.xoa(maTK);
        return new ResponseEntity<>("Tài khoản với mã " + maTK + " đã được xóa.", HttpStatus.OK);
    }

    // API phân trang lấy danh sách tài khoản
    @GetMapping("/taikhoan/phantang")
    public ResponseEntity<Page<TaiKhoan>> layDSTaiKhoanPhanTrang(@RequestParam int pageNumber) {
        Page<TaiKhoan> taiKhoans = taiKhoanService.layDSTaiKhoanPhanTrang(pageNumber);
        return new ResponseEntity<>(taiKhoans, HttpStatus.OK);
    }
}
