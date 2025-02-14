package com.example.QuanLyPhongMayBackEnd.controller;

import com.example.QuanLyPhongMayBackEnd.entity.Quyen;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import com.example.QuanLyPhongMayBackEnd.security.JwtUtil;
import com.example.QuanLyPhongMayBackEnd.service.MailService;
import com.example.QuanLyPhongMayBackEnd.service.TaiKhoanService;
import com.example.QuanLyPhongMayBackEnd.service.TokenService;
import com.example.QuanLyPhongMayBackEnd.service.UploadImageFile;
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

    InetAddress ip = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
    String ipAddress = ip.toString();

    public TaiKhoanController(UploadImageFile uploadImageFile) throws UnknownHostException {
        this.uploadImageFile = uploadImageFile;
    }
//    private String getSubRandom() {
//        return fileStorageService.provider_RandomString();
//    }
    @PostMapping("/luutaikhoan")
    public String luuTaiKhoan(
            @RequestParam String maTK,
            @RequestParam String tenDangNhap,
            @RequestParam String matKhau,
            @RequestParam String email,  // New parameter for email
            @RequestParam(required = false) MultipartFile imageFile,  // Handle the image file
            @RequestParam String quyen,  // Assuming quyen is a string for the role ID
            @RequestParam String tenQuyen) throws IOException {  // New parameter for the role name

        // Check if email is already taken
        if (taiKhoanRepository.findByEmail(email).isPresent()) {
            return "Email đã được sử dụng!";
        }

        // Create a new TaiKhoan instance and set values
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTK(maTK);
        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setMatKhau(matKhau);
        taiKhoan.setEmail(email);

        // Map 'quyen' to 'Quyen' object
        Quyen quyenObj = new Quyen();
        quyenObj.setMaQuyen(Long.valueOf(quyen));  // Set the permission ID
        quyenObj.setTenQuyen(tenQuyen);  // Set the role name (newly added)

        taiKhoan.setQuyen(quyenObj);

        // Encrypt the password before saving
        String encodedPassword = passwordEncoder.encode(taiKhoan.getMatKhau());
        taiKhoan.setMatKhau(encodedPassword);

        // Handle image upload to Cloudinary (if provided)
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = uploadImageFile.uploadImage(imageFile);  // Call the file upload service to upload the image to Cloudinary
            taiKhoan.setImage(imageUrl);  // Set the image URL from Cloudinary
        }

        // Save the account to the database
        taiKhoanService.luu(taiKhoan);

        // Send confirmation email after successful registration
        mailService.sendConfirmationEmail(email);

        return "Tài khoản đã được lưu thành công!";
    }





    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        Optional<TaiKhoan> taiKhoanOptional = taiKhoanService.timTaiKhoanByUsername(username);

        if (taiKhoanOptional.isPresent()) {
            TaiKhoan taiKhoan = taiKhoanOptional.get();

            // Kiểm tra mật khẩu
            if (passwordEncoder.matches(password, taiKhoan.getMatKhau())) {
                // Tạo token
                String token = jwtUtil.generateToken(username);

                // Lưu token vào cơ sở dữ liệu
                Token newToken = tokenService.saveToken(token, taiKhoan);

                // Trả về token
                Map<String, String> response = new HashMap<>();
                response.put("token", newToken.getToken());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/checkLogin")
    public ResponseEntity<Map<String, Object>> checkLogin(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        // Tìm token trong cơ sở dữ liệu
        Optional<Token> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isPresent()) {
            TaiKhoan taiKhoan = tokenOptional.get().getTaiKhoan(); // Lấy tài khoản từ token
            response.put("status", "success");
            response.put("message", "User is logged in");
            response.put("data", Map.of(
                    "maTK", taiKhoan.getMaTK(),
                    "tenDangNhap", taiKhoan.getTenDangNhap(),
                    "quyen", taiKhoan.getQuyen().getMaQuyen() // Giả sử quyen có phương thức getRole_id()
            ));
        } else {
            // Token không tồn tại hoặc không hợp lệ
            response.put("status", "error");
            response.put("message", "Invalid or expired token");
        }

        return new ResponseEntity<>(response, response.get("status").equals("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
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
    @GetMapping("/taikhoan/phantang")
    public ResponseEntity<Page<TaiKhoan>> layDSTaiKhoanPhanTrang(@RequestParam int pageNumber) {
        Page<TaiKhoan> taiKhoans = taiKhoanService.layDSTaiKhoanPhanTrang(pageNumber);
        return new ResponseEntity<>(taiKhoans, HttpStatus.OK);
    }
}
