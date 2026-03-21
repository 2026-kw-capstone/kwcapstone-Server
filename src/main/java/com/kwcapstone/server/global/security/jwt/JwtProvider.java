package com.kwcapstone.server.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey secretKey; // 암호화 키
    private final Long accessExpireMs; // Access Token 만료 시간
    private final Long refreshExpireMs; // Refresh Token 만료 시간

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expire}") Long accessExpireMs,
            @Value("${jwt.refresh-expire}") Long refreshExpireMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpireMs = accessExpireMs;
        this.refreshExpireMs = refreshExpireMs;
    }

    // 로그인 성공 시 Access Token 생성
    public String createAccessToken(Long memberId, String email) {
        Long now = System.currentTimeMillis(); // 현재 시간

        return Jwts.builder() // JWT 생성 시작
                .claim("memberId", memberId) // JWT payload에 memberId 저장
                .claim("email", email) // JWT payload에 email 저장
                .claim("type", "access") // Token 종류 표시(access)
                .setIssuedAt(new Date(now)) // 토큰 발급 시간(메서드 deprecated)
                .setExpiration(new Date(now + accessExpireMs)) // 토큰 만료 시간(메서드 deprecated)
                .signWith(secretKey) // JWT 서명 생성(Header + Payload -> Signature 생성)
                .compact(); // JWT 문자열 생성
    }

    // Refresh Token 생성
    public String createRefreshToken(Long memberId, String email) {
        Long now = System.currentTimeMillis();

        return Jwts.builder()
                .claim("memberId", memberId)
                .claim("email", email)
                .claim("type", "refresh") // Token 종류 표시(refresh)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpireMs))
                .signWith(secretKey)
                .compact();
    }

    // JWT에서 memberId 추출
    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    // JWT에서 email 추출
    public String getEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public Long getRefreshExpireMs() {
        return refreshExpireMs;
    }

    // Access Token 검증
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);

            // Access Token인지 확인
            return "access".equals(claims.get("type"));
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 서명 검증
                .build()
                .parseSignedClaims(token)
                .getPayload(); // JWT -> Claims
    }
}
