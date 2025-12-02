package com.zeromarket.server.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;   // Base64 인코딩된 문자열 (X)

    private SecretKey signingKey;

    private final long ACCESS_EXPIRATION = 1000L * 60 * 30;           // 30분
    private final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    public void init() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ✅ Access Token 생성
    public String generateAccessToken(String loginId, String role) {
        return Jwts.builder()
            .subject(loginId)
            .claim("role", role)
            .claim("type", "ACCESS")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
            .signWith(signingKey)   // SignatureAlgorithm 제거
            .compact();
    }

    // ✅ Refresh Token 생성
    public String generateRefreshToken(String loginId) {
        return Jwts.builder()
            .subject(loginId)
            .claim("type", "REFRESH")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
            .signWith(signingKey)
            .compact();
    }

    // ✅ Access Token 검증
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaims(token); // 검증의 대부분은 이 줄에서 이미 이루어진다
            String type = claims.get("type", String.class);

            if (!"ACCESS".equals(type)) {
                throw new JwtException("Not an access token");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Refresh Token 검증
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            String type = claims.get("type", String.class);

            if (!"REFRESH".equals(type)) {
                throw new JwtException("Not a refresh token");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ JJWT 0.12.5 방식 Claim 파싱
    private Claims getClaims(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        System.out.println("JWT PAYLOAD: " + claims); // JWT PAYLOAD: {type=REFRESH, iat=1763893357, exp=1764498157}

        return claims;
    }

    public String getLoginId(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getType(String token) {
        return getClaims(token).get("type", String.class);
    }
}
