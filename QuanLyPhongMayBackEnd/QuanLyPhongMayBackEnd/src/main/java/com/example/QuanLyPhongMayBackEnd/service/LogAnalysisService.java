package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.DTO.FullLogAnalysisResultDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.io.IOException;


@Service
public class LogAnalysisService {

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @Value("${fastapi.url}")
    private String fastapiBaseUrl;
    @Autowired
    private TaiKhoanService taiKhoanService;

    public LogAnalysisService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }

    @PostConstruct
    public void init() {
        // Tăng giới hạn buffer cho WebClient khi đọc phản hồi
        final int maxMemorySize = 10 * 1024 * 1024; // Ví dụ: 10 MB

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .build();

        this.webClient = webClientBuilder
                .baseUrl(fastapiBaseUrl)
                .exchangeStrategies(exchangeStrategies) // Áp dụng cấu hình giới hạn buffer
                .build();
    }

    public Mono<FullLogAnalysisResultDTO> analyzeLogFile(MultipartFile file, String token) {
        if (!isUserLoggedIn(token)) {
            return null; // Token không hợp lệ
        }
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        try {
            builder.part("file", new ByteArrayResource(file.getBytes()))
                    .filename(file.getOriginalFilename())
                    .contentType(MediaType.TEXT_PLAIN);

        } catch (IOException e) {
            System.err.println("Error reading file from multipart: " + e.getMessage());
            return Mono.error(new RuntimeException("Failed to read file content for upload.", e));
        }

        return this.webClient.post()
                .uri("/api/fastapi/analyze-log")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                // Xử lý các status code lỗi 4xx, 5xx
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    // FIX: Sử dụng toString() thay vì getReasonPhrase()
                                    String errorMsg = String.format("FastAPI returned error: %d %s, Body: %s",
                                            clientResponse.statusCode().value(),
                                            clientResponse.statusCode().toString(), // <-- SỬA Ở ĐÂY
                                            body);
                                    System.err.println(errorMsg);
                                    return Mono.error(new RuntimeException(errorMsg));
                                })
                )
                .bodyToMono(FullLogAnalysisResultDTO.class) // Sử dụng DTO mới
                .onErrorMap(e -> {
                    System.err.println("Error calling FastAPI service or processing response: " + e.getMessage());

                    String errorMessage = "Failed to get log analysis from FastAPI service.";
                    Throwable cause = e.getCause();

                    if (e instanceof WebClientResponseException) {
                        errorMessage = e.getMessage(); // Giữ nguyên message từ onStatus nếu có
                    } else if (cause instanceof DataBufferLimitException) {
                        errorMessage = "Response body from FastAPI exceeded buffer limit: " + cause.getMessage();
                        System.err.println(errorMessage);
                        cause.printStackTrace();
                    } else if (cause != null) {
                        errorMessage = "Failed to process FastAPI response body: " + cause.getMessage();
                        cause.printStackTrace();
                    } else {
                        e.printStackTrace();
                    }

                    return new RuntimeException(errorMessage, e);
                });
    }
}