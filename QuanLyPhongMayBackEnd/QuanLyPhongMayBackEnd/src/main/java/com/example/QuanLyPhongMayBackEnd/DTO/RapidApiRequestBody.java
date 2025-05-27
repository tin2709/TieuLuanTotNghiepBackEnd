package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.List;

public class RapidApiRequestBody {
    private String from; // source language
    private String to; // target language
    private Object json; // The JSON content to translate
    private List<String> protected_paths; // Paths not to translate
    private List<String> common_protected_paths; // Common paths not to translate

    // Constructor (Optional, helpful for building)
    public RapidApiRequestBody() {}

    public RapidApiRequestBody(String from, String to, Object json, List<String> protected_paths, List<String> common_protected_paths) {
        this.from = from;
        this.to = to;
        this.json = json;
        this.protected_paths = protected_paths;
        this.common_protected_paths = common_protected_paths;
    }

    // Getters and Setters (required for serialization/deserialization)
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public Object getJson() { return json; }
    public void setJson(Object json) { this.json = json; }
    public List<String> getProtected_paths() { return protected_paths; }
    public void setProtected_paths(List<String> protected_paths) { this.protected_paths = protected_paths; }
    public List<String> getCommon_protected_paths() { return common_protected_paths; }
    public void setCommon_protected_paths(List<String> common_protected_paths) { this.common_protected_paths = common_protected_paths; }
}