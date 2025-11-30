package com.zeromarket.server.api.service;

import com.zeromarket.server.api.dto.ChatMessageResponse;
import com.zeromarket.server.api.dto.ChatRoomRequest;
import com.zeromarket.server.common.entity.ChatMessage;
import java.util.List;

public interface ChatService {

    ChatMessage selectChatMessageByMessageId(Long messageId);

    Long selectChatRoomByProductIdBuyerId(Long productId, Long buyerId);

    List<ChatMessageResponse> selectChatMessages(Long chatRoomId, Long memberId);
}

