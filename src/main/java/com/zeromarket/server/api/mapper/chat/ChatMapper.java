package com.zeromarket.server.api.mapper.chat;

import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRoomRequest;
import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.entity.ChatRoom;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMapper {

    ChatMessage selectChatMessageByMessageId(Long messageId);

    Long selectChatRoom(ChatRoomRequest chatRoomRequest);

    void createChatRoom(ChatRoom chatRoom);

    void createChatParticipant(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long sellerId);

    void createChatMessage(ChatMessage chatMessage);

    int countChatParticipants(@Param("chatRoomId") Long chatRoomId);

    List<ChatMessageResponse> selectChatMessages(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    boolean existsParticipant(@Param("chatRoomId") Long chatRoomId, @Param("memberId")  Long memberId);

    ChatInfoWithMessageResponse selectChatInfo(Long chatRoomId);
}
