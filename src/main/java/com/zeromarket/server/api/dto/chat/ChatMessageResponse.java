package com.zeromarket.server.api.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zeromarket.server.common.entity.ChatMessage;
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
public class ChatMessageResponse {

    private Long messageId;
    private Long chatRoomId;
    private Long memberId;
    private String content;
    private MessageType messageType; /* enum */
    @JsonProperty("isRead")
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @JsonProperty("isMine")
    private Boolean isMine;

    public static ChatMessageResponse fromEntity(ChatMessage message) {
        return ChatMessageResponse.builder()
            .messageId(message.getMessageId())
            .chatRoomId(message.getChatRoomId())
            .memberId(message.getMemberId())
            .content(message.getContent())
            .messageType(message.getMessageType())
            .isRead(false)
            .createdAt(message.getCreatedAt())
            .updatedAt(message.getUpdatedAt())
            .isMine(null)
            .build();
    }

}