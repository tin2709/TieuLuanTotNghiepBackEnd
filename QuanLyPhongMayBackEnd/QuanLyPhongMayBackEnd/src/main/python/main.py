# src/main/python/combined_app.py

from fastapi import FastAPI, File, UploadFile, HTTPException, Body
from pydantic import BaseModel, Field, ConfigDict
from typing import List, Dict, Any, Tuple, Optional

import io
import re
from collections import defaultdict
import traceback
from datetime import datetime, timedelta

from googletrans import Translator # Using unofficial Google Translate API


app = FastAPI()

# --- Configuration for Log Analysis Repeated Action Detection ---
TIME_WINDOW_SECONDS = 60
ACTION_THRESHOLD = 10
# --------------------------------------------------------------

# --- Pydantic Models for Log Analysis ---
class LogEntryDetail(BaseModel):
    time: str
    user: str
    action: str
    status: str
    details: Dict[str, Any] = Field(default_factory=dict)

class LogAnalysisSummary(BaseModel):
    total_lines: int
    date_lines: int
    detail_lines: int
    summary_lines: int
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
    other_errors_count: int
    users_active: List[str]
    actions_summary: Dict[str, Dict[str, int]]
    errors_by_type: Dict[str, int]
    repeated_action_warnings: List[str] = Field(default_factory=list)
    summary_message: str

class FullLogAnalysisResult(BaseModel):
    summary: LogAnalysisSummary
    parsed_entries: List[LogEntryDetail]
# --- End Pydantic Models for Log Analysis ---


# --- Pydantic Model for Translation Request ---
class TranslationRequest(BaseModel):
    target_language: str = Field(alias="targetLanguage")
    text_content: Dict[str, Any] = Field(alias="textContent")
    source_language: Optional[str] = Field(None, alias="sourceLanguage")

    model_config = ConfigDict(populate_by_name=True, arbitrary_types_allowed=True)
# --- End Pydantic Model for Translation Request ---


# --- Global Translator Instance ---
# Instantiate the translator
translator = Translator()
# --- End Global Translator Instance ---


# --- Recursive Translation Function ---
def recursive_translate_strings(
        data: Any,
        target_lang: str,
        source_lang: Optional[str] = None
) -> Any:
    """
    Recursively translates string values within a nested dictionary or list.
    Non-string values and structure are preserved.
    Handles potential None results from translation more safely.
    """
    if isinstance(data, str):
        # Translate the string
        try:
            if not data.strip():
                return data # Return empty/whitespace string as is

            # Use the global translator instance
            # src='auto' is the default if source_lang is None
            translated = translator.translate(data, dest=target_lang, src=source_lang)

            # --- IMPROVED CHECK FOR TRANSLATION RESULT ---
            # Check if the translation result object is valid and has the 'text' attribute
            if translated is None or not hasattr(translated, 'text'):
                print(f"Warning: Translation failed or returned unexpected None/missing text for: '{data}'. Result: {translated}")
                return data # Return original string on failure

            # Now it's safe to access translated.text
            translated_text = translated.text

            # Safely get the 'src' attribute and check if it's a non-None string before using lower()
            detected_src = getattr(translated, 'src', None)
            if detected_src is not None and isinstance(detected_src, str) and detected_src.lower() == target_lang.lower():
                print(f"Info: Detected source '{detected_src}' matches target '{target_lang}' for: '{data}' (May be already translated or silent failure)")

            return translated_text # Return the translated text
            # --- END IMPROVED CHECK ---


        except Exception as e:
            # Catch any other errors during translation (e.g., network, rate limit)
            # This will now catch errors not related to accessing .text or .src.lower()
            print(f"Error translating string '{data}': {e}")
            # Return original data on error
            return data

    elif isinstance(data, dict):
        return {k: recursive_translate_strings(v, target_lang, source_lang) for k, v in data.items()}

    elif isinstance(data, list):
        return [recursive_translate_strings(item, target_lang, source_lang) for item in data]

    else:
        # Return non-string, non-dict, non-list types unchanged (numbers, booleans, None)
        return data
# --- End Recursive Translation Function ---


# --- Log Analysis Endpoint ---
@app.post("/api/fastapi/analyze-log")
async def analyze_log_file(file: UploadFile = File(...)):
    """
    Nhận file log từ Spring Boot, phân tích và trả về kết quả.
    Thêm logic phát hiện hành động lặp lại nhanh chóng.
    """
    try:
        contents = await file.read()
        log_content = contents.decode(encoding='utf-8', errors='ignore')

        lines = log_content.splitlines()
        total_lines = len(lines)

        date_lines = 0
        detail_lines = 0
        summary_lines = 0

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
        actions_summary = defaultdict(lambda: defaultdict(int))

        parsed_entries = []

        user_action_timestamps: Dict[Tuple[str, str], List[datetime]] = defaultdict(list)
        repeated_action_warnings: List[str] = []
        current_date: datetime.date = None

        date_pattern = re.compile(r'^\d{4}-\d{2}-\d{2}$')
        general_detail_pattern = re.compile(r'^(\d{2}:\d{2}:\d{2}) - User: (\S+) - (.*?)$')
        summary_pattern = re.compile(r'^Log summary for \d{4}-\d{2}-\d{2}: (\d+) log entries.$')


        for i, line in enumerate(lines):
            line = line.strip()
            if not line:
                continue

            date_match = date_pattern.match(line)
            if date_match:
                date_lines += 1
                try:
                    current_date = datetime.strptime(date_match.group(0), '%Y-%m-%d').date()
                except ValueError:
                    print(f"Warning: Line {i+1}: Could not parse date from '{line}'")
                    current_date = None
                continue

            if summary_pattern.match(line):
                summary_lines += 1
                continue

            match = general_detail_pattern.match(line)
            if match:
                detail_lines += 1
                time_str, user, rest_of_line = match.groups()
                active_users.add(user)

                entry_datetime: datetime = None
                if current_date:
                    try:
                        entry_time = datetime.strptime(time_str, '%H:%M:%S').time()
                        entry_datetime = datetime.combine(current_date, entry_time)
                    except ValueError:
                        print(f"Warning: Line {i+1}: Could not parse time '{time_str}' from '{line}'")

                action = "unrecognized_action"
                status = "unrecognized_status"
                details: Dict[str, Any] = {}

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
                        status = "Error"
                        details['errorMessage'] = "User not logged in."
                    else:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()

                elif "layPhongMayTheoMa" in rest_of_line:
                    action = "layPhongMayTheoMa"
                    ma_match_initial = re.search(r'maPhong: (\d+)', rest_of_line)
                    if ma_match_initial: details['maPhong'] = int(ma_match_initial.group(1))
                    if "Success" in rest_of_line:
                        status = "Success"
                    elif "User not logged in" in rest_of_line:
                        status = "Error"
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
                        status = "Error"
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
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()
                    else:
                        status = "unrecognized_status"

                elif "timKiemPhongMay" in rest_of_line:
                    action = "timKiemPhongMay"
                    if "Success" in rest_of_line:
                        status = "Success"
                        keyword_match = re.search(r'keyword: (.*?), Result size: (\d+)', rest_of_line)
                        if keyword_match:
                            details['keyword'] = keyword_match.group(1).strip()
                            details['resultSize'] = int(keyword_match.group(2))
                    elif "Invalid number format" in rest_of_line:
                        status = "Error"
                        format_match = re.search(r"Invalid number format for column '(\S+)'. value: (.*)", rest_of_line)
                        if format_match:
                            details['column'] = format_match.group(1)
                            details['value'] = format_match.group(2).strip()
                        else:
                            details['errorMessage'] = rest_of_line.strip()
                    elif "Invalid column name" in rest_of_line:
                        status = "Error"
                        column_match = re.search(r"Invalid column name. column: (\S+)", rest_of_line)
                        if column_match: details['column'] = column_match.group(1)
                        else:
                            details['errorMessage'] = rest_of_line.strip()
                    else:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()

                elif "xoa" in rest_of_line:
                    action = "xoa"
                    ma_match = re.search(r'maPhong: (\d+)', rest_of_line)
                    if ma_match: details['id'] = int(ma_match.group(1))
                    if "Success" in rest_of_line:
                        status = "Success"
                    elif "Error:" in rest_of_line:
                        status = "Error"
                        error_message_match = re.search(r'Error: (.*?)(?:, Request Token:.*)?$', rest_of_line)
                        if error_message_match: details['errorMessage'] = error_message_match.group(1).strip()
                        else: details['errorMessage'] = rest_of_line.strip()
                    else:
                        status = "unrecognized_status"

                elif "luu" in rest_of_line:
                    action = "luu"
                    if "Success" in rest_of_line:
                        status = "Success"
                        ma_match = re.search(r'Room saved: (\d+)', rest_of_line)
                        if ma_match: details['maSaved'] = int(ma_match.group(1))
                    elif "Error saving" in rest_of_line:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()
                    else:
                        status = "unrecognized_status"

                elif "capNhatTheoMa" in rest_of_line:
                    action = "capNhatTheoMa"
                    ma_match_initial = re.search(r'maPhong: (\d+)', rest_of_line)
                    if ma_match_initial: details['id'] = int(ma_match_initial.group(1))

                    if "Success" in rest_of_line:
                        status = "Success"
                    elif "Optimistic Lock Exception" in rest_of_line:
                        status = "Error"
                        details['errorMessage'] = "Optimistic Lock Exception"
                    elif "Tang not found" in rest_of_line:
                        status = "Error"
                        details['errorMessage'] = "Tang not found"
                        tang_match = re.search(r"maTang: (\d+)", rest_of_line)
                        if tang_match: details['maTang'] = int(tang_match.group(1))
                    elif "Error:" in rest_of_line:
                        status = "Error"
                        details['errorMessage'] = rest_of_line.strip()
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

                elif "Error:" in rest_of_line:
                    status = "Error"
                    details['errorMessage'] = rest_of_line.strip()

                actions_summary[action][status] += 1

                parsed_entry = LogEntryDetail(time=time_str, user=user, action=action, status=status, details=details)
                parsed_entries.append(parsed_entry)

                if entry_datetime:
                    user_action_key = (user, action)
                    timestamps_list = user_action_timestamps[user_action_key]
                    timestamps_list.append(entry_datetime)
                    window_start_time = entry_datetime - timedelta(seconds=TIME_WINDOW_SECONDS)
                    timestamps_list[:] = [ts for ts in timestamps_list if ts >= window_start_time]

                    if len(timestamps_list) > ACTION_THRESHOLD:
                        warning_message = (
                            f"WARNING: User '{user}' performed action '{action}' "
                            f"{len(timestamps_list)} times within a {TIME_WINDOW_SECONDS} second window "
                            f"(around {entry_datetime.strftime('%Y-%m-%d %H:%M:%S')})."
                        )
                        if not repeated_action_warnings or repeated_action_warnings[-1] != warning_message:
                            repeated_action_warnings.append(warning_message)


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

        final_errors_by_type = defaultdict(int)

        for entry in parsed_entries:
            if entry.status == "Error" or entry.status == "User not logged in.":
                final_error_count += 1

                current_error_type = "Other Unclassified Error"

                error_message = str(entry.details.get('errorMessage', '')).lower()
                action_status_check = f"{entry.action.lower()} - {entry.status.lower()} - {error_message}"


                if "access denied" in action_status_check or "user not logged in" in action_status_check:
                    current_error_type = "Access Denied"
                elif "optimistic lock exception" in error_message:
                    current_error_type = "Optimistic Lock"
                elif "duplicate entry" in error_message:
                    current_error_type = "Duplicate Entry"
                elif "null or transient value" in error_message:
                    current_error_type = "Null or Transient Value Error"
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


                final_errors_by_type[current_error_type] += 1

                if current_error_type == "Access Denied": final_access_denied_count += 1
                elif current_error_type.startswith("Optimistic Lock"): final_optimistic_lock_count += 1
                elif current_error_type.startswith("Duplicate Entry"): final_duplicate_entry_count += 1
                elif current_error_type.startswith("Null or Transient Value Error"): final_null_or_transient_value_error_count += 1
                elif current_error_type.startswith("Tang Not Found"): final_tang_not_found_count += 1
                elif current_error_type.startswith("Invalid Format"): final_invalid_format_count += 1
                elif current_error_type.startswith("Invalid Column"): final_invalid_column_count += 1
                elif current_error_type.startswith("Multiple Bag Fetch"): final_multiple_bag_fetch_error_count += 1
                elif current_error_type.startswith("JWT Error"): final_jwt_error_count += 1


        final_other_errors_count = final_errors_by_type.get("Other Unclassified Error", 0)

        summary_message_parts = []

        summary_message_parts.append(f"Log Analysis Report for {file.filename}\n")

        summary_message_parts.append("--- Overview ---")
        summary_message_parts.append(f"- Total lines processed: {total_lines}")
        summary_message_parts.append(f"- Successfully parsed detail entries: {detail_lines}")
        summary_message_parts.append(f"- Lines matching date pattern: {date_lines}")
        summary_message_parts.append(f"- Lines matching summary pattern: {summary_lines}\n")

        summary_message_parts.append("--- Error Summary ---")
        summary_message_parts.append(f"A total of {final_error_count} errors were detected in the parsed entries.")
        if final_error_count > 0:
            summary_message_parts.append("Breakdown by error type:")
            sorted_errors = sorted(final_errors_by_type.items(), key=lambda item: (-item[1], item[0]))
            for err_type, count in sorted_errors:
                summary_message_parts.append(f"  - {err_type}: {count} times")
        else:
            summary_message_parts.append("No specific errors were identified in the parsed entries.\n")

        summary_message_parts.append("\n--- Repeated Action Warnings ---")
        if repeated_action_warnings:
            summary_message_parts.append(f"Detected {len(repeated_action_warnings)} potential rapid repeated action sequences:")
            for warning in repeated_action_warnings:
                summary_message_parts.append(f"- {warning}")
        else:
            summary_message_parts.append("No rapid repeated action sequences detected based on configured thresholds.")
        summary_message_parts.append("\n")

        summary_message_parts.append("\n--- Action Performance Summary ---")
        if actions_summary:
            sorted_actions = sorted(actions_summary.keys())
            for action in sorted_actions:
                statuses = actions_summary[action]
                summary_message_parts.append(f"- Action: {action}")
                sorted_statuses = sorted(statuses.keys(), key=lambda s: (s != "Error" and s != "User not logged in.", s))
                for status in sorted_statuses:
                    count = statuses[status]
                    summary_message_parts.append(f"  - {status}: {count} times")
        else:
            summary_message_parts.append("No actions were parsed from the log entries.\n")

        summary_message_parts.append("\n--- User Activity ---")
        num_active_users = len(active_users)
        summary_message_parts.append(f"{num_active_users} unique user(s) logged activity.")
        if num_active_users > 0:
            user_list = sorted(list(active_users))
            if num_active_users > 20:
                summary_message_parts.append(f"  List (first 20): {', '.join(user_list[:20])} ...")
            else:
                summary_message_parts.append(f"  List: {', '.join(user_list)}")
        summary_message_parts.append("\n")

        summary_message = "\n".join(summary_message_parts)

        summary_result = LogAnalysisSummary(
            total_lines=total_lines,
            date_lines=date_lines,
            detail_lines=detail_lines,
            summary_lines=summary_lines,
            error_count=final_error_count,
            access_denied_count=final_access_denied_count,
            optimistic_lock_count=final_optimistic_lock_count,
            duplicate_entry_count=final_duplicate_entry_count,
            null_or_transient_value_error_count=final_null_or_transient_value_error_count,
            tang_not_found_count=final_tang_not_found_count,
            invalid_format_count=final_invalid_format_count,
            invalid_column_count=final_invalid_column_count,
            multiple_bag_fetch_error_count=final_multiple_bag_fetch_error_count,
            jwt_error_count=final_jwt_error_count,
            other_errors_count=final_other_errors_count,
            users_active=sorted(list(active_users)),
            actions_summary=dict(actions_summary),
            errors_by_type=dict(final_errors_by_type),
            repeated_action_warnings=repeated_action_warnings,
            summary_message=summary_message
        )

        return FullLogAnalysisResult(summary=summary_result, parsed_entries=parsed_entries)


    except Exception as e:
        print(f"Error processing file in FastAPI: {e}")
        traceback.print_exc()
        error_detail = f"Internal server error during log analysis: {e}"
        raise HTTPException(status_code=500, detail=error_detail)


# --- Translation Endpoint ---
@app.post("/translate-json")
async def translate_json_endpoint(request: TranslationRequest = Body(...)):
    """
    Receives a JSON structure, translates string values within textContent
    to the targetLanguage, and returns the translated structure.
    """
    try:
        print(f"Received translation request to: {request.target_language}")

        translated_content = recursive_translate_strings(
            request.text_content,
            target_lang=request.target_language,
            source_lang=request.source_language
        )

        return translated_content

    except HTTPException:
        raise
    except Exception as e:
        print(f"An unexpected error occurred during translation: {e}")
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Internal server error during translation: {e}")
# --- End Translation Endpoint ---


# To run (ensure you have installed the necessary libraries from requirements.txt):
# uvicorn combined_app:app --reload --port 8000