package com.zeromarket.server.common.entity;

import com.zeromarket.server.common.enums.MessageType;
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
public class ChatMessage {

    private Long messageId;
    private Long chatRoomId;
    private Long memberId;
    private String content;
    private MessageType messageType; /* enum */
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
