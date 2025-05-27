package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Map;

// Ánh xạ cấu trúc trả về từ RapidAPI dựa trên mẫu bạn cung cấp
public class RapidApiResponse {
    private boolean status;
    private String message;
    private RapidApiResult result;

    // Getters and Setters
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public RapidApiResult getResult() { return result; }
    public void setResult(RapidApiResult result) { this.result = result; }

    public static class RapidApiResult {
        private Map<String, Object> jsonContent; // Phần chứa JSON đã dịch

        // Getters and Setters
        public Map<String, Object> getJsonContent() { return jsonContent; }
        public void setJsonContent(Map<String, Object> jsonContent) { this.jsonContent = jsonContent; }

        // Các trường khác có thể có trong result, bạn có thể thêm vào nếu cần
        // private String detectedSourceLanguage;
    }
}