package com.zeromarket.server.api.publisher;

import com.zeromarket.server.api.config.RabbitConfig;
import com.zeromarket.server.api.dto.chat.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqChatPublisher implements ChatPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(ChatDto.ChatMessagePush push) {
        String routingKey = RabbitConfig.ROUTING_KEY_PREFIX + push.getChatRoomId();
        rabbitTemplate.convertAndSend(RabbitConfig.CHAT_EXCHANGE, routingKey, push);
        log.debug("[PUSH:RABBIT] routingKey={}, push={}", routingKey, push);
    }
}