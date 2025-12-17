package com.zeromarket.server.api.controller.chat;

import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.chat.ChatService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatStompController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void send(ChatDto.ChatMessageReq req, Principal principal) {
        // @AuthenticationPrincipal은 STOMP에서 불안정

        if (principal == null) {
            log.warn("[STOMP-IN] principal is null. req={}", req);
            return;
        }

        var auth = (Authentication) principal;
        var user = (CustomUserDetails) auth.getPrincipal();

        req.setMemberId(user.getMemberId());
        log.info("[STOMP-IN] senderId={}, req={}", user.getMemberId(), req);

        chatService.publish(req);
    }

}