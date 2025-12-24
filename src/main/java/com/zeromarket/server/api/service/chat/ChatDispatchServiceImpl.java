package com.zeromarket.server.api.service.chat;

import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.mapper.chat.ChatMapper;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.enums.MessageType;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatDispatchServiceImpl implements ChatDispatchService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;

    @Transactional
    @Override
    public ChatDto.ChatMessagePush persistAndPush(ChatDto.ChatMessageRes msg) {

        // 1) DB INSERT
        ChatMessage entity = new ChatMessage();
        entity.setChatRoomId(msg.getChatRoomId());
        entity.setMemberId(msg.getMemberId());
        entity.setMessageType(msg.getMessageType() != null ? msg.getMessageType() : MessageType.TEXT);
        entity.setContent(msg.getContent());

        chatMapper.createChatMessage(entity);

        // 2) chat_room 마지막 메시지 갱신
        chatMapper.updateLastMessage(msg.getChatRoomId(), entity.getMessageId());

        // 3) push payload 구성
        ChatDto.ChatMessagePush push = ChatDto.ChatMessagePush.builder()
                .messageId(entity.getMessageId())
                .chatRoomId(msg.getChatRoomId())
                .memberId(msg.getMemberId())
                .content(msg.getContent())
                .createdAt(OffsetDateTime.now().toString())
                .messageType(entity.getMessageType())
                .build();

        String dest = "/sub/chat/room/" + msg.getChatRoomId();
        messagingTemplate.convertAndSend(dest, push);

        log.info("[DB->PUSH] dest={}, push={}", dest, push);
        return push;
    }
}