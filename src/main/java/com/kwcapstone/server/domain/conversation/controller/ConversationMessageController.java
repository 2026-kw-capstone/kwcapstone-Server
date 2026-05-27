package com.kwcapstone.server.domain.conversation.controller;

import com.kwcapstone.server.domain.conversation.dto.request.TextMessageSendReqDTO;
import com.kwcapstone.server.domain.conversation.dto.request.VoiceMessageSendReqDTO;
import com.kwcapstone.server.domain.conversation.dto.response.MessageSendResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.MessageVoiceUrlResDTO;
import com.kwcapstone.server.domain.conversation.service.ConversationMessageService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class ConversationMessageController {
    private final ConversationMessageService conversationMessageService;

    @Operation(summary = "메시지 전송(텍스트)")
    @PostMapping("/text")
    public ApiResponse<MessageSendResDTO> sendTextMessage(
            @RequestBody @Valid TextMessageSendReqDTO request
    ) {
        MessageSendResDTO result = conversationMessageService.sendTextMessage(request);

        return ApiResponse.onSuccess(result, SuccessCode.CREATED, request.getClientRequestId());
    }

    @Operation(summary = "메시지 전송(오디오)")
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MessageSendResDTO> sendVoiceMessage(
            @ModelAttribute @Valid VoiceMessageSendReqDTO request
    ) {
        MessageSendResDTO result = conversationMessageService.sendVoiceMessage(request);

        return ApiResponse.onSuccess(result, SuccessCode.CREATED, request.getClientRequestId());
    }

    @Operation(summary = "음성 메시지 재생")
    @GetMapping("/{messageId}/voice")
    public ApiResponse<MessageVoiceUrlResDTO> getMessageVoiceUrl(
            @PathVariable Long messageId
    ) {
        MessageVoiceUrlResDTO result = conversationMessageService.getMessageVoiceUrl(messageId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
