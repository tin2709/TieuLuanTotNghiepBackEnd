package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Map;
// import com.fasterxml.jackson.annotation.JsonProperty; // Nếu cần dùng camelCase trong Java DTOs

// Lớp biểu diễn một dòng log chi tiết đã được parse (LogEntryDetail bên Python)
// Tên các trường (attributes) phải khớp với key trong JSON mà FastAPI trả về
public class LogEntryDetailResponse {

    // Tên trường khớp với JSON key từ Python (snake_case mặc định của Pydantic)
    private String time;
    private String user;
    private String action;
    private String status;
    // details là Dict[str, Any] -> Map<String, Object>
    // Pydantic default_factory=dict sẽ trả về {} nếu không có details cụ thể
    private Map<String, Object> details;


    // Default constructor (cần thiết cho Jackson để deserialize)
    public LogEntryDetailResponse() {
    }

    // Getters and Setters (Jackson sử dụng getter/setter hoặc public fields để map)
    // Nếu bạn dùng Lombok, chỉ cần thêm @Data annotation

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    // Optional: toString() for debugging
    @Override
    public String toString() {
        return "LogEntryDetailResponse{" +
                "time='" + time + '\'' +
                ", user='" + user + '\'' +
                ", action='" + action + '\'' +
                ", status='" + status + '\'' +
                ", details=" + details +
                '}';
    }
}