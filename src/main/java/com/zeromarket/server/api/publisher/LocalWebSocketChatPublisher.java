package com.zeromarket.server.api.publisher;

import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.service.chat.ChatDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalWebSocketChatPublisher implements ChatPublisher {

    private final ChatDispatchService chatDispatchService;

    @Override
    public void publish(ChatDto.ChatMessageRes msg) {
        log.debug("[PUBLISH:LOCAL] room={}, member={}", msg.getChatRoomId(), msg.getMemberId());
        chatDispatchService.persistAndPush(msg);
    }
}