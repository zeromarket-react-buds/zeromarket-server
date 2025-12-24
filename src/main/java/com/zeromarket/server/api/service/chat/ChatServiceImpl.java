package com.zeromarket.server.api.service.chat;

import com.zeromarket.server.api.config.RabbitConfig;
import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.dto.chat.ChatDto.ChatReadEvent;
import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageRequest;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatPersistResult;
import com.zeromarket.server.api.dto.chat.ChatRecentMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRoomRequest;
import com.zeromarket.server.api.dto.chat.ChatRoomResponse;
import com.zeromarket.server.api.dto.noti.NotificationPush;
import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.mapper.chat.ChatMapper;
import com.zeromarket.server.api.mapper.noti.NotificationMapper;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.api.publisher.ChatEventPublisher;
import com.zeromarket.server.api.publisher.ChatPublisher;
import com.zeromarket.server.api.publisher.NotificationPublisher;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.entity.ChatRoom;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.MessageType;
import com.zeromarket.server.common.enums.NotificationRefType;
import com.zeromarket.server.common.enums.NotificationType;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatMapper chatMapper;
    private final ProductQueryMapper productQueryMapper;
    private final ChatPublisher chatPublisher;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationMapper notificationMapper;
    private final NotificationPublisher notificationPublisher;

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
        }

        return chatRoomId;
    }

    @Override
    public ChatInfoWithMessageResponse selectChatInfoWithMessages(Long chatRoomId, Long memberId) {

        ChatInfoWithMessageResponse chatInfoWithMessageResponse = chatMapper.selectChatInfo(
            chatRoomId);
        chatInfoWithMessageResponse.setChatMessages(selectChatMessages(chatRoomId, memberId));
        chatInfoWithMessageResponse.setYourLastReadMessageId(
            chatMapper.getLastReadMessageId(chatRoomId, memberId));
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

    private Long createNewChatRoom(ChatRoomRequest chatRoomRequest) {

        ChatRoom chatRoom = ChatRoom.of(chatRoomRequest);

        Long chatRoomId = chatMapper.upsertChatRoomId(chatRoom);

        log.info("생성된 chatRoomId: {}", chatRoomId);

        chatMapper.createChatParticipant(chatRoomId, chatRoomRequest.getBuyerId());
        chatMapper.createChatParticipant(chatRoomId, chatRoomRequest.getSellerId());

        return chatRoomId;
    }


    @Transactional
    public void markAsRead(Long chatRoomId, Long memberId, Long lastReadMessageId) {
        if (lastReadMessageId == null) {
            return;
        }

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

    @Override
    public void publish(ChatDto.ChatMessageReq req) {
        ChatPersistResult result = this.persist(req);

        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    chatPublisher.publish(result.getChatPush());
                    notificationPublisher.publish(result.getNotificationPush());
                }
            }
        );
    }

    @Transactional
    @Override
    public ChatPersistResult persist(ChatDto.ChatMessageReq req) {

        // 1) 메시지 저장
        ChatMessage entity = new ChatMessage();
        entity.setChatRoomId(req.getChatRoomId());
        entity.setMemberId(req.getMemberId());
        entity.setMessageType(MessageType.TEXT);
        entity.setContent(req.getContent());
        chatMapper.createChatMessage(entity);

        // 2) last_message 갱신
        chatMapper.updateLastMessage(req.getChatRoomId(), entity.getMessageId());

        // 3) 채팅 push DTO
        ChatDto.ChatMessagePush chatPush = ChatDto.ChatMessagePush.builder()
            .messageId(entity.getMessageId())
            .chatRoomId(req.getChatRoomId())
            .memberId(req.getMemberId())
            .content(req.getContent())
            .createdAt(OffsetDateTime.now().toString())
            .build();

        // 4) 수신자 조회
        List<Long> receiverIds = chatMapper.findChatRoomParticipantIdsExceptSender(
            req.getChatRoomId(), req.getMemberId()
        );

        // 5) 알림 upsert (DB)
        String preview = makePreview(req.getContent());
        String linkUrl = "/chats/" + req.getChatRoomId();

        for (Long receiverId : receiverIds) {
            notificationMapper.upsertChatNotification(
                receiverId, req.getChatRoomId(), preview, linkUrl
            );
        }

        // 6) 알림 push DTO (개인 채널)
        NotificationPush notiPush = NotificationPush.builder()
            .notificationType(NotificationType.CHAT_MESSAGE)
            .refType(NotificationRefType.CHAT_ROOM)
            .refId(req.getChatRoomId())
            .body(preview)
            .linkUrl(linkUrl)
            .createdAt(OffsetDateTime.now().toString())
            .receiverIds(receiverIds)
            .build();

        return new ChatPersistResult(chatPush, notiPush);
    }

    private String makePreview(String content) {
        if (content == null) {
            return null;
        }
        String s = content.replace("\n", " ");
        return s.length() > 60 ? s.substring(0, 60) + "..." : s;
    }

}
