package com.zeromarket.server.api.publisher;

import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.service.chat.ChatDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalWebSocketChatPublisher implements ChatPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publish(ChatDto.ChatMessagePush push) {
        String dest = "/sub/chat/room/" + push.getChatRoomId();
        messagingTemplate.convertAndSend(dest, push);
        log.debug("[PUSH:LOCAL] dest={}, push={}", dest, push);
    }
}