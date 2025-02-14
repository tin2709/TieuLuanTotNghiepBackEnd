package com.example.QuanLyPhongMayBackEnd.security;

import com.example.QuanLyPhongMayBackEnd.entity.Token;
import com.example.QuanLyPhongMayBackEnd.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtil(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

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
}

