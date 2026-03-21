package com.kwcapstone.server.domain.member.entity;

import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "member", uniqueConstraints = {@UniqueConstraint(name = "uk_member_email", columnNames = "email")})
public class Member extends BaseEntity {
    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 30)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "refresh_token_expired_at")
    private LocalDateTime refreshTokenExpiredAt;

    public void updateRefreshToken(String token, LocalDateTime expiredAt) {
        this.refreshToken = token;
        this.refreshTokenExpiredAt = expiredAt;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiredAt = null;
    }
}
