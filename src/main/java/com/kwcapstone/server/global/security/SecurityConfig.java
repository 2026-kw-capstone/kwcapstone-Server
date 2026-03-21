package com.kwcapstone.server.global.security;

import com.kwcapstone.server.global.security.jwt.JwtAuthenticationFilter;
import com.kwcapstone.server.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 기존 보안 기능 비활성화
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC
                        .requestMatchers("/auth/**").permitAll()
                        // 나머지
                        .anyRequest().authenticated() // 현재는 JWT 인증 필요
                )

                /**
                 * JWT 인증 필터 등록
                 * UsernamePasswordAuthenticationFilter 이전에 실행
                 * Authorization: Bearer {token} 헤더에서 JWT 추출
                 * 유효한 경우 SecurityContext에 AuthPrincipal 세팅
                 * 즉, 로그인 사용자 등록
                 */
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
