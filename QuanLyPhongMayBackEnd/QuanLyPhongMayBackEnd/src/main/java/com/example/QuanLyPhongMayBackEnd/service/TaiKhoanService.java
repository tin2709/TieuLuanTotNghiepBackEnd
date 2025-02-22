package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.Auth;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.repository.AuthRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import com.example.QuanLyPhongMayBackEnd.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TaiKhoanService {
    @Autowired
    private PasswordEncoder passwordEncoder; // Được tự động tạo ra bởi Spring Security
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailService mailService;
    @Autowired
    private JwtUtil jwtUtil;
    // Xóa tài khoản theo mã
    public void xoa(String maTK) {
        taiKhoanRepository.deleteById(maTK);
    }

    // Lưu tài khoản
    public TaiKhoan luu(TaiKhoan taiKhoan) {
        return taiKhoanRepository.save(taiKhoan);
    }




    // Phương thức phân trang lấy danh sách tài khoản
    public Page<TaiKhoan> layDSTaiKhoanPhanTrang(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10); // Mỗi trang 10 tài khoản
        return taiKhoanRepository.findAll(pageable);
    }
    public Map<String, Object> checkUserLoginStatus(String tokenValue) {
        Map<String, Object> response = new HashMap<>();

        // Tìm token trong cơ sở dữ liệu
        Optional<Token> token = tokenRepository.findByToken(tokenValue);

        if (token.isPresent() && token.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            // Token hợp lệ, lấy thông tin người dùng
            TaiKhoan taiKhoan = token.get().getTaiKhoan();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", taiKhoan.getMaTK());
            userData.put("username", taiKhoan.getTenDangNhap());
            userData.put("email", taiKhoan.getEmail());
            userData.put("role", taiKhoan.getQuyen().getMaQuyen());
            userData.put("avatar_url", taiKhoan.getImage());

            response.put("status", "success");
            response.put("message", "User is logged in");
            response.put("data", Map.of("user", userData));
        } else {
            // Token không hợp lệ hoặc hết hạn
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
        }

        return response;
    }

    // Tìm tài khoản theo username (dùng trong login)
    public Optional<TaiKhoan> timTaiKhoanByUsername(String username) {
        return taiKhoanRepository.findByTenDangNhap(username); // Giả sử có phương thức này trong TaiKhoanRepository
    }
    // Kiểm tra xem một chuỗi có phải là email không
    private boolean isEmail(String identifier) {
        return identifier != null && identifier.contains("@") && identifier.contains(".");
    }
    public ResponseEntity<?> handleForgotPassword(String identifier) {
        Optional<TaiKhoan> taiKhoanOptional;

        // Kiểm tra xem identifier có phải là email không
        if (isEmail(identifier)) {
            // Nếu là email, tìm user theo email
            taiKhoanOptional = taiKhoanRepository.findByEmail(identifier);
        } else {
            // Nếu không phải email, tìm user theo username
            taiKhoanOptional = taiKhoanRepository.findByTenDangNhap(identifier);
        }

        // Kiểm tra nếu không tìm thấy user
        if (taiKhoanOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "error",
                    "message", "User not found."
            ));
        }

        TaiKhoan taiKhoan = taiKhoanOptional.get();
        // Tìm hoặc tạo Auth
        Optional<Auth> existingAuth = authRepository.findByTaiKhoanEmailAndPurpose(taiKhoan.getEmail(), "FORGOT_PASSWORD");
        Auth auth;
        if (existingAuth.isPresent()) {
            auth = existingAuth.get();
            authService.updateOtp(auth); // Ghi đè OTP mới
        } else {
            auth = authService.generateOtp(taiKhoan, "FORGOT_PASSWORD"); // Tạo mới nếu chưa có
        }

        // Gửi OTP qua email
        mailService.sendOtp(taiKhoan.getEmail(), auth.getOtp());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "email", taiKhoan.getEmail(),
                "message", "OTP sent to email."
        ));
    }


    // Xử lý verify OTP
    public ResponseEntity<?> handleVerifyOtp(String email, String otp) {
        Optional<Auth> authOptional = authRepository.findByTaiKhoanEmailAndOtpAndPurpose(email, otp, "FORGOT_PASSWORD");

        if (authOptional.isEmpty() || authOptional.get().getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", "Invalid or expired OTP."
            ));
        }

        Auth auth = authOptional.get();
        auth.setVerified(true);
        authRepository.save(auth);

        return ResponseEntity.ok(Map.of(
                "status", "success",

                "message", "OTP verified successfully."
        ));
    }

    public Map<String, Object> updatePassword(String email, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        Optional<Auth> authOptional = authRepository.findByTaiKhoanEmailAndPurposeAndIsVerified(email, "FORGOT_PASSWORD", true);

        if (authOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Unauthorized or OTP not verified.");
            return response;
        }



        Optional<TaiKhoan> userOptional = taiKhoanRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            response.put("status", "error");
            response.put("message", "User not found.");
            return response;
        }
        try {
            TaiKhoan taiKhoan = userOptional.get();

            String encodedPassword = passwordEncoder.encode(newPassword);
            taiKhoan.setMatKhau(encodedPassword);

            taiKhoanRepository.save(taiKhoan); // Lưu user đã được cập nhật
            authRepository.delete(authOptional.get()); // Xóa dòng Auth tương ứng
            response.put("status", "success");
            response.put("message", "Password updated successfully for user with email: " + email);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }
    public Map<String, Object> reLogin(String tokenValue) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Optional<Token> optionalToken = tokenRepository.findByToken(tokenValue);

        if (optionalToken.isPresent() && optionalToken.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            TaiKhoan taiKhoan = optionalToken.get().getTaiKhoan();
            tokenRepository.delete(optionalToken.get());

            String newTokenValue = jwtUtil.generateToken(taiKhoan.getTenDangNhap());
            Token newToken = new Token(newTokenValue, LocalDateTime.now(),
                    LocalDateTime.now().plusSeconds(jwtUtil.getExpiration()), taiKhoan);
            tokenRepository.save(newToken);

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", taiKhoan.getMaTK());
            userData.put("username", taiKhoan.getTenDangNhap());
            userData.put("email", taiKhoan.getEmail());
            userData.put("role", taiKhoan.getQuyen().getMaQuyen());
            userData.put("avatar_url", taiKhoan.getImage());


            response.put("relogin", Map.of(
                    "Login", true,
                    "status", "success",
                    "message", "Login successful with existing token",
                    "newToken", newTokenValue,
                    "data", Map.of("user", userData),
                    "time", LocalDateTime.now()
            ));
        } else {
            response.put("login", Map.of(
                    "Login", false,
                    "status", "error",
                    "message", "Invalid or expired token"
            ));
        }
        return response;
    }
}
