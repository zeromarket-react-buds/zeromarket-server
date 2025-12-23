package com.zeromarket.server.common.entity;

import com.zeromarket.server.api.dto.chat.ChatRoomRequest;
import com.zeromarket.server.api.dto.chat.ChatRoomResponse;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom {

    private Long chatRoomId;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private String productImage;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long lastMessageId;

    public static ChatRoom of(ChatRoomRequest request) {
        return ChatRoom.builder()
            .productId(request.getProductId())
            .buyerId(request.getBuyerId())
            .sellerId(request.getSellerId())
            .productImage(request.getProductImage())
            .build();
    }

}
