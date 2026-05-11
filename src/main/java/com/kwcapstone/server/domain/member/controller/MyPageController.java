package com.kwcapstone.server.domain.member.controller;

import com.kwcapstone.server.domain.member.dto.request.UpdateNicknameReqDTO;
import com.kwcapstone.server.domain.member.dto.response.MyPageMeResDTO;
import com.kwcapstone.server.domain.member.dto.response.UpdateNicknameResDTO;
import com.kwcapstone.server.domain.member.service.MyPageService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/me")
    public ApiResponse<MyPageMeResDTO> getMyInfo() {
        MyPageMeResDTO result = myPageService.getMyInfo();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @PatchMapping("/me/nickname")
    public ApiResponse<UpdateNicknameResDTO> updateNickname(
            @RequestBody @Valid UpdateNicknameReqDTO request
    ) {
        UpdateNicknameResDTO result = myPageService.updateMyNickname(request.getNickname());

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
