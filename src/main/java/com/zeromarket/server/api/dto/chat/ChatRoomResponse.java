package com.zeromarket.server.api.dto.chat;

import com.zeromarket.server.common.entity.ChatMessage;
import com.zeromarket.server.common.entity.ChatRoom;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class ChatRoomResponse {

    private Long chatRoomId;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private String productImage;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String yourNickname;
    private Long lastMessageId;
    private String lastMessageContent;
    private LocalDateTime lastMessageAt;
    private String lastMessageSenderId;
    private Integer unreadCount;
    private ChatMessage lastChatMessage;

    public static ChatRoomResponse fromEntity(ChatRoom room) {

        return ChatRoomResponse.builder()
            .chatRoomId(room.getChatRoomId())
            .productId(room.getProductId())
            .buyerId(room.getBuyerId())
            .sellerId(room.getSellerId())
            .productImage(room.getProductImage())
            .isDeleted(room.getIsDeleted())
            .createdAt(room.getCreatedAt())
            .updatedAt(room.getUpdatedAt())
            .deletedAt(room.getDeletedAt())
            .lastMessageId(room.getLastMessageId())
            .build();
    }
}
