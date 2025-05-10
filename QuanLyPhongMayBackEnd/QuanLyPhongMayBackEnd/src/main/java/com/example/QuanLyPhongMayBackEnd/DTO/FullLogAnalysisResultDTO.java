package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.List;
// import com.fasterxml.jackson.annotation.JsonProperty; // Nếu cần dùng camelCase

// Lớp biểu diễn cấu trúc JSON TỔNG THỂ mà FastAPI trả về (FullLogAnalysisResult bên Python)
public class FullLogAnalysisResultDTO {

    // Tên trường 'summary' phải khớp với key 'summary' trong JSON từ FastAPI
    // Kiểu dữ liệu là DTO tương ứng với LogAnalysisSummary
    private LogAnalysisSummaryResponse summary; // Hoặc LogAnalysisSummaryDTO nếu bạn đổi tên

    // Tên trường 'parsed_entries' phải khớp với key 'parsed_entries' trong JSON từ FastAPI
    // Kiểu dữ liệu là List của DTO tương ứng với LogEntryDetail
//    private List<LogEntryDetailResponse> parsed_entries; // <-- UNCOMMENTED

    // Default constructor (cần thiết cho Jackson)
    public FullLogAnalysisResultDTO() {
    }

    // Getters and Setters (hoặc dùng Lombok @Data)

    public LogAnalysisSummaryResponse getSummary() {
        return summary;
    }

    public void setSummary(LogAnalysisSummaryResponse summary) {
        this.summary = summary;
    }

//    public List<LogEntryDetailResponse> getParsed_entries() { // <-- NEW/UNCOMMENTED GETTER
//        return parsed_entries;
//    }
//
//    public void setParsed_entries(List<LogEntryDetailResponse> parsed_entries) { // <-- NEW/UNCOMMENTED SETTER
//        this.parsed_entries = parsed_entries;
//    }


    // Optional: toString() for debugging (Update to include parsed_entries)
    @Override
    public String toString() {
        return "FullLogAnalysisResultDTO{" +
                "summary=" + summary +
//                ", parsed_entries=" + (parsed_entries != null ? parsed_entries.size() : 0) + " entries" + // Avoid printing potentially large list
                '}';
    }
}