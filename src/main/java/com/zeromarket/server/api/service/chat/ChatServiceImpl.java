package com.zeromarket.server.api.service.chat;

import com.zeromarket.server.api.config.RabbitConfig;
import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.dto.chat.ChatDto.ChatReadEvent;
import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageRequest;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRecentMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRoomRequest;
import com.zeromarket.server.api.dto.chat.ChatRoomResponse;
import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.mapper.chat.ChatMapper;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.api.publisher.ChatEventPublisher;
import com.zeromarket.server.api.publisher.ChatPublisher;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.entity.ChatRoom;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.MessageType;
import com.zeromarket.server.common.exception.ApiException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatMapper chatMapper;
    private final ProductQueryMapper productQueryMapper;
    private final ChatPublisher chatPublisher;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ChatMessage selectChatMessageByMessageId(Long messageId) {
        return chatMapper.selectChatMessageByMessageId(messageId);
    }

    @Override
    @Transactional
    public Long selectChatRoomByProductIdBuyerId(Long productId, Long buyerId) {
        ProductBasicInfo productBasicInfo
            = productQueryMapper.selectBasicInfo(productId);

        if (productBasicInfo == null) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
            .productId(productId)
            .buyerId(buyerId)
            .sellerId(productBasicInfo.getSellerId())
            .productImage(productBasicInfo.getMainImage())
            .build();

        Long chatRoomId = chatMapper.selectChatRoom(chatRoomRequest);

        if (chatRoomId == null || chatRoomId <= 0) {
            chatRoomId = this.createNewChatRoom(chatRoomRequest);
            // TODO: 임시 처리
//            this.createChatTextMessage(chatRoomId, buyerId, "구매 의사 있어요.");
        }

        return chatRoomId;
    }

    @Override
    public ChatInfoWithMessageResponse selectChatInfoWithMessages(Long chatRoomId, Long memberId) {

        ChatInfoWithMessageResponse chatInfoWithMessageResponse = chatMapper.selectChatInfo(
            chatRoomId);
        chatInfoWithMessageResponse.setChatMessages(selectChatMessages(chatRoomId, memberId));
        chatInfoWithMessageResponse.setYourLastReadMessageId(chatMapper.getLastReadMessageId(chatRoomId, memberId));
        return chatInfoWithMessageResponse;

    }

    @Override
    public List<ChatMessageResponse> selectChatMessages(Long chatRoomId, Long memberId) {

        // 유저의 채팅방이 맞는지 체크
        if (!chatMapper.existsParticipant(chatRoomId, memberId)) {
            throw new ApiException(ErrorCode.CHAT_NOT_FOUND);
        }

        return chatMapper.selectChatMessages(chatRoomId, memberId);
    }

    @Override
    public List<ChatRoomResponse> selectRecentChatMessages(Long memberId) {
        return chatMapper.selectRecentChatMessages(memberId);
    }


    @Override
    public void publish(ChatDto.ChatMessageReq req) {
        // 1) 무조건 DB 저장 먼저 (트랜잭션)
        ChatDto.ChatMessagePush push = this.persist(req);

        // 2) 커밋이 성공한 뒤에만 push 되도록 (권장)
        org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
            new org.springframework.transaction.support.TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    chatPublisher.publish(push);
                }
            }
        );
    }

    private Long createNewChatRoom(ChatRoomRequest chatRoomRequest) {

        ChatRoom chatRoom = ChatRoom.of(chatRoomRequest);

        Long chatRoomId = chatMapper.upsertChatRoomId(chatRoom);

        log.info("생성된 chatRoomId: {}", chatRoomId);

        chatMapper.createChatParticipant(chatRoomId, chatRoomRequest.getBuyerId());
        chatMapper.createChatParticipant(chatRoomId, chatRoomRequest.getSellerId());

        return chatRoomId;
    }

    private void createChatTextMessage(Long chatRoomId, Long memberId, String content) {
        ChatMessage newMessage = ChatMessage.builder().chatRoomId(chatRoomId)
            .memberId(memberId)
            .content(content)
            .messageType(MessageType.TEXT)
            .build();
        chatMapper.createChatMessage(newMessage);

        chatMapper.updateLastMessage(chatRoomId, newMessage.getMessageId());
    }

    @Transactional
    public void markAsRead(Long chatRoomId, Long memberId, Long lastReadMessageId) {
        if (lastReadMessageId == null) return;

        chatMapper.updateLastReadMessage(
            chatRoomId,
            memberId,
            lastReadMessageId
        );

        // 상대에게 읽음 이벤트 전파 (같은 방 구독자들에게 브로드캐스트)
        ChatReadEvent event = ChatReadEvent.builder()
            .chatRoomId(chatRoomId)
            .readerId(memberId)
            .lastReadMessageId(lastReadMessageId)
            .build();
        //  커밋 이후 처리되도록 이벤트만 던짐
        eventPublisher.publishEvent(event);

    }

    @Transactional
    @Override
    public ChatDto.ChatMessagePush persist(ChatDto.ChatMessageReq req) {

        // 1) DB INSERT
        ChatMessage entity = new ChatMessage();
        entity.setChatRoomId(req.getChatRoomId());
        entity.setMemberId(req.getMemberId());
        entity.setMessageType(MessageType.TEXT);
        entity.setContent(req.getContent());

        chatMapper.createChatMessage(entity);

        // 2) chat_room 마지막 메시지 갱신
        chatMapper.updateLastMessage(req.getChatRoomId(), entity.getMessageId());

        // 3) push payload 구성(확정된 messageId 기반)
        ChatDto.ChatMessagePush push = ChatDto.ChatMessagePush.builder()
                .messageId(entity.getMessageId())
                .chatRoomId(req.getChatRoomId())
                .memberId(req.getMemberId())
                .content(req.getContent())
                .createdAt(OffsetDateTime.now().toString())
                .build();

        log.info("[DB:SAVED] room={}, messageId={}", push.getChatRoomId(), push.getMessageId());
        return push;
    }

}
