package com.example.QuanLyPhongMayBackEnd.service;

// ... (các import khác giữ nguyên) ...
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.UserPermission;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.UserPermissionRepository;
import jakarta.persistence.EntityNotFoundException;
// import org.springframework.transaction.annotation.Transactional; // Dùng của jakarta nếu mới
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; // Hoặc dùng của springframework
import lombok.RequiredArgsConstructor; // Sử dụng constructor injection
// import org.springframework.beans.factory.annotation.Autowired; // BỎ Autowired ở đây
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service // Đặt tên bean rõ ràng
public class UserPermissionService {

    // BỎ @Autowired ở đây, chỉ cần private final
    @Autowired
    private UserPermissionRepository userPermissionRepository;
    // BỎ @Autowired ở đây, chỉ cần private final
    @Autowired
    private TaiKhoanRepository taiKhoanRepository; // Vẫn giữ repo với kiểu ID <TaiKhoan, String>
    @Autowired
    private TaiKhoanService taiKhoanService;

    public boolean isUserLoggedIn(String token) {
        return taiKhoanService.checkUserLoginStatus(token).get("status").equals("success");
    }
    /**
     * Thêm một quyền cụ thể cho người dùng.
     * Trả về Optional chứa UserPermission nếu thành công.
     * Trả về Optional.empty() nếu quyền đã tồn tại.
     * Ném ra EntityNotFoundException nếu người dùng không tồn tại.
     * Áp dụng WORKAROUND cho TaiKhoanRepository.
     */
    @Transactional
    public Optional<UserPermission> addPermission(Long userId, String resource, String action) {
        // --- WORKAROUND: Chuyển Long sang String để gọi findById ---
        String userIdAsString = String.valueOf(userId); // Hoặc userId.toString()
        TaiKhoan taiKhoan = taiKhoanRepository.findById(userIdAsString)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản với ID: " + userId));
        // --- HẾT WORKAROUND ---

        // Kiểm tra quyền đã tồn tại chưa (Dùng Long ID)
        boolean exists = userPermissionRepository.existsByTaiKhoan_MaTKAndResourceAndAction(userId, resource, action);
        if (exists) {
            return Optional.empty();
        }

        // Tạo và lưu quyền mới
        UserPermission newPermission = new UserPermission(taiKhoan, resource, action);
        UserPermission savedPermission = userPermissionRepository.save(newPermission);
        return Optional.of(savedPermission);
    }

    /**
     * Xóa một quyền cụ thể của người dùng.
     */
    @Transactional
    public void deletePermission(Long userId, String resource, String action) {
        userPermissionRepository.deleteByTaiKhoan_MaTKAndResourceAndAction(userId, resource, action);
    }

    /**
     * Thêm nhiều quyền cho nhiều người dùng dựa trên danh sách.
     * Trả về Map chứa thông tin chi tiết về kết quả.
     * Áp dụng WORKAROUND cho TaiKhoanRepository.
     */
    @Transactional
    public Map<String, Object> addMultiplePermissions(List<Long> userIds, List<String> resources, List<String> actions) {
        // 1. Kiểm tra đầu vào
        if (userIds == null || resources == null || actions == null ||
                userIds.isEmpty() || resources.isEmpty() || actions.isEmpty()) {
            throw new IllegalArgumentException("Danh sách userIds, resources, và actions không được rỗng.");
        }
        int requestSize = userIds.size();
        if (requestSize != resources.size() || requestSize != actions.size()) {
            throw new IllegalArgumentException("Số lượng userIds, resources, và actions phải bằng nhau.");
        }

        // 2. Khởi tạo Map kết quả và các biến đếm/theo dõi
        Map<String, Object> result = new HashMap<>();
        List<UserPermission> permissionsToSave = new ArrayList<>();
        Set<String> processedKeys = new HashSet<>(); // Để tránh trùng lặp trong cùng 1 request
        int successfullyAddedCount = 0;
        int skippedDuplicateRequestCount = 0;
        int skippedExistingDbCount = 0;
        int skippedUserNotFoundCount = 0;

        // 3. WORKAROUND: Lấy danh sách TaiKhoan bằng String ID
        List<String> userStringIds = userIds.stream()
                .distinct() // Chỉ lấy các ID duy nhất để query
                .map(String::valueOf) // Chuyển Long thành String
                .collect(Collectors.toList());

        List<TaiKhoan> foundTaiKhoans = taiKhoanRepository.findAllById(userStringIds);
        Map<Long, TaiKhoan> taiKhoanMap = foundTaiKhoans.stream()
                .collect(Collectors.toMap(TaiKhoan::getMaTK, tk -> tk)); // Tạo Map với Key là Long

        // 4. Duyệt qua từng yêu cầu thêm quyền
        for (int i = 0; i < requestSize; i++) {
            Long userId = userIds.get(i); // Sử dụng Long ID gốc để xử lý logic
            String resource = resources.get(i);
            String action = actions.get(i);
            String currentKey = userId + ":" + resource + ":" + action; // Khóa để kiểm tra trùng lặp request

            // 4a. Bỏ qua nếu trùng lặp trong chính request này
            if (!processedKeys.add(currentKey)) {
                skippedDuplicateRequestCount++;
                continue;
            }

            // 4b. Bỏ qua nếu không tìm thấy TaiKhoan (từ Map đã lấy)
            TaiKhoan taiKhoan = taiKhoanMap.get(userId);
            if (taiKhoan == null) {
                skippedUserNotFoundCount++;
                continue;
            }

            // 4c. (Tùy chọn) Validate resource/action ở đây nếu cần

            // 4d. Bỏ qua nếu quyền đã tồn tại trong DB (Dùng Long ID)
            boolean exists = userPermissionRepository.existsByTaiKhoan_MaTKAndResourceAndAction(userId, resource, action);
            if (exists) {
                skippedExistingDbCount++;
                continue;
            }

            // 4e. Nếu mọi thứ ổn, thêm vào danh sách chờ lưu
            permissionsToSave.add(new UserPermission(taiKhoan, resource, action));
        }

        // 5. Lưu tất cả các quyền hợp lệ vào DB
        List<UserPermission> savedPermissions = new ArrayList<>(); // Danh sách quyền đã thực sự lưu
        if (!permissionsToSave.isEmpty()) {
            savedPermissions = userPermissionRepository.saveAll(permissionsToSave);
            successfullyAddedCount = savedPermissions.size();
        }

        // 6. Đưa kết quả vào Map
        result.put("totalRequested", requestSize);
        result.put("successfullyAdded", successfullyAddedCount);
        result.put("skippedDuplicateRequest", skippedDuplicateRequestCount);
        result.put("skippedExistingDb", skippedExistingDbCount);
        result.put("skippedUserNotFound", skippedUserNotFoundCount);
        result.put("addedPermissions", savedPermissions); // Đưa danh sách quyền đã thêm vào map

        // 7. Trả về Map kết quả
        return result;
    }
    @Transactional
    public Map<String, Object> deleteMultiplePermissions(List<Long> userIds, List<String> resources, List<String> actions) {
        // 1. Kiểm tra đầu vào
        if (userIds == null || resources == null || actions == null ||
                userIds.isEmpty() || resources.isEmpty() || actions.isEmpty()) {
            throw new IllegalArgumentException("Danh sách userIds, resources, và actions không được rỗng.");
        }
        int requestSize = userIds.size();
        if (requestSize != resources.size() || requestSize != actions.size()) {
            throw new IllegalArgumentException("Số lượng userIds, resources, và actions phải bằng nhau.");
        }

        // 2. Khởi tạo Map kết quả và các biến đếm/theo dõi
        Map<String, Object> result = new HashMap<>();
        Set<String> processedKeys = new HashSet<>(); // Để tránh trùng lặp trong cùng 1 request
        int deletionsAttemptedCount = 0;
        int skippedDuplicateRequestCount = 0;

        // 3. Duyệt qua từng yêu cầu xóa quyền
        for (int i = 0; i < requestSize; i++) {
            Long userId = userIds.get(i);
            String resource = resources.get(i);
            String action = actions.get(i);
            String currentKey = userId + ":" + resource + ":" + action; // Khóa để kiểm tra trùng lặp request

            // 3a. Bỏ qua nếu trùng lặp trong chính request này
            if (!processedKeys.add(currentKey)) {
                skippedDuplicateRequestCount++;
                continue; // Không thực hiện xóa cho bản ghi trùng lặp trong request
            }

            // 3b. Thực hiện xóa (Không cần kiểm tra tồn tại trước)
            // Tên phương thức repository phải khớp: deleteByTaiKhoan_MaTKAndResourceAndAction
            try {
                userPermissionRepository.deleteByTaiKhoan_MaTKAndResourceAndAction(userId, resource, action);
                deletionsAttemptedCount++; // Đếm số lần thực hiện xóa thành công (không có lỗi)
            } catch (Exception e) {
                // Ghi lại lỗi nếu cần thiết, nhưng thường deleteBy không ném lỗi nếu không tìm thấy
                // Có thể ném lỗi nếu có vấn đề với DB connection hoặc transaction
                System.err.printf("Lỗi khi thực hiện xóa quyền cho key %s: %s%n", currentKey, e.getMessage());
                // Quyết định xem có nên dừng lại hay tiếp tục với các quyền khác
                // Ở đây chúng ta sẽ tiếp tục
            }
        }

        // 4. Đưa kết quả vào Map
        result.put("totalRequested", requestSize);
        result.put("uniqueDeletionsAttempted", deletionsAttemptedCount); // Số lệnh xóa duy nhất đã thực thi
        result.put("skippedDuplicateInRequest", skippedDuplicateRequestCount); // Số yêu cầu trùng lặp bị bỏ qua

        // 5. Trả về Map kết quả
        return result;
    }
    public List<UserPermission> getUserPermissionsByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userPermissionRepository.findByTaiKhoan_MaTKIn(userIds);
    }


    // --- NEW: Get permissions for a single user ID ---
    /**
     * Lấy tất cả UserPermission cho một user ID cụ thể.
     * Trả về danh sách rỗng nếu không tìm thấy quyền nào hoặc user ID là null.
     */
    public List<UserPermission> getUserPermissionsByUserId(Long userId) {
        // Handle null input gracefully
        if (userId == null) {
            return Collections.emptyList(); // Return an empty list if userId is null
        }
        // Call the repository method for a single user ID
        return userPermissionRepository.findByTaiKhoan_MaTK(userId);
    }

}