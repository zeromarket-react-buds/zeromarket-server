package com.zeromarket.server.api.mapper.chat;

import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRecentMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRoomRequest;
import com.zeromarket.server.api.dto.chat.ChatRoomResponse;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.entity.ChatRoom;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMapper {

    //    List<ChatRoomResponse> selectChatRooms();
    List<ChatRoomResponse> selectRecentChatMessages(@Param("memberId") Long memberId);

    ChatRoom selectChatRoomByChatRoomId(Long chatRoomId);

    ChatMessage selectChatMessageByMessageId(Long messageId);

    Long selectChatRoom(ChatRoomRequest chatRoomRequest);

    void createChatRoom(ChatRoom chatRoom);

    void createChatParticipant(@Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long sellerId);

    void createChatMessage(ChatMessage chatMessage);

    int updateLastMessage(@Param("chatRoomId") Long chatRoomId,
        @Param("messageId") Long messageId);

    Integer selectUnreadMessageCount(@Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long memberId);

    int countChatParticipants(@Param("chatRoomId") Long chatRoomId);

    List<ChatMessageResponse> selectChatMessages(@Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long memberId);

    boolean existsParticipant(@Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long memberId);

    ChatInfoWithMessageResponse selectChatInfo(Long chatRoomId);

    Long upsertChatRoomId(ChatRoom chatRoom);

    void updateLastReadMessage(
        @Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long memberId,
        @Param("lastReadMessageId") Long lastReadMessageId
    );

    Long getLastReadMessageId(@Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long memberId);

    // 채팅방 참여자 ID 조회 (보낸 사람 제외)
    List<Long> findChatRoomParticipantIdsExceptSender(
        @Param("chatRoomId") Long chatRoomId,
        @Param("memberId") Long memberId
    );

    List<Long> getChatRoomIdsByProductId(Long productId);
//    List<ChatRecentMessageResponse> selectRecentChatMessages(@Param("memberId")  Long memberId);
}
