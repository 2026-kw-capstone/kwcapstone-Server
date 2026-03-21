package com.kwcapstone.server.global.security.jwt;

import com.kwcapstone.server.global.security.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 요청마다 JWT 검사(인증)
    private final JwtProvider jwtProvider;

    /**
     * 요청이 들어올 때마다 실행되는 로직
     * 1. Authorization 헤더 확인
     * 2. Bearer 토큰 추출
     * 3. JWT 검증
     * 4. 사용자 정보 추출
     * 5. Authentication 생성
     * 6. SecurityContext 저장
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtProvider.validateAccessToken(token)) {
                Long memberId = jwtProvider.getMemberId(token);
                String email = jwtProvider.getEmail(token);

                AuthPrincipal principal = new AuthPrincipal(memberId, email);

                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
