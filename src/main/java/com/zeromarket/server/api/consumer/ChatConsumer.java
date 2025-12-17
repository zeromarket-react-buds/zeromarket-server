package com.zeromarket.server.api.consumer;

import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.mapper.chat.ChatMapper;
import com.zeromarket.server.api.service.chat.ChatDispatchService;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.enums.MessageType;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConsumer {

    private ChatDispatchService chatDispatchService;

    @RabbitListener(queues = "chat.queue.server")
    public void onMessage(ChatDto.ChatMessageRes msg) {
        chatDispatchService.persistAndPush(msg);
        log.info("[CONSUME] room={}, member={}", msg.getChatRoomId(), msg.getMemberId());
    }

}
