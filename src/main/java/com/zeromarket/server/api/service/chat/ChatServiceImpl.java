package com.zeromarket.server.api.service.chat;

import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRoomRequest;
import com.zeromarket.server.api.dto.product.ProductBasicInfo;
import com.zeromarket.server.api.mapper.chat.ChatMapper;
import com.zeromarket.server.api.mapper.product.ProductQueryMapper;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.entity.ChatRoom;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.enums.MessageType;
import com.zeromarket.server.common.exception.ApiException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService{

    private final ChatMapper chatMapper;
    private final ProductQueryMapper productQueryMapper;

    @Override
    public ChatMessage selectChatMessageByMessageId(Long messageId) {
        return chatMapper.selectChatMessageByMessageId(messageId);
    }

    @Override
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
            this.createChatTextMessage(chatRoomId, buyerId, "구매 의사 있어요.");
        }

        return chatRoomId;
    }

    @Override
    public ChatInfoWithMessageResponse selectChatInfoWithMessages(Long chatRoomId, Long memberId) {

        ChatInfoWithMessageResponse chatInfoWithMessageResponse = chatMapper.selectChatInfo(chatRoomId);
        chatInfoWithMessageResponse.setChatMessages(selectChatMessages(chatRoomId, memberId));

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

    private Long createNewChatRoom(ChatRoomRequest chatRoomRequest) {

        ChatRoom chatRoom = ChatRoom.of(chatRoomRequest);
        chatMapper.createChatRoom(chatRoom);
        Long chatRoomId = chatRoom.getChatRoomId();

        log.info("생성된 chatRoomId: {}", chatRoomId);

        chatMapper.createChatParticipant(chatRoomId, chatRoomRequest.getBuyerId());
        chatMapper.createChatParticipant(chatRoomId, chatRoomRequest.getSellerId());

        return chatRoomId;
    }

    private void createChatTextMessage(Long chatRoomId, Long memberId, String content) {

        chatMapper.createChatMessage(ChatMessage.builder().chatRoomId(chatRoomId)
            .memberId(memberId)
            .content(content)
            .messageType(MessageType.TEXT)
            .build());
    }


}
