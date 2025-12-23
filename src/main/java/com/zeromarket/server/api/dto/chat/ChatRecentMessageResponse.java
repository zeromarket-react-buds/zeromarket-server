package com.zeromarket.server.api.dto.chat;

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
public class ChatRecentMessageResponse {

    private Long chatRoomId;
    private Long productId;
    private String productImage;
    private Long buyerId;
    private Long sellerId;
    private String yourNickname;
    private Long messageId;
    private String content;
    private LocalDateTime createdAt;
}
