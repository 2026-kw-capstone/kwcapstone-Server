package com.kwcapstone.server.domain.auth.service;

import com.kwcapstone.server.domain.auth.converter.AuthConverter;
import com.kwcapstone.server.domain.auth.dto.request.AuthLoginReqDTO;
import com.kwcapstone.server.domain.auth.dto.request.AuthSignUpReqDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthLoginResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthSignUpResDTO;
import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.exception.code.MemberErrorCode;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    @Override
    public AuthSignUpResDTO signup(AuthSignUpReqDTO request) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Member 생성
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .build();

        memberRepository.save(member);

        return AuthConverter.toSignUpResDTO(member);
    }

    // 로그인
    @Override
    public LoginTokens login(AuthLoginReqDTO request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED)); // TODO: 에러코드 구체화하기

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED); // TODO: 에러코드 구체화하기
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());

        // DB에 저장할 Refresh Token 만료 시각 계산
        LocalDateTime refreshExpiredAt = LocalDateTime.now().plusSeconds(jwtProvider.getRefreshExpireMs() / 1000);

        // Refresh Token DB 저장
        member.updateRefreshToken(refreshToken, refreshExpiredAt);

        // 쿠키 만료 시간용 값 계산
        long refreshMaxAgeSeconds = jwtProvider.getRefreshExpireMs() / 1000;

        AuthLoginResDTO body = AuthConverter.toLoginResDTO(accessToken, member);

        return new LoginTokens(body, refreshToken, refreshMaxAgeSeconds);
    }

    /**
     * Refresh Token을 받아
     * 새로운 Access Token, 새로운 Refresh Token을 발급하는 메서드
     * 현재 구조는 Refresh Token Rotation까지 포함된 구조
     */
    @Override
    public ReissueTokens reissue(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // Refresh Token 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long memberId = jwtProvider.getMemberId(refreshToken);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // DB Refresh Token과 비교
        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // DB Refresh Token 만료 검사
        if (member.getRefreshTokenExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 새로운 Access Token, Refresh Token 발급 (Refresh Token Rotation)
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId(), member.getEmail());

        LocalDateTime newExpiredAt = LocalDateTime.now().plusSeconds(jwtProvider.getRefreshExpireMs() / 1000);

        // DB에 현재 저장된 Refresh Token이 기존 Refresh Token과 같을 때만 교체 (새 Refresh Token으로 교체)
        int updated = memberRepository.rotateRefreshToken(memberId, refreshToken, newRefreshToken, newExpiredAt);

        if (updated == 0) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        long refreshMaxAgeSeconds = jwtProvider.getRefreshExpireMs() / 1000;

        return new ReissueTokens(newAccessToken, newRefreshToken, refreshMaxAgeSeconds);
    }

    @Override
    public void logout() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.clearRefreshToken();
    }
}
