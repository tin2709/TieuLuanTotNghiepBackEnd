package com.example.QuanLyPhongMayBackEnd.security;


import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        // Gửi tất cả các exception tới Sentry
        Sentry.captureException(ex);

        // Trả về thông báo lỗi
        return new ResponseEntity<>("Đã xảy ra lỗi. Vui lòng thử lại sau.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

