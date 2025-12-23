package com.zeromarket.server.api.dto.noti;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPush {
    private String notificationType; // "CHAT_MESSAGE"
    private String refType;          // "CHAT_ROOM"
    private Long refId;              // chatRoomId
    private String body;             // preview
    private String linkUrl;          // "/chat/rooms/{id}"
    private String createdAt;
    private List<Long> receiverIds;  // 누구에게 보낼지
}
