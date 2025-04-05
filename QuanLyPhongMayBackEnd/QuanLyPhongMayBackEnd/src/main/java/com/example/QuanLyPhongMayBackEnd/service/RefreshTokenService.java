package com.example.QuanLyPhongMayBackEnd.service;

import com.example.QuanLyPhongMayBackEnd.entity.RefreshToken;
import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
// Bỏ import TokenRefreshException
import com.example.QuanLyPhongMayBackEnd.repository.RefreshTokenRepository;
import com.example.QuanLyPhongMayBackEnd.repository.TaiKhoanRepository;
import jakarta.transaction.Transactional;
// Bỏ import Logger và LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    /**
     * Tìm RefreshToken dựa vào chuỗi token.
     * @param token Chuỗi refresh token.
     * @return Optional<RefreshToken>
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Tạo mới một RefreshToken cho người dùng.
     * @param maTK Mã tài khoản của người dùng.
     * @return Đối tượng RefreshToken mới đã được lưu.
     * @throws RuntimeException nếu không tìm thấy TaiKhoan với maTK cung cấp.
     */
    @Transactional
    public RefreshToken createRefreshToken(String maTK) {
        // System.out.println("Đang tạo refresh token cho MaTK: " + maTK); // Ví dụ thay thế log
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy tài khoản với mã " + maTK)); // Ném lỗi chuẩn

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTaiKhoan(taiKhoan);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        // System.out.println("Đã tạo refresh token mới thành công cho MaTK: " + maTK); // Ví dụ thay thế log
        return refreshToken;
    }

    /**
     * Kiểm tra xem RefreshToken có còn hạn hay không.
     * Nếu hết hạn, xóa token khỏi DB và trả về Optional rỗng.
     * @param token Đối tượng RefreshToken cần kiểm tra.
     * @return Optional chứa RefreshToken nếu còn hạn, ngược lại trả về Optional rỗng.
     */
    public Optional<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            // System.out.println("Refresh token đã hết hạn, đang xóa: " + token.getToken()); // Ví dụ thay thế log
            refreshTokenRepository.delete(token); // Xóa token hết hạn
            return Optional.empty(); // Trả về rỗng để báo hiệu token không hợp lệ/hết hạn
        }
        return Optional.of(token); // Trả về token nếu còn hạn
    }

    /**
     * Vô hiệu hóa (xóa) một RefreshToken cụ thể.
     * Được sử dụng trong quá trình rotation sau khi token đã được dùng thành công.
     * @param token Đối tượng RefreshToken cần xóa/vô hiệu hóa.
     */
    @Transactional
    public void deleteRefreshToken(RefreshToken token) {
        // System.out.println("Đang xóa/vô hiệu hóa refresh token: " + token.getToken()); // Ví dụ thay thế log
        refreshTokenRepository.delete(token);
    }


    /**
     * Xóa tất cả RefreshToken của một người dùng (ví dụ: khi logout).
     * @param maTK Mã tài khoản của người dùng.
     * @return Số lượng token đã xóa.
     * @throws RuntimeException nếu không tìm thấy TaiKhoan với maTK cung cấp.
     */
    @Transactional
    public int deleteAllTokensByUserId(String maTK) {
        // System.out.println("Đang xóa tất cả refresh token cho MaTK: " + maTK); // Ví dụ thay thế log
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTK)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy tài khoản với mã " + maTK)); // Ném lỗi chuẩn
        int deletedCount = refreshTokenRepository.deleteByTaiKhoan(taiKhoan);
        // System.out.println("Đã xóa " + deletedCount + " refresh token cho MaTK: " + maTK); // Ví dụ thay thế log
        return deletedCount;
    }


    /**
     * (Tùy chọn) Xóa định kỳ các refresh token đã hết hạn trong database.
     */
    @Scheduled(fixedRate = 86400000) // Chạy mỗi ngày
    @Transactional
    public void deleteExpiredRefreshTokens() {
        Instant now = Instant.now();
        // Giả sử bạn có phương thức deleteByExpiryDateBefore trong Repository
        int deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(now);
        if (deletedCount > 0) {
            System.out.println("Đã xóa định kỳ " + deletedCount + " refresh token hết hạn."); // Thay thế log
        }
    }
}