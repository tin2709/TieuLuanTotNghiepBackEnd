package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.DTO.LoginResponseDTO;
import com.example.QuanLyPhongMayBackEnd.entity.Quyen;
import com.example.QuanLyPhongMayBackEnd.entity.RefreshToken;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import com.example.QuanLyPhongMayBackEnd.security.JwtUtil;
import com.example.QuanLyPhongMayBackEnd.service.*;
import io.sentry.Sentry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@CrossOrigin
@RestController
public class TaiKhoanController {
    @Value("${upload-dir}")
    private String uploadDir;

    @Autowired
    private PasswordEncoder passwordEncoder; // Được tự động tạo ra bởi Spring Security

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private final UploadImageFile uploadImageFile;
    @Autowired
    private RefreshTokenService refreshTokenService;
    InetAddress ip = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
    String ipAddress = ip.toString();
    @Autowired
    private MayTinhService mayTinhService;

    public TaiKhoanController(UploadImageFile uploadImageFile) throws UnknownHostException {
        this.uploadImageFile = uploadImageFile;
    }

    //    private String getSubRandom() {
//        return fileStorageService.provider_RandomString();
//    }
    @PostMapping("/luutaikhoan")
    public ResponseEntity<?> luuTaiKhoan( // Changed return type to ResponseEntity<?>
                                          @RequestParam String tenDangNhap,
                                          @RequestParam String matKhau,
                                          @RequestParam String email,
                                          @RequestParam(required = false) MultipartFile imageFile,
                                          @RequestParam String maQuyen
    ) throws IOException {

        // Check if email is already taken
        Optional<TaiKhoan> existingTaiKhoan = taiKhoanRepository.findByEmail(email);
        if (existingTaiKhoan.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // Return 400 status
                    .body("Email đã được sử dụng!"); // Return error message
        }

        // Create a new TaiKhoan instance and set values
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(matKhau);
        taiKhoan.setEmail(email);

        // Map 'quyen' to 'Quyen' object
        Quyen quyen = new Quyen(maQuyen);
        taiKhoan.setQuyen(quyen);

        // Encrypt the password before saving
        String encodedPassword = passwordEncoder.encode(taiKhoan.getMatKhau());
        taiKhoan.setMatKhau(encodedPassword);

        // Handle image upload to Cloudinary (if provided)
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadImageFile.uploadImage(imageFile);
            taiKhoan.setImage(imageUrl);
        }

        // Save the account to the database
        TaiKhoan savedTaiKhoan = taiKhoanService.luu(taiKhoan); // Get the saved TaiKhoan object

        // Send confirmation email after successful registration
        mailService.sendConfirmationEmail(email);

        return ResponseEntity.ok(savedTaiKhoan); // Return 200 status with the saved TaiKhoan object
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {

        // 1. Tìm tài khoản theo tên đăng nhập
        Optional<TaiKhoan> taiKhoanOptional = taiKhoanService.timTaiKhoanByUsername(username);

        // Kiểm tra xem tài khoản có tồn tại không
        if (taiKhoanOptional.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOptional.get();

            // 2. Kiểm tra tài khoản có bị khóa không
            if (taiKhoan.isBanned()) {
                System.out.println("Login thất bại cho user: " + username + ". Tài khoản bị khóa.");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden - Tài khoản bị cấm
            }


            if (taiKhoan.getMatKhau().equals(password)) {
                System.out.println("Login thành công cho user: " + username + " bằng plain text password match (INSECURE).");
            } else if (passwordEncoder.matches(password, taiKhoan.getMatKhau())) { // Kiểm tra bằng PasswordEncoder nếu plain text không khớp
                System.out.println("Login thành công cho user: " + username + " bằng encoded password match.");
            }
            else {
                // Mật khẩu không khớp (cả plain text và encoded)
                System.out.println("Login thất bại cho user: " + username + ". Sai mật khẩu.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized - Xác thực thất bại (sai mật khẩu)
            }

            // **ĐĂNG NHẬP THÀNH CÔNG (nếu đến được đây, mật khẩu đã được xác thực)**
            try {
                // 4. Tạo Access Token sử dụng JWT
                String accessToken = jwtUtil.generateToken(username);

                // 5. Lấy thời gian hết hạn từ Access Token
                Date expirationDate = jwtUtil.getExpirationDateFromToken(accessToken);
                Long expiresAtTimestamp = (expirationDate != null) ? expirationDate.getTime() : null; // Chuyển sang milliseconds timestamp

                // 6. Lưu Access Token (và có thể cập nhật thời gian hết hạn) vào DB (Tùy chọn - Cần xem xét lại tính cần thiết trong JWT stateless)
                // Lưu ý: Việc lưu token vào DB có thể không cần thiết trong kiến trúc JWT stateless.
                // Tuy nhiên, code hiện tại vẫn giữ logic này. Cần đánh giá lại xem có thực sự cần thiết hay không.
                tokenService.saveToken(accessToken, taiKhoan);

                // 7. Tạo Refresh Token để cấp mới Access Token khi hết hạn
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(String.valueOf(taiKhoan.getMaTK()));

                // 8. Tạo DTO chứa thông tin phản hồi đăng nhập thành công
                LoginResponseDTO loginResponse = new LoginResponseDTO( // Consider creating this DTO inside the method scope
                        accessToken,
                        refreshToken.getToken(),
                        taiKhoan.getMaTK(),
                        taiKhoan.getTenDangNhap(),
                        taiKhoan.getEmail(),
                        taiKhoan.getQuyen() != null ? taiKhoan.getQuyen().getMaQuyen() : null,
                        taiKhoan.getImage(),
                        expiresAtTimestamp // Truyền timestamp hết hạn vào DTO
                );

                // 9. Trả về phản hồi thành công (200 OK) kèm theo DTO
                return new ResponseEntity<>(loginResponse, HttpStatus.OK);

            } catch (Exception e) {
                // Xử lý lỗi nếu có lỗi xảy ra trong quá trình tạo token, lưu token, ...
                System.err.println("Lỗi khi tạo/lưu token/lấy expiry cho user " + username + ": " + e.getMessage());
                e.printStackTrace(); // In stacktrace để debug (chỉ nên dùng trong môi trường dev/test)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error - Lỗi máy chủ
            }

        } else {
            // Không tìm thấy tài khoản với tên đăng nhập này
            System.out.println("Login thất bại. Không tìm thấy user: " + username);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401 Unauthorized - Xác thực thất bại (không tìm thấy user)
        }
    }
    @GetMapping("/checkUser")
    public ResponseEntity<Map<String, Object>> checkUser(@RequestParam String username,
                                                         @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();

        // Tìm tài khoản trong cơ sở dữ liệu dựa vào tên đăng nhập
        Optional<TaiKhoan> taiKhoanOptional = taiKhoanRepository.findByTenDangNhap(username);

        if (taiKhoanOptional.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOptional.get();

            // Kiểm tra nếu mật khẩu đã mã hóa đúng
            if (passwordEncoder.matches(password, taiKhoan.getMatKhau())) {
                response.put("status", "success");
                response.put("message", "User found");
                response.put("data", Map.of(
                        "maTK", taiKhoan.getMaTK(),
                        "tenDangNhap", taiKhoan.getTenDangNhap(),
                        "quyen", taiKhoan.getQuyen().getMaQuyen(), // Giả sử quyen có phương thức getMaQuyen()
                        "email", taiKhoan.getEmail(),
                        "image", taiKhoan.getImage(),
                        "isBanned", taiKhoan.isBanned() // Thêm thuộc tính isBanned vào response
                ));
            } else {
                // Mật khẩu không đúng
                response.put("status", "error");
                response.put("message", "Invalid username or password");
            }
        } else {
            // Tài khoản không tồn tại
            response.put("status", "error");
            response.put("message", "User not found");
        }

        return new ResponseEntity<>(response, response.get("status").equals("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/checkingLogin")
    public ResponseEntity<Map<String, Object>> checkingLogin(@RequestParam String username,
                                                             @RequestParam String password,
                                                             @RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        // Tìm token trong cơ sở dữ liệu
        Optional<Token> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isPresent()) {
            TaiKhoan taiKhoan = tokenOptional.get().getTaiKhoan(); // Lấy tài khoản từ token

            // Kiểm tra nếu username và mật khẩu đã mã hóa đúng
            if (taiKhoan.getTenDangNhap().equals(username) && passwordEncoder.matches(password, taiKhoan.getMatKhau())) {
                response.put("status", "success");
                response.put("message", "User is logged in");
                response.put("data", Map.of(
                        "maTK", taiKhoan.getMaTK(),
                        "tenDangNhap", taiKhoan.getTenDangNhap(),
                        "quyen", taiKhoan.getQuyen().getMaQuyen(), // Giả sử quyen có phương thức getMaQuyen()
                        "isBanned", taiKhoan.isBanned() // Thêm thuộc tính isBanned vào response
                ));
            } else {
                // Username or password incorrect
                response.put("status", "error");
                response.put("message", "Invalid username or password");
            }
        } else {
            // Token không tồn tại hoặc không hợp lệ
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
        }

        return new ResponseEntity<>(response, response.get("status").equals("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/reLogin")
    public Map<String, Object> reLogin(@RequestParam String token) throws Exception {
        return taiKhoanService.reLogin(token);
    }




    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldname = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldname, errorMessage);

        });
        return errors;
    }


    // API để xóa tài khoản theo mã
    @DeleteMapping("/taikhoan/{maTK}")
    public ResponseEntity<String> xoaTaiKhoan(@PathVariable("maTK") String maTK) {
        taiKhoanService.xoa(maTK);
        return new ResponseEntity<>("Tài khoản với mã " + maTK + " đã được xóa.", HttpStatus.OK);
    }

    // API phân trang lấy danh sách tài khoản
    @GetMapping("/taikhoan/phantrang")
    public ResponseEntity<Page<TaiKhoan>> layDSTaiKhoanPhanTrang(@RequestParam int pageNumber) {
        Page<TaiKhoan> taiKhoans = taiKhoanService.layDSTaiKhoanPhanTrang(pageNumber);
        return new ResponseEntity<>(taiKhoans, HttpStatus.OK);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@RequestParam Map<String, String> request) {
        String email = request.get("email");
        return taiKhoanService.handleForgotPassword(email);
    }

    @PostMapping("/verify_otp_forgot_password")
    public ResponseEntity<?> verifyOtps(@RequestParam Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        return taiKhoanService.handleVerifyOtp(email, otp);
    }

    @PostMapping("/update_password")
    public ResponseEntity<Map<String, Object>> updatePass(@RequestParam Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        // Gọi service để xử lý logic cập nhật password
        Map<String, Object> response = taiKhoanService.updatePassword(email, password);

        // Tạo ResponseEntity từ Map
        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response); // Trả về 200 OK nếu thành công
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // Trả về 401 Unauthorized nếu lỗi
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(@RequestParam String token) {
        // Call the service method to handle logout logic
        Map<String, Object> response = taiKhoanService.logoutUser(token);

        // Check the status of the response and return appropriate HTTP status
        if ("success".equals(response.get("status"))) {
            return new ResponseEntity<>(response, HttpStatus.OK); // Success response with 200 OK
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Error response with 400 BAD REQUEST
        }
    }

    @PostMapping("/banUser")
    public ResponseEntity<Map<String, Object>> banUser(
            @RequestParam Long maTk,
            @RequestParam String token) {

        // Lấy maTK từ token
        Long maTK = jwtUtil.getMaTKFromToken(token);  // Lấy maTK từ token
        if (maTK == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Token không hợp lệ"
            ));
        }

        // Kiểm tra xem người dùng có quyền admin không
        TaiKhoan taiKhoan = taiKhoanRepository.findById(String.valueOf(maTK)).orElse(null);
        if (taiKhoan == null || taiKhoan.getQuyen().getMaQuyen() != 5) {  // Kiểm tra quyền của người dùng
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "message", "Chỉ có admin mới có quyền"
            ));
        }

        // Kiểm tra và ban user
        TaiKhoan userToBan = taiKhoanRepository.findById(String.valueOf(maTk)).orElse(null);
        if (userToBan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User not found"
            ));
        }

        // Ban user
        userToBan.setBanned(true);
        taiKhoanRepository.save(userToBan);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User has been banned"
        ));
    }

    // API để mở khóa tài khoản
    @PostMapping("/unbanUser")
    public ResponseEntity<Map<String, Object>> unbanUser(
            @RequestParam Long maTk,
            @RequestParam String token) {


        // Lấy maTK từ token
        Long maTK = jwtUtil.getMaTKFromToken(token);  // Lấy maTK từ token
        if (maTK == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Token không hợp lệ"
            ));
        }

        // Kiểm tra xem người dùng có quyền admin không
        TaiKhoan taiKhoan = taiKhoanRepository.findById(String.valueOf(maTK)).orElse(null);
        if (taiKhoan == null || taiKhoan.getQuyen().getMaQuyen() != 5) {  // Kiểm tra quyền của người dùng
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", "error",
                    "message", "Chỉ có admin mới có quyền"
            ));
        }

        // Kiểm tra và unban user
        TaiKhoan userToUnban = taiKhoanRepository.findById(String.valueOf(maTk)).orElse(null);
        if (userToUnban == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User not found"
            ));
        }

        // Unban user
        userToUnban.setBanned(false);
        taiKhoanRepository.save(userToUnban);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User has been unbanned"
        ));


    }
    @GetMapping("/getAllUser")
    public ResponseEntity<Map<String, Object>> getAllAccounts() {
        Map<String, Object> response = new HashMap<>();

        // Retrieve all accounts from the database
        Iterable<TaiKhoan> allTaiKhoans = taiKhoanRepository.findAll();

        // Check if there are any accounts in the database
        if (allTaiKhoans != null) {
            response.put("status", "success");
            response.put("message", "Accounts found");
            response.put("data", allTaiKhoans);
        } else {
            response.put("status", "error");
            response.put("message", "No accounts found");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/CapNhatTaiKhoan") // Use PUT for updates
    public ResponseEntity<?> capNhatTaiKhoan(
            @RequestParam Long maTK, // Get maTK from a Request Param.  This is less RESTful.
            @RequestParam String tenDangNhap,
            @RequestParam String email,
            @RequestParam(required = false) String matKhau, // Password is optional (for changes)
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam String maQuyen,
            @RequestParam String token  // You'll likely need the token for authorization
    ) {

        if (!mayTinhService.isUserLoggedIn(token)) {
            return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
        }


        try {
            // Fetch existing user
            Optional<TaiKhoan> existingUserOptional = taiKhoanRepository.findById(String.valueOf(maTK)); // Use findById from service.  Convert maTK to String.
            if (!existingUserOptional.isPresent()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            TaiKhoan existingUser = existingUserOptional.get();


            // Update fields.  Only update if provided.
            existingUser.setTenDangNhap(tenDangNhap);
            existingUser.setEmail(email);

            // Handle Password Update *Separately*
            if (matKhau != null && !matKhau.isEmpty()) {
                String encodedPassword = passwordEncoder.encode(matKhau);
                existingUser.setMatKhau(encodedPassword);
            }
            // Map 'quyen' to 'Quyen' object
            Quyen quyen = new Quyen(maQuyen);
            existingUser.setQuyen(quyen);
            // Handle Image Update (Optional)
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = uploadImageFile.uploadImage(imageFile);  //Use your file upload service
                existingUser.setImage(imageUrl);
            }

            TaiKhoan updatedTaiKhoan = taiKhoanService.luu(existingUser); // Reuse your existing 'luu' method.
            return new ResponseEntity<>(updatedTaiKhoan, HttpStatus.OK);

        } catch (Exception e) {
            Sentry.captureException(e);
            // Log the exception (using a logger like SLF4J is best practice)
            e.printStackTrace();
            return new ResponseEntity<>("Error updating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestParam String refreshTokenValue,
                                          @RequestParam String maTK) {

        // 1. Tìm refresh token cũ
        Optional<RefreshToken> optionalOldToken = refreshTokenService.findByToken(refreshTokenValue);

        // 2. Kiểm tra tồn tại
        if (optionalOldToken.isEmpty()) {
            System.err.println("Lỗi làm mới token: Refresh token không tồn tại.");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        RefreshToken oldToken = optionalOldToken.get();
        TaiKhoan taiKhoan = oldToken.getTaiKhoan(); // Lấy tài khoản từ Refresh Token

        // 3. Kiểm tra tài khoản từ token
        if (taiKhoan == null) {
            System.err.println("Lỗi làm mới token: Không thể xác định tài khoản từ refresh token.");
            refreshTokenService.deleteRefreshToken(oldToken); // Xóa token lỗi
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // 4. *** KIỂM TRA KHÔNG AN TOÀN: Kiểm tra maTK gửi lên ***
        try {
            Long requestMaTkLong = Long.parseLong(maTK);
            if (!taiKhoan.getMaTK().equals(requestMaTkLong)) {
                System.err.println("Lỗi làm mới token: maTK trong request ("+maTK+") không khớp với token.");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (NumberFormatException e) {
            System.err.println("Lỗi làm mới token: maTK trong request không phải là số hợp lệ: " + maTK);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // 5. Kiểm tra hạn sử dụng của refresh token CŨ
        Optional<RefreshToken> verifiedTokenOpt = refreshTokenService.verifyExpiration(oldToken);
        if (verifiedTokenOpt.isEmpty()) {
            System.err.println("Lỗi làm mới token: Refresh token đã hết hạn.");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // --- Thực hiện Logic Xóa + Tạo Mới ---
        try {
            // 6. Xóa TẤT CẢ Refresh Token của người dùng này (Logic giống logout)
            int deletedRefreshCount = refreshTokenService.deleteAllTokensByUserId(String.valueOf(taiKhoan.getMaTK()));
            System.out.println("Đã xóa " + deletedRefreshCount + " Refresh Token cũ của tài khoản: " + taiKhoan.getTenDangNhap());

            // 7. Xóa TẤT CẢ Access Token cũ của người dùng này (Logic giống logout)
            int deletedAccessCount = taiKhoanService.deleteAccessTokenByUser(taiKhoan);
            System.out.println("Đã xóa " + deletedAccessCount + " Access Token cũ của tài khoản: " + taiKhoan.getTenDangNhap());

            // --- Tạo Token Mới ---

            // 8. Tạo refresh token MỚI
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(String.valueOf(taiKhoan.getMaTK()));

            // 9. Tạo access token MỚI (JWT String)
            String newAccessToken = jwtUtil.generateToken(taiKhoan.getTenDangNhap());

            // 10. **QUAN TRỌNG:** Lưu access token MỚI vào bảng `token`.
            //     Phương thức `saveAccessToken` trong `TaiKhoanService` đã được thiết kế
            //     để tính toán `expiresAt` dựa trên hạn của Access Token (thông qua jwtUtil.getExpiration()).
            //     Nó sẽ lưu hạn của `newAccessToken` chứ KHÔNG phải hạn của `refreshTokenValue`.
            taiKhoanService.saveAccessToken(newAccessToken, taiKhoan);
            System.out.println("Đã lưu Access Token mới vào DB với hạn của chính nó.");


            // 11. Chuẩn bị Map trả về chứa token mới (Không dùng DTO Response)
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("token", newAccessToken);
            responseBody.put("refreshToken", newRefreshToken.getToken());

            // 12. Trả về thành công
            return new ResponseEntity<>(responseBody, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Lỗi trong quá trình làm mới token: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/taikhoanemail")
    public ResponseEntity<?> getTaiKhoanByEmail(@RequestParam String email) {
        try {
            Optional<TaiKhoan> taiKhoanOptional = taiKhoanRepository.findByEmail(email);
            if (taiKhoanOptional.isPresent()) {
                TaiKhoan taiKhoan = taiKhoanOptional.get();
                return ResponseEntity.ok(taiKhoan); // Return 200 OK with TaiKhoan object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "error", "message", "Không tìm thấy tài khoản với email: " + email)); // Return 404 Not Found
            }
        } catch (Exception e) {
            Sentry.captureException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", "Lỗi khi lấy thông tin tài khoản: " + e.getMessage())); // Return 500 Internal Server Error
        }
    }

}
