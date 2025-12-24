package com.zeromarket.server.api.dto.noti;

import com.zeromarket.server.common.enums.NotificationRefType;
import com.zeromarket.server.common.enums.NotificationType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationPush {
    private NotificationType notificationType; // "CHAT_MESSAGE"
    private NotificationRefType refType;          // "CHAT_ROOM"
    private Long refId;              // chatRoomId
    private String body;             // preview
    private String linkUrl;          // "/chat/rooms/{id}"
    private String createdAt;
    private List<Long> receiverIds;  // 누구에게 보낼지
}
