package com.example.QuanLyPhongMayBackEnd.security;

import com.example.QuanLyPhongMayBackEnd.entity.TaiKhoan;
import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import com.example.QuanLyPhongMayBackEnd.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {
    private final TokenRepository tokenRepository;
    // Tạo key sử dụng với thuật toán HS512
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtil(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Lazy

    // Tạo token
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    // Xác thực token
    public boolean validateToken(String token) {
        if (!isTokenInDatabase(token)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isTokenInDatabase(String token) {
        Optional<Token> existingToken = tokenRepository.findByToken(token);
        return existingToken.isPresent() && existingToken.get().getExpiresAt().isAfter(LocalDateTime.now());
    }
    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public long getExpiration() {
        return expiration;
    }
    public Long getMaTKFromToken(String token) {
        // Kiểm tra token có hợp lệ không
        if (validateToken(token)) {
            // Lấy username từ token
            String username = getUsernameFromToken(token);

            // Tìm tài khoản theo username
            Optional<TaiKhoan> taiKhoanOptional = taiKhoanService.timTaiKhoanByUsername(username);

            // Kiểm tra xem tài khoản có tồn tại không
            if (taiKhoanOptional.isPresent()) {
                // Nếu có, trả về maTK của tài khoản
                return taiKhoanOptional.get().getMaTK();
            } else {
                // Nếu không tìm thấy tài khoản
                throw new RuntimeException("Tài khoản không tồn tại trong hệ thống.");
            }
        } else {
            // Nếu token không hợp lệ
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn.");
        }
    }
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key) // Sử dụng key đã có
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration(); // Lấy trường 'exp' dưới dạng Date
        } catch (Exception e) {
            // Xử lý nếu token không hợp lệ hoặc không parse được
            System.err.println("Could not get expiration date from token: " + e.getMessage());
            return null; // Hoặc throw exception tùy logic của bạn
        }
    }


}