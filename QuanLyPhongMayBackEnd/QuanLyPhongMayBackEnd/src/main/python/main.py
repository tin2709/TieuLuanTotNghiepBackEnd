# src/main/python/main.py
from fastapi import FastAPI, File, UploadFile, HTTPException
# Import Field từ pydantic
from pydantic import BaseModel, Field # Đảm bảo Field được import
from typing import List, Dict, Any, Tuple # Import Tuple for type hinting
# from typing import Optional # Không cần Optional nữa nếu dùng default_factory

import io
import re
from collections import defaultdict
import traceback
from datetime import datetime, timedelta # Import datetime and timedelta for time analysis

app = FastAPI()

# --- Configuration for Repeated Action Detection ---
TIME_WINDOW_SECONDS = 60 # Check for repeated actions within this many seconds
ACTION_THRESHOLD = 10   # Trigger a warning if more than this many actions occur in the window
# -------------------------------------------------


# Định nghĩa cấu trúc của một dòng log chi tiết được phân tích
class LogEntryDetail(BaseModel):
    time: str # Ví dụ: 21:33:32
    user: str
    action: str # Ví dụ: isUserLoggedIn, layDSPhongMay
    status: str # Ví dụ: true, Success, Error, User not logged in.
    # Sửa lỗi: Sử dụng default_factory cho mutable default {}
    # Quay lại dùng default_factory để đảm bảo luôn là {} trong JSON thay vì null
    details: Dict[str, Any] = Field(default_factory=dict)


# Định nghĩa cấu trúc dữ liệu kết quả phân tích TỔNG QUAN
class LogAnalysisSummary(BaseModel):
    total_lines: int
    date_lines: int # Số dòng chỉ chứa ngày tháng
    detail_lines: int # Số dòng log chi tiết
    summary_lines: int # Số dòng tổng kết cuối ngày
    error_count: int
    access_denied_count: int
    optimistic_lock_count: int
    duplicate_entry_count: int
    null_or_transient_value_error_count: int
    tang_not_found_count: int
    invalid_format_count: int
    invalid_column_count: int
    multiple_bag_fetch_error_count: int
    jwt_error_count: int
    other_errors_count: int # Đếm các lỗi khác không thuộc danh mục trên
    users_active: List[str] # Danh sách các user xuất hiện trong log
    actions_summary: Dict[str, Dict[str, int]] # Tổng kết số lần mỗi action thành công/thất bại
    errors_by_type: Dict[str, int] # Tổng kết số lỗi theo loại
    repeated_action_warnings: List[str] = Field(default_factory=list) # <-- Thêm trường này cho cảnh báo lặp hành động
    summary_message: str # Tóm tắt chung
    # extracted_errors: List[str] # Tùy chọn: trích xuất các dòng lỗi

# Định nghĩa cấu trúc dữ liệu kết quả phân tích CHI TIẾT (ví dụ các dòng log được parse)
# Có thể trả về cả summary và details
class FullLogAnalysisResult(BaseModel):
    summary: LogAnalysisSummary # Trường này chứa summary
    parsed_entries: List[LogEntryDetail] # Trường này chứa danh sách các entry chi tiết

@app.post("/api/fastapi/analyze-log")
async def analyze_log_file(file: UploadFile = File(...)):
    """
    Nhận file log từ Spring Boot, phân tích và trả về kết quả.
    Thêm logic phát hiện hành động lặp lại nhanh chóng.
    """
    try:
        contents = await file.read()
        # Thêm encoding='utf-8' để đảm bảo decode đúng
        log_content = contents.decode(encoding='utf-8', errors='ignore') # errors='ignore' để tránh lỗi nếu có ký tự không hợp lệ

        lines = log_content.splitlines()
        total_lines = len(lines)

        # Reset counters and lists for each request
        date_lines = 0
        detail_lines = 0
        summary_lines = 0

        # Initialize error counters and summaries (these will be finalized after parsing)
        # The individual error counters are mainly for matching the DTO structure
        access_denied_count = 0
        optimistic_lock_count = 0
        duplicate_entry_count = 0
        null_or_transient_value_error_count = 0
        tang_not_found_count = 0
        invalid_format_count = 0
        invalid_column_count = 0
        multiple_bag_fetch_error_count = 0
        jwt_error_count = 0
        other_errors_count = 0

        active_users = set()
        actions_summary = defaultdict(lambda: defaultdict(int)) # { action: { status: count } }

        parsed_entries = [] # To store parsed log entries

        # --- Data structure for repeated action detection ---
        # Key: (user, action), Value: List of datetime objects (timestamps)
        user_action_timestamps: Dict[Tuple[str, str], List[datetime]] = defaultdict(list)
        repeated_action_warnings: List[str] = []
        current_date: datetime.date = None # Keep track of the current date from the log
        # ---------------------------------------------------


        # Regex patterns
        date_pattern = re.compile(r'^\d{4}-\d{2}-\d{2}$')
        general_detail_pattern = re.compile(r'^(\d{2}:\d{2}:\d{2}) - User: (\S+) - (.*?)$')
        summary_pattern = re.compile(r'^Log summary for \d{4}-\d{2}-\d{2}: (\d+) log entries.$')


        # Use enumerate to get line number for warnings/errors
        for i, line in enumerate(lines):
            line = line.strip()
            if not line:
                continue

            # Check for date line first to update current_date for time analysis
            date_match = date_pattern.match(line)
            if date_match:
                date_lines += 1
                try:
                    # Parse the date string into a date object
                    current_date = datetime.strptime(date_match.group(0), '%Y-%m-%d').date()
                except ValueError:
                    print(f"Warning: Line {i+1}: Could not parse date from '{line}'")
                    current_date = None # Reset if parsing fails, affects subsequent time parsing
                continue # Skip to next line after finding a date

            # Check for summary line
            if summary_pattern.match(line):
                summary_lines += 1
                continue # Skip to next line after finding summary


            # Process potential detail lines
            match = general_detail_pattern.match(line)
            if match:
                detail_lines += 1 # Count as detail line if it matches the pattern
                time_str, user, rest_of_line = match.groups()
                active_users.add(user)

                # --- Parse timestamp into datetime object for time-based analysis ---
                entry_datetime: datetime = None
                if current_date:
                    try:
                        # Combine the current date with the time string
                        entry_time = datetime.strptime(time_str, '%H:%M:%S').time()
                        entry_datetime = datetime.combine(current_date, entry_time)
                    except ValueError:
                        print(f"Warning: Line {i+1}: Could not parse time '{time_str}' from '{line}'")
                        # entry_datetime remains None, cannot use for time analysis

                # --- Initialize parsed entry details before specific parsing ---
                action = "unrecognized_action"
                status = "unrecognized_status"
                details: Dict[str, Any] = {}
                # specific_error_type = None # Use this internally during parsing if helpful


                # --- Detailed Parsing Logic based on rest_of_line ---
                # Extract action, status, and populate details dictionary based on the log content
                # Make sure 'status' is set to "Error" consistently when an error condition is met.

                if "isUserLoggedIn:" in rest_of_line:
                    action = "isUserLoggedIn"
                    status_match = re.search(r'isUserLoggedIn: (true|false)', rest_of_line)
                    status = status_match.group(1) if status_match else "parse_error_status"

                elif "layDSPhongMay" in rest_of_line:
                    action = "layDSPhongMay"
                    if "Success" in rest_of_line:
                        status = "Success"
                        size_match = re.search(r'Result size: (\d+)', rest_of_line)
                        if size_match: details['resultSize'] = int(size_match.group(1))
                    elif "User not logged in" in rest_of_line:
                        status = "Error" # Set status to Error for consistency
                        details['errorMessage'] = "User not logged in."
                    else:
                        status = "Error" # Assume other cases are errors
                        details['errorMessage'] = rest_of_line.strip() # Capture the raw line or part of it

                elif "layPhongMayTheoMa" in rest_of_line:
                    action = "layPhongMayTheoMa"
                    ma_match_initial = re.search(r'maPhong: (\d+)', rest_of_line)
                    if ma_match_initial: details['maPhong'] = int(ma_match_initial.group(1))
                    if "Success" in rest_of_line:
                        status = "Success"
                    elif "User not logged in" in rest_of_line:
                        status = "Error" # Set status to Error
                        details['errorMessage'] = "User not logged in."
                    else:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()

                elif "layDSMayTinhTheoPhong" in rest_of_line:
                    action = "layDSMayTinhTheoPhong"
                    if "Success" in rest_of_line:
                        status = "Success"
                        ma_comp_match = re.search(r'maPhong: (\d+), Found (\d+) computers.', rest_of_line)
                        if ma_comp_match:
                            details['maPhong'] = int(ma_comp_match.group(1))
                            details['foundComputers'] = int(ma_comp_match.group(2))
                    elif "User not logged in" in rest_of_line:
                        status = "Error" # Set status to Error
                        details['errorMessage'] = "User not logged in."
                        ma_match_denied = re.search(r'Access denied for maPhong: (\d+)', rest_of_line)
                        if ma_match_denied: details['maPhong'] = int(ma_match_denied.group(1))
                    else:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()


                elif "layDanhSachPhongMayVaThongKe" in rest_of_line:
                    action = "layDanhSachPhongMayVaThongKe"
                    if "Success" in rest_of_line:
                        status = "Success"
                        size_match = re.search(r'Result size: (\d+)', rest_of_line)
                        if size_match: details['resultSize'] = int(size_match.group(1))
                        time_match = re.search(r'Thời gian thực thi: (\d+)ms', rest_of_line)
                        if time_match: details['executionTimeMs'] = int(time_match.group(1))
                    elif "Error:" in rest_of_line:
                        status = "Error" # Explicitly Error
                        details['errorMessage'] = rest_of_line.strip()
                    else:
                        status = "unrecognized_status" # If it has the action name but no Success/Error pattern

                elif "timKiemPhongMay" in rest_of_line:
                    action = "timKiemPhongMay"
                    if "Success" in rest_of_line:
                        status = "Success"
                        keyword_match = re.search(r'keyword: (.*?), Result size: (\d+)', rest_of_line)
                        if keyword_match:
                            details['keyword'] = keyword_match.group(1).strip()
                            details['resultSize'] = int(keyword_match.group(2))
                    elif "Invalid number format" in rest_of_line:
                        status = "Error" # Set status to Error
                        format_match = re.search(r"Invalid number format for column '(\S+)'. value: (.*)", rest_of_line)
                        if format_match:
                            details['column'] = format_match.group(1)
                            details['value'] = format_match.group(2).strip()
                        else:
                            details['errorMessage'] = rest_of_line.strip() # Capture if regex fails
                    elif "Invalid column name" in rest_of_line:
                        status = "Error" # Set status to Error
                        column_match = re.search(r"Invalid column name. column: (\S+)", rest_of_line)
                        if column_match: details['column'] = column_match.group(1)
                        else:
                            details['errorMessage'] = rest_of_line.strip() # Capture if regex fails
                    else:
                        status = "Error" # Assume other cases are errors
                        details['errorMessage'] = rest_of_line.strip()

                elif "xoa" in rest_of_line: # Covers xoaPhongMay, xoaMayTinh
                    action = "xoa" # General action type 'xoa'
                    ma_match = re.search(r'maPhong: (\d+)', rest_of_line) # Example: can be maPhong or maMayTinh
                    if ma_match: details['id'] = int(ma_match.group(1)) # Use a general 'id' key
                    if "Success" in rest_of_line:
                        status = "Success"
                    elif "Error:" in rest_of_line:
                        status = "Error" # Set status to Error
                        error_message_match = re.search(r'Error: (.*?)(?:, Request Token:.*)?$', rest_of_line)
                        if error_message_match: details['errorMessage'] = error_message_match.group(1).strip()
                        else: details['errorMessage'] = rest_of_line.strip()
                    else:
                        status = "unrecognized_status"

                elif "luu" in rest_of_line: # Covers luuPhongMay, luuMayTinh
                    action = "luu" # General action type 'luu'
                    if "Success" in rest_of_line:
                        status = "Success"
                        ma_match = re.search(r'Room saved: (\d+)', rest_of_line) # Example: specific to Room
                        if ma_match: details['maSaved'] = int(ma_match.group(1)) # Use a general 'maSaved' key
                    elif "Error saving" in rest_of_line: # Covers "Error saving room:", "Error saving computer:"
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip() # Capture the full error message
                    else:
                        status = "unrecognized_status"

                elif "capNhatTheoMa" in rest_of_line: # Covers capNhatPhongMayTheoMa, capNhatMayTinhTheoMa
                    action = "capNhatTheoMa" # General action type 'capNhatTheoMa'
                    ma_match_initial = re.search(r'maPhong: (\d+)', rest_of_line) # Example: can be maPhong or maMayTinh
                    if ma_match_initial: details['id'] = int(ma_match_initial.group(1)) # Use a general 'id' key

                    if "Success" in rest_of_line:
                        status = "Success"
                    elif "Optimistic Lock Exception" in rest_of_line:
                        status = "Error" # Set status to Error
                        details['errorMessage'] = "Optimistic Lock Exception" # Simplified message, details has raw
                    elif "Tang not found" in rest_of_line:
                        status = "Error" # Set status to Error
                        details['errorMessage'] = "Tang not found"
                        tang_match = re.search(r"maTang: (\d+)", rest_of_line)
                        if tang_match: details['maTang'] = int(tang_match.group(1))
                    elif "Error:" in rest_of_line:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip() # Capture full error
                    else:
                        status = "unrecognized_status"

                elif "thongKeMayTinhTheoThoiGian" in rest_of_line:
                    action = "thongKeMayTinhTheoThoiGian"
                    if "Statistics generated for" in rest_of_line:
                        status = "Success"
                        days_match = re.search(r'Statistics generated for (\d+) days.', rest_of_line)
                        if days_match: details['daysGenerated'] = int(days_match.group(1))
                    else:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()

                # Add other actions here...
                # Add a general catch-all for unparsed errors
                elif "Error:" in rest_of_line:
                    status = "Error"
                    details['errorMessage'] = rest_of_line.strip()


                # --- End Detailed Parsing Logic ---

                # Count action/status regardless of recognition status
                actions_summary[action][status] += 1

                # Append the parsed entry
                parsed_entry = LogEntryDetail(time=time_str, user=user, action=action, status=status, details=details)
                parsed_entries.append(parsed_entry)


                # --- Repeated Action Detection Logic ---
                # This logic needs the parsed 'action' from the step above
                if entry_datetime: # Only perform analysis if we have a valid datetime
                    user_action_key = (user, action) # Use the parsed action here
                    timestamps_list = user_action_timestamps[user_action_key]

                    # Add the current timestamp
                    timestamps_list.append(entry_datetime)

                    # Define the start of the time window
                    window_start_time = entry_datetime - timedelta(seconds=TIME_WINDOW_SECONDS)

                    # Filter out timestamps older than the window
                    # Using slicing assignment `[:]` to modify the list in place
                    timestamps_list[:] = [ts for ts in timestamps_list if ts >= window_start_time]

                    # Check if the number of timestamps in the window exceeds the threshold
                    # Only trigger if the threshold is newly crossed (count just became > ACTION_THRESHOLD)
                    # Or if it continues to be above threshold but with new timestamps added
                    if len(timestamps_list) > ACTION_THRESHOLD:
                        warning_message = (
                            f"WARNING: User '{user}' performed action '{action}' "
                            f"{len(timestamps_list)} times within a {TIME_WINDOW_SECONDS} second window "
                            f"(around {entry_datetime.strftime('%Y-%m-%d %H:%M:%S')})."
                        )
                        # Simple check to avoid identical consecutive warnings
                        if not repeated_action_warnings or repeated_action_warnings[-1] != warning_message:
                            repeated_action_warnings.append(warning_message)

                # --- End Repeated Action Detection Logic ---


        # --- Recalculate Summary Counts based on parsed_entries ---
        # This approach is more reliable as counts are derived from the final parsed data.
        final_error_count = 0
        final_access_denied_count = 0
        final_optimistic_lock_count = 0
        final_duplicate_entry_count = 0
        final_null_or_transient_value_error_count = 0
        final_tang_not_found_count = 0
        final_invalid_format_count = 0
        final_invalid_column_count = 0
        final_multiple_bag_fetch_error_count = 0
        final_jwt_error_count = 0
        final_other_errors_count = 0

        final_errors_by_type = defaultdict(int) # Detailed error counts by type string

        for entry in parsed_entries:
            # Only count entries where status indicates an error or a specific error type was identified
            # If your parsing logic sets status="Error" for all errors, checking status is sufficient.
            # If you sometimes set status to something else like "User not logged in." but want to count it as error,
            # you need to include those statuses here or rely solely on error_type being set.
            # Assuming status == "Error" is the primary indicator for total_error_count
            if entry.status == "Error" or entry.status == "User not logged in.": # Include "User not logged in." in total error count
                final_error_count += 1

                # Determine error_type based on details['errorMessage'] or action/status string
                current_error_type = "Other Unclassified Error" # Default type for errors not specifically matched

                error_message = str(entry.details.get('errorMessage', '')).lower()
                action_status_check = f"{entry.action.lower()} - {entry.status.lower()} - {error_message}" # Check action, status, and message


                # Prioritize specific checks based on common log patterns or error messages
                if "access denied" in action_status_check or "user not logged in" in action_status_check:
                    current_error_type = "Access Denied"
                elif "optimistic lock exception" in error_message:
                    current_error_type = "Optimistic Lock"
                elif "duplicate entry" in error_message:
                    current_error_type = "Duplicate Entry"
                elif "null or transient value" in error_message:
                    current_error_type = "Null or Transient Value Error"
                # Check tang not found - based on action and specific message/details?
                # Assuming "Tang not found" might be in the raw log line or error message for certain actions
                elif entry.action in ["layTangTheoMa", "capNhatTheoMa"] and ("tang not found" in action_status_check or "khong tim thay tang" in action_status_check):
                    current_error_type = "Tang Not Found"
                    if 'maTang' in entry.details: current_error_type += f" (maTang: {entry.details['maTang']})"
                elif "invalid number format" in action_status_check:
                    current_error_type = "Invalid Format"
                elif "invalid column name" in action_status_check:
                    current_error_type = "Invalid Column"
                elif "multiplebagfetchexception" in error_message:
                    current_error_type = "Multiple Bag Fetch"
                elif "jwt signature does not match" in error_message or "error getting username from token" in error_message:
                    current_error_type = "JWT Error"
                # Add more specific error checks based on keywords in `error_message` or `action_status_check`


                final_errors_by_type[current_error_type] += 1

                # Update individual counters based on identified type
                if current_error_type == "Access Denied": final_access_denied_count += 1
                # Use startswith if the type name might have details added like "(maTang: X)"
                elif current_error_type.startswith("Optimistic Lock"): final_optimistic_lock_count += 1
                elif current_error_type.startswith("Duplicate Entry"): final_duplicate_entry_count += 1
                elif current_error_type.startswith("Null or Transient Value Error"): final_null_or_transient_value_error_count += 1
                elif current_error_type.startswith("Tang Not Found"): final_tang_not_found_count += 1
                elif current_error_type.startswith("Invalid Format"): final_invalid_format_count += 1
                elif current_error_type.startswith("Invalid Column"): final_invalid_column_count += 1
                elif current_error_type.startswith("Multiple Bag Fetch"): final_multiple_bag_fetch_error_count += 1
                elif current_error_type.startswith("JWT Error"): final_jwt_error_count += 1
                # 'Other Unclassified Error' is implicitly handled by final_errors_by_type


        # Recalculate other_errors_count: the count for "Other Unclassified Error"
        final_other_errors_count = final_errors_by_type.get("Other Unclassified Error", 0)


        # --- Generate Detailed Summary Message ---
        summary_message_parts = []

        summary_message_parts.append(f"Log Analysis Report for {file.filename}\n")

        # 1. Overview
        summary_message_parts.append("--- Overview ---")
        summary_message_parts.append(f"- Total lines processed: {total_lines}")
        summary_message_parts.append(f"- Successfully parsed detail entries: {detail_lines}")
        summary_message_parts.append(f"- Lines matching date pattern: {date_lines}")
        summary_message_parts.append(f"- Lines matching summary pattern: {summary_lines}\n")


        # 2. Error Summary (Using recalculated counts)
        summary_message_parts.append("--- Error Summary ---")
        summary_message_parts.append(f"A total of {final_error_count} errors were detected in the parsed entries.")
        if final_error_count > 0:
            summary_message_parts.append("Breakdown by error type:")
            # Sort errors by count descending, then alphabetically
            sorted_errors = sorted(final_errors_by_type.items(), key=lambda item: (-item[1], item[0]))
            for err_type, count in sorted_errors:
                summary_message_parts.append(f"  - {err_type}: {count} times")
        else:
            summary_message_parts.append("No specific errors were identified in the parsed entries.\n")


        # 3. Repeated Action Warnings <-- NEW SECTION
        summary_message_parts.append("\n--- Repeated Action Warnings ---")
        if repeated_action_warnings:
            summary_message_parts.append(f"Detected {len(repeated_action_warnings)} potential rapid repeated action sequences:")
            for warning in repeated_action_warnings:
                summary_message_parts.append(f"- {warning}")
        else:
            summary_message_parts.append("No rapid repeated action sequences detected based on configured thresholds.")
        summary_message_parts.append("\n") # Add newline after this section


        # 4. Action Performance Summary (Renumbered from 3)
        summary_message_parts.append("\n--- Action Performance Summary ---")
        if actions_summary:
            # Sort actions alphabetically
            sorted_actions = sorted(actions_summary.keys())
            for action in sorted_actions:
                statuses = actions_summary[action]
                summary_message_parts.append(f"- Action: {action}")
                # Sort statuses to prioritize Error/unrecognized
                sorted_statuses = sorted(statuses.keys(), key=lambda s: (s != "Error" and s != "User not logged in.", s)) # Prioritize Error and User not logged in.
                for status in sorted_statuses:
                    count = statuses[status]
                    summary_message_parts.append(f"  - {status}: {count} times")
        else:
            summary_message_parts.append("No actions were parsed from the log entries.\n")


        # 5. User Activity Summary (Renumbered from 4)
        summary_message_parts.append("\n--- User Activity ---")
        num_active_users = len(active_users)
        summary_message_parts.append(f"{num_active_users} unique user(s) logged activity.")
        if num_active_users > 0:
            user_list = sorted(list(active_users))
            if num_active_users > 20: # Example limit
                summary_message_parts.append(f"  List (first 20): {', '.join(user_list[:20])} ...")
            else:
                summary_message_parts.append(f"  List: {', '.join(user_list)}")
        summary_message_parts.append("\n")


        # Join all parts into the final summary message
        summary_message = "\n".join(summary_message_parts)


        # --- Prepare Final Result ---
        summary_result = LogAnalysisSummary(
            total_lines=total_lines,
            date_lines=date_lines,
            detail_lines=detail_lines,
            summary_lines=summary_lines,
            error_count=final_error_count, # Use final counts
            access_denied_count=final_access_denied_count,
            optimistic_lock_count=final_optimistic_lock_count,
            duplicate_entry_count=final_duplicate_entry_count,
            null_or_transient_value_error_count=final_null_or_transient_value_error_count,
            tang_not_found_count=final_tang_not_found_count,
            invalid_format_count=final_invalid_format_count,
            invalid_column_count=final_invalid_column_count,
            multiple_bag_fetch_error_count=final_multiple_bag_fetch_error_count,
            jwt_error_count=final_jwt_error_count,
            other_errors_count=final_other_errors_count, # Use final count
            users_active=sorted(list(active_users)),
            actions_summary=dict(actions_summary),
            errors_by_type=dict(final_errors_by_type), # Use final counts
            repeated_action_warnings=repeated_action_warnings, # Include the generated warnings list
            summary_message=summary_message # Use the newly generated detailed message
        )

        # Return the full result object matching FullLogAnalysisResult Pydantic model
        return FullLogAnalysisResult(summary=summary_result, parsed_entries=parsed_entries)


    except Exception as e:
        print(f"Error processing file in FastAPI: {e}")
        traceback.print_exc()
        error_detail = f"Internal server error during log analysis: {e}"
        # In a real app, you might want to return less detail in production
        raise HTTPException(status_code=500, detail=error_detail)

# To run (ensure you have installed the necessary libraries):
# pip install fastapi uvicorn python-multipart pydantic
# uvicorn main:app --reload --port 8000