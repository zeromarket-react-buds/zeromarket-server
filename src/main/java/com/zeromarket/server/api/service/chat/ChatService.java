package com.zeromarket.server.api.service.chat;

import com.zeromarket.server.api.dto.chat.ChatDto;
import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageRequest;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRecentMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRoomResponse;
import com.zeromarket.server.common.entity.ChatMessage;
import java.util.List;

public interface ChatService {

    ChatMessage selectChatMessageByMessageId(Long messageId);

    Long selectChatRoomByProductIdBuyerId(Long productId, Long buyerId);

    ChatInfoWithMessageResponse selectChatInfoWithMessages(Long chatRoomId, Long memberId);

    List<ChatMessageResponse> selectChatMessages(Long chatRoomId, Long memberId);

    List<ChatRoomResponse> selectRecentChatMessages(Long memberId);

//    void sendMessage(ChatMessageRequest message);

    void publish(ChatDto.ChatMessageReq req);
//    void sendMessage(ChatDto.ChatMessageReq req);

    void markAsRead(Long chatRoomId, Long memberId, Long lastReadMessageId);
}

