package com.example.QuanLyPhongMayBackEnd.controller;

// Import DTO chính đã thay đổi
import com.example.QuanLyPhongMayBackEnd.DTO.FullLogAnalysisResultDTO; // <-- Sửa import này

import com.example.QuanLyPhongMayBackEnd.service.LogAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin // Cho phép truy cập từ frontend ở domain khác
// Tên class nên khớp với tên file LogAnalysisController.java
public class LogAnalysisController{

    // Tốt nhất nên dùng constructor injection thay vì field injection
    private final LogAnalysisService logAnalysisService;

    // Constructor injection
    @Autowired // Annotation này tùy chọn nếu chỉ có một constructor
    public LogAnalysisController(LogAnalysisService logAnalysisService) {
        this.logAnalysisService = logAnalysisService;
    }


    /**
     * API endpoint để nhận file log từ frontend và gửi đi phân tích bởi FastAPI.
     * @param file File log được gửi trong request part có tên là "file".
     * @return ResponseEntity chứa kết quả phân tích hoặc thông báo lỗi.
     */
    @PostMapping(value = "/analyze-log", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    // Thay đổi kiểu generic của ResponseEntity để khớp với Service
    public Mono<ResponseEntity<FullLogAnalysisResultDTO>> analyzeLog(@RequestParam("file") MultipartFile file,@RequestParam String token) { // <-- Sửa ở đây

        if (file.isEmpty()) {
            // Trả về 400 Bad Request. Body có thể là null hoặc một DTO lỗi chung nếu bạn định nghĩa
            // Trả về null body cho FullLogAnalysisResultDTO khi bad request
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        // logAnalysisService.analyzeLogFile bây giờ trả về Mono<FullLogAnalysisResultDTO>
        return logAnalysisService.analyzeLogFile(file, token)
                // analysisResult ở đây là FullLogAnalysisResultDTO
                .map(analysisResult -> ResponseEntity.ok(analysisResult))
                .onErrorResume(e -> {
                    // Lỗi đã được log và wrap trong service, chỉ cần log lại ở controller
                    System.err.println("Error during log analysis request in controller: " + e.getMessage());
                    // Log stack trace của lỗi gốc từ service (đã được log trong service nhưng log lại ở đây cũng không sao)
                    e.printStackTrace();
                    // Trả về response lỗi cho client (frontend)
                    // Trả về null body cho FullLogAnalysisResultDTO khi internal server error
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }
}