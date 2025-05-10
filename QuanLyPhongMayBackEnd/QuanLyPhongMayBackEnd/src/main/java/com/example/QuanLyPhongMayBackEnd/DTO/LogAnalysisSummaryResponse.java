package com.example.QuanLyPhongMayBackEnd.DTO;

import java.util.List;
import java.util.Map;
// import com.fasterxml.jackson.annotation.JsonProperty; // Nếu cần dùng camelCase

// Lớp biểu diễn phần tổng kết (LogAnalysisSummary bên Python)
// Tên các trường (attributes) phải khớp với key trong JSON TỪ PHẦN 'summary' của FastAPI
public class LogAnalysisSummaryResponse { // Hoặc đổi tên thành LogAnalysisSummaryDTO

    // Sử dụng tên biến khớp với JSON key từ Python (snake_case)
    private int total_lines;
    private int date_lines;
    private int detail_lines;
    private int summary_lines;
    private int error_count;
    private int access_denied_count;
    private int optimistic_lock_count;
    private int duplicate_entry_count;
    private int null_or_transient_value_error_count;
    private int tang_not_found_count;
    private int invalid_format_count;
    private int invalid_column_count;
    private int multiple_bag_fetch_error_count;
    private int jwt_error_count;
    private int other_errors_count;
    private List<String> users_active;
    private Map<String, Map<String, Integer>> actions_summary;
    private Map<String, Integer> errors_by_type;
    private List<String> repeated_action_warnings; // <-- NEW FIELD
    private List<String> behavior_anomaly_warnings; // <-- NEW FIELD
    private String summary_message;


    // Default constructor
    public LogAnalysisSummaryResponse() {
    }

    // Getters and Setters (hoặc dùng Lombok @Data)

    public int getTotal_lines() { return total_lines; }
    public void setTotal_lines(int total_lines) { this.total_lines = total_lines; }

    public int getDate_lines() { return date_lines; }
    public void setDate_lines(int date_lines) { this.date_lines = date_lines; }

    public int getDetail_lines() { return detail_lines; }
    public void setDetail_lines(int detail_lines) { this.detail_lines = detail_lines; }

    public int getSummary_lines() { return summary_lines; }
    public void setSummary_lines(int summary_lines) { this.summary_lines = summary_lines; }

    public int getError_count() { return error_count; }
    public void setError_count(int error_count) { this.error_count = error_count; }

    public int getAccess_denied_count() { return access_denied_count; }
    public void setAccess_denied_count(int access_denied_count) { this.access_denied_count = access_denied_count; }

    public int getOptimistic_lock_count() { return optimistic_lock_count; }
    public void setOptimistic_lock_count(int optimistic_lock_count) { this.optimistic_lock_count = optimistic_lock_count; }

    public int getDuplicate_entry_count() { return duplicate_entry_count; }
    public void setDuplicate_entry_count(int duplicate_entry_count) { this.duplicate_entry_count = duplicate_entry_count; }

    public int getNull_or_transient_value_error_count() { return null_or_transient_value_error_count; }
    public void setNull_or_transient_value_error_count(int null_or_transient_value_error_count) { this.null_or_transient_value_error_count = null_or_transient_value_error_count; }

    public int getTang_not_found_count() { return tang_not_found_count; }
    public void setTang_not_found_count(int tang_not_found_count) { this.tang_not_found_count = tang_not_found_count; }

    public int getInvalid_format_count() { return invalid_format_count; }
    public void setInvalid_format_count(int invalid_format_count) { this.invalid_format_count = invalid_format_count; }

    public int getInvalid_column_count() { return invalid_column_count; }
    public void setInvalid_column_count(int invalid_column_count) { this.invalid_column_count = invalid_column_count; }

    public int getMultiple_bag_fetch_error_count() { return multiple_bag_fetch_error_count; }
    public void setMultiple_bag_fetch_error_count(int multiple_bag_fetch_error_count) { this.multiple_bag_fetch_error_count = multiple_bag_fetch_error_count; }

    public int getJwt_error_count() { return jwt_error_count; }
    public void setJwt_error_count(int jwt_error_count) { this.jwt_error_count = jwt_error_count; }

    public int getOther_errors_count() { return other_errors_count; }
    public void setOther_errors_count(int other_errors_count) { this.other_errors_count = other_errors_count; }

    public List<String> getUsers_active() { return users_active; }
    public void setUsers_active(List<String> users_active) { this.users_active = users_active; }

    public Map<String, Map<String, Integer>> getActions_summary() { return actions_summary; }
    public void setActions_summary(Map<String, Map<String, Integer>> actions_summary) { this.actions_summary = actions_summary; }

    public Map<String, Integer> getErrors_by_type() { return errors_by_type; }
    public void setErrors_by_type(Map<String, Integer> errors_by_type) { this.errors_by_type = errors_by_type; }

    public List<String> getRepeated_action_warnings() { // <-- NEW GETTER
        return repeated_action_warnings;
    }

    public void setRepeated_action_warnings(List<String> repeated_action_warnings) { // <-- NEW SETTER
        this.repeated_action_warnings = repeated_action_warnings;
    }

    public List<String> getBehavior_anomaly_warnings() { // <-- NEW GETTER
        return behavior_anomaly_warnings;
    }

    public void setBehavior_anomaly_warnings(List<String> behavior_anomaly_warnings) { // <-- NEW SETTER
        this.behavior_anomaly_warnings = behavior_anomaly_warnings;
    }


    public String getSummary_message() { return summary_message; }
    public void setSummary_message(String summary_message) { this.summary_message = summary_message; }

    // Optional: toString() for debugging (Update to include new fields)
    @Override
    public String toString() {
        return "LogAnalysisSummaryResponse{" +
                "total_lines=" + total_lines +
                ", date_lines=" + date_lines +
                ", detail_lines=" + detail_lines +
                ", summary_lines=" + summary_lines +
                ", error_count=" + error_count +
                ", access_denied_count=" + access_denied_count +
                ", optimistic_lock_count=" + optimistic_lock_count +
                ", duplicate_entry_count=" + duplicate_entry_count +
                ", null_or_transient_value_error_count=" + null_or_transient_value_error_count +
                ", tang_not_found_count=" + tang_not_found_count +
                ", invalid_format_count=" + invalid_format_count +
                ", invalid_column_count=" + invalid_column_count +
                ", multiple_bag_fetch_error_count=" + multiple_bag_fetch_error_count +
                ", jwt_error_count=" + jwt_error_count +
                ", other_errors_count=" + other_errors_count +
                ", users_active=" + users_active +
                ", actions_summary=" + actions_summary +
                ", errors_by_type=" + errors_by_type +
                ", repeated_action_warnings=" + repeated_action_warnings + // <-- Include in toString
                ", behavior_anomaly_warnings=" + behavior_anomaly_warnings + // <-- Include in toString
                ", summary_message='" + summary_message + '\'' +
                '}';
    }
}