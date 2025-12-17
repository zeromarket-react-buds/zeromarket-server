package com.zeromarket.server.api.dto.chat;

import lombok.*;

public class ChatDto {

    @Getter
    @Builder
    public static class ChatReadEvent {

        private Long chatRoomId;
        private Long readerId;
        private Long lastReadMessageId;
    }

    @Getter
    @Setter
    public static class ChatReadReq {
        private Long lastReadMessageId;
    }

    @Getter
    @Builder
    @ToString
    public static class ChatMessagePush {

        private Long messageId;
        private Long chatRoomId;
        private Long memberId;
        private String content;
        private String createdAt;
        private Boolean isMine; // 내가 보낸 메시지인지 여부
    }

    @Getter
    @Setter
    @ToString
    public static class ChatSendReq {

        private Long chatRoomId;
        private String content;
        // (임시) private Long memberId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ChatMessageReq {

        private Long chatRoomId;   // roomId
        private Long memberId;     // sender
        private String content;    // message text
    }

    @Getter
    @Builder
    @ToString
    public static class ChatMessageRes {

        private Long chatRoomId;
        private Long memberId;
        private String content;
        private String sentAt; // ISO string

        public static ChatMessageRes from(ChatMessageReq req) {
            return ChatMessageRes.builder()
                .chatRoomId(req.getChatRoomId())
                .memberId(req.getMemberId())
                .content(req.getContent())
                .sentAt(java.time.OffsetDateTime.now().toString())
                .build();
        }
    }
}
