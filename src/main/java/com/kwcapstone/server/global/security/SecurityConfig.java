package com.kwcapstone.server.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.jwt.JwtAuthenticationFilter;
import com.kwcapstone.server.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // 로그인이 안 된 상태에서 보호된 API 호출 시
        AuthenticationEntryPoint entryPoint = (request, response, authException) -> {
            response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), ApiResponse.onFailure(null, ErrorCode.UNAUTHORIZED));
        };

        // 로그인은 되어 있지만 권한이 없는 경우
        AccessDeniedHandler deniedHandler = (request, response, accessDeniedException) -> {
            response.setStatus(ErrorCode.FORBIDDEN.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), ApiResponse.onFailure(null, ErrorCode.FORBIDDEN));
        };

        http
                // 기존 보안 기능 비활성화
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // CORS 설정 - CorsConfig 클래스 사용
                .cors(Customizer.withDefaults())

                // URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated() // 로그아웃은 인증 필요

                        // PUBLIC
                        .requestMatchers("/auth/**").permitAll()

                        // PROTECTED
                        .requestMatchers("/api/messages/**").authenticated()
                        .requestMatchers("/api/conversations/**").authenticated()

                        // 나머지
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(deniedHandler)
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
