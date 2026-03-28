package com.kwcapstone.server.domain.conversation.controller;

import com.kwcapstone.server.domain.conversation.dto.response.ConversationDetailResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationListItemResDTO;
import com.kwcapstone.server.domain.conversation.service.ConversationQueryService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationQueryController {
    private final ConversationQueryService conversationQueryService;

    @GetMapping
    public ApiResponse<List<ConversationListItemResDTO>> getConversations() {
        List<ConversationListItemResDTO> result = conversationQueryService.getConversations();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @GetMapping("/{conversationId}")
    public ApiResponse<ConversationDetailResDTO> getConversationDetail(
            @PathVariable Long conversationId
    ) {
        ConversationDetailResDTO result = conversationQueryService.getConversationDetail(conversationId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
