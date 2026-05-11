package com.kwcapstone.server.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateNicknameReqDTO {
    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Size(max = 20, message = "닉네임은 20자를 초과할 수 없습니다.")
    private String nickname;
}
