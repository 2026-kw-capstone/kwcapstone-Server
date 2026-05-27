package com.kwcapstone.server.domain.conversation.controller;

import com.kwcapstone.server.domain.conversation.dto.request.ConversationTitleUpdateReqDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationTitleUpdateResDTO;
import com.kwcapstone.server.domain.conversation.service.ConversationCommandService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationCommandController {
    private final ConversationCommandService conversationCommandService;

    @Operation(summary = "대화 이름 수정")
    @PatchMapping("/{conversationId}")
    public ApiResponse<ConversationTitleUpdateResDTO> updateConversationTitle(
            @PathVariable Long conversationId,
            @RequestBody @Valid ConversationTitleUpdateReqDTO request
    ) {
        ConversationTitleUpdateResDTO result =
                conversationCommandService.updateConversationTitle(conversationId, request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "대화 삭제")
    @DeleteMapping("/{conversationId}")
    public ApiResponse<Void> deleteConversation(
            @PathVariable Long conversationId
    ) {
        conversationCommandService.deleteConversation(conversationId);

        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }
}
