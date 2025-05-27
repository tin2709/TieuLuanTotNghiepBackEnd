package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.Map;

public class FrontendTranslationRequest {
    private String targetLanguage;
    private Map<String, Object> textContent; // Nhận cấu trúc JSON từ frontend
    private String sourceLanguage; // Optional

    // Getters and Setters (cần cho Spring)
    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }
    public Map<String, Object> getTextContent() { return textContent; }
    public void setTextContent(Map<String, Object> textContent) { this.textContent = textContent; }
    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }
}