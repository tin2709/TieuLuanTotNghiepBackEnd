package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.UserPermission;
import com.example.QuanLyPhongMayBackEnd.service.UserPermissionService; // Đảm bảo tên service đúng
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.stereotype.Controller; // Dùng RestController cho API
import org.springframework.web.bind.annotation.*; // Import các annotation cần thiết

import java.util.*; // Import Collections

@RestController // Sử dụng RestController cho API
@CrossOrigin
public class UserPermissionController {

    @Autowired // Hoặc dùng constructor injection với @RequiredArgsConstructor
    private UserPermissionService userPermissionService;

    // --- Endpoint cũ getUserPermissions và updatePermission giữ nguyên ---
    // ... (Nếu có) ...


    /**
     * API thêm một quyền duy nhất.
     * Xử lý trường hợp quyền đã tồn tại trực tiếp.
     */
    @PostMapping("/addUserPermission")
    // @PreAuthorize("hasRole('ADMIN')") // Bật lại nếu cần phân quyền
    public ResponseEntity<Object> addSinglePermission(
            @RequestParam Long userId,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam String token
    ) {
        if (!userPermissionService.isUserLoggedIn(token)) {
            throw new RuntimeException("Token không hợp lệ!");
        }
        Map<String, Object> response = new HashMap<>();
        try {
            // Gọi service trả về Optional
            Optional<UserPermission> savedPermissionOpt = userPermissionService.addPermission(userId, resource, action);

            // Kiểm tra kết quả Optional
            if (savedPermissionOpt.isPresent()) {
                // Thành công, quyền đã được thêm mới
                response.put("message", "Thêm quyền thành công.");
                response.put("permission", savedPermissionOpt.get()); // Lấy giá trị từ Optional
                return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
            } else {
                // **XỬ LÝ TRÙNG LẶP NGAY TẠI ĐÂY**
                // Quyền đã tồn tại từ trước (service trả về Optional.empty())
                response.put("message", String.format("Quyền '%s' cho resource '%s' đã tồn tại cho người dùng ID %d.", action, resource, userId));
                return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
            }

        } catch (EntityNotFoundException e) { // Bắt lỗi không tìm thấy user
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (IllegalArgumentException e) { // Bắt lỗi tham số không hợp lệ từ service (nếu có)
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) { // Bắt các lỗi chung khác
            System.err.println("Lỗi không xác định khi thêm quyền: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Lỗi hệ thống khi thêm quyền.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    /**
     * API xóa một quyền duy nhất.
     */
    @DeleteMapping("/deleteUserPermission")
    // @PreAuthorize("hasRole('ADMIN')") // Bật lại nếu cần phân quyền
    public ResponseEntity<Object> deleteSinglePermission(
            @RequestParam Long userId,
            @RequestParam String resource,
            @RequestParam String action,
            @RequestParam String token
            ) {
        if (!userPermissionService.isUserLoggedIn(token)) {
            throw new RuntimeException("Token không hợp lệ!");
        }
        Map<String, Object> response = new HashMap<>();
        try {
            userPermissionService.deletePermission(userId, resource, action);
            response.put("message", String.format("Đã thực hiện xóa quyền '%s' cho resource '%s' của người dùng ID %d.", action, resource, userId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi xóa quyền: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Lỗi hệ thống khi xóa quyền.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API thêm nhiều quyền cùng lúc.
     * Sử dụng Map trả về từ Service.
     */
    @PostMapping("/addMultipleUserPermission")
    // @PreAuthorize("hasRole('ADMIN')") // Bật lại nếu cần phân quyền
    public ResponseEntity<Object> addMultiplePermissions(
            @RequestParam List<Long> userIds,
            @RequestParam List<String> resources,
            @RequestParam List<String> actions,
            @RequestParam String token
            ) {
        if (!userPermissionService.isUserLoggedIn(token)) {
            throw new RuntimeException("Token không hợp lệ!");

        }
        Map<String, Object> response = new HashMap<>(); // Map để trả về cho client
        try {
            // Nhận Map kết quả từ service
            Map<String, Object> serviceResult = userPermissionService.addMultiplePermissions(userIds, resources, actions);

            // Lấy thông tin từ Map kết quả của service
            int totalRequested = (int) serviceResult.getOrDefault("totalRequested", 0);
            int successfullyAdded = (int) serviceResult.getOrDefault("successfullyAdded", 0);
            int skippedDuplicateRequest = (int) serviceResult.getOrDefault("skippedDuplicateRequest", 0);
            int skippedExistingDb = (int) serviceResult.getOrDefault("skippedExistingDb", 0);
            int skippedUserNotFound = (int) serviceResult.getOrDefault("skippedUserNotFound", 0);
            // Lấy danh sách một cách an toàn, phòng trường hợp key không tồn tại hoặc giá trị null/sai kiểu
            Object addedPermissionsObj = serviceResult.get("addedPermissions");
            List<UserPermission> addedPermissions = new ArrayList<>();
            if (addedPermissionsObj instanceof List) {
                // Cố gắng ép kiểu một cách an toàn
                try {
                    List<?> rawList = (List<?>) addedPermissionsObj;
                    for (Object item : rawList) {
                        if (item instanceof UserPermission) {
                            addedPermissions.add((UserPermission) item);
                        }
                    }
                } catch (ClassCastException e) {
                    System.err.println("Lỗi ép kiểu danh sách addedPermissions từ service: " + e.getMessage());
                    // Có thể bỏ qua hoặc đặt addedPermissions là rỗng
                }
            }


            // Tạo message phản hồi chi tiết
            String message = String.format(
                    "Đã xử lý %d yêu cầu thêm quyền. Thêm thành công: %d. Bỏ qua: %d (Trùng lặp request: %d, Đã tồn tại DB: %d, User không tồn tại: %d).",
                    totalRequested,
                    successfullyAdded,
                    skippedDuplicateRequest + skippedExistingDb + skippedUserNotFound,
                    skippedDuplicateRequest,
                    skippedExistingDb,
                    skippedUserNotFound
            );

            // Đưa thông tin vào response trả về cho client
            response.put("message", message);
            // Đưa danh sách đã ép kiểu an toàn vào response
            response.put("addedPermissions", addedPermissions);
            // Có thể giữ lại map chi tiết từ service nếu client cần
            response.put("details", serviceResult);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi thêm nhiều quyền: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Lỗi hệ thống khi thêm nhiều quyền.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/deleteMultipleUserPermission") // Sử dụng đường dẫn khác
    // @PreAuthorize("hasRole('ADMIN')") // Bật lại nếu cần phân quyền
    public ResponseEntity<Object> deleteMultiplePermissions(
            @RequestParam List<Long> userIds,
            @RequestParam List<String> resources,
            @RequestParam List<String> actions,
            @RequestParam String token // Giữ lại token nếu cần xác thực
    ) {
        // 1. Xác thực token trước
        if (!userPermissionService.isUserLoggedIn(token)) {
            // Tạo response lỗi xác thực
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token không hợp lệ hoặc người dùng chưa đăng nhập!");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        // 2. Nếu token hợp lệ, tiếp tục xử lý
        Map<String, Object> response = new HashMap<>();
        try {
            // Gọi service để xóa nhiều quyền
            Map<String, Object> serviceResult = userPermissionService.deleteMultiplePermissions(userIds, resources, actions);

            // Lấy thông tin từ kết quả service
            int totalRequested = (int) serviceResult.getOrDefault("totalRequested", 0);
            int deletionsAttempted = (int) serviceResult.getOrDefault("uniqueDeletionsAttempted", 0);
            int skippedDuplicate = (int) serviceResult.getOrDefault("skippedDuplicateInRequest", 0);

            // Tạo message phản hồi
            String message = String.format(
                    "Đã xử lý %d yêu cầu xóa quyền. Số lệnh xóa duy nhất được thực thi: %d. Bỏ qua do trùng lặp trong request: %d.",
                    totalRequested,
                    deletionsAttempted,
                    skippedDuplicate
            );

            response.put("message", message);
            response.put("details", serviceResult); // Trả về chi tiết số liệu

            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK

        } catch (IllegalArgumentException e) {
            // Lỗi do input không hợp lệ (list khác size, list rỗng)
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) {
            // Các lỗi không mong muốn khác trong quá trình xóa
            System.err.println("Lỗi không xác định khi xóa nhiều quyền: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Lỗi hệ thống khi xóa nhiều quyền.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping("/getUserPermissionsByUserId") // Changed path slightly for clarity
    // @PreAuthorize("hasRole('ADMIN')") // Consider restricting access
    public ResponseEntity<Object> getUserPermissionsByUserId(
            @RequestParam Long userId, // Accepts a single Long
            @RequestParam String token
    ) {
        // 1. Validate token
        if (!userPermissionService.isUserLoggedIn(token)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token không hợp lệ hoặc người dùng chưa đăng nhập!");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }

        // 2. Handle potentially null userId (Spring's @RequestParam for primitive Long requires it,
        //    but for wrapper Long, it can be null if not provided)
        //    Using wrapper Long and checking null is safer than primitive long
        if (userId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User ID không được rỗng.");
            response.put("permissions", Collections.emptyList());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request for missing required param
        }


        // 3. Call the service to get permissions
        try {
            List<UserPermission> permissions = userPermissionService.getUserPermissionsByUserId(userId); // Use the new service method

            // 4. Prepare and return the response
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("Tìm thấy %d quyền cho người dùng ID %d.", permissions.size(), userId));
            response.put("permissions", permissions); // Add the list of permissions

            return new ResponseEntity<>(response, HttpStatus.OK); // 200 OK

        } catch (Exception e) {
            // 5. Handle unexpected errors
            System.err.println("Lỗi không xác định khi lấy quyền theo ID user: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi hệ thống khi lấy quyền theo ID user.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}