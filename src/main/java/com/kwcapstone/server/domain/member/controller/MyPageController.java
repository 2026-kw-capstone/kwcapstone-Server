package com.kwcapstone.server.domain.member.controller;

import com.kwcapstone.server.domain.member.dto.response.MyPageMeResDTO;
import com.kwcapstone.server.domain.member.service.MyPageService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
