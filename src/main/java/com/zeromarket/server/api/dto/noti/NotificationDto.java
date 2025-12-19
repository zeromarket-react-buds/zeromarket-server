package com.zeromarket.server.api.dto.noti;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long notificationId;
    private String notificationType;
    private String refType;
    private Long refId;
    private String body;
    private String linkUrl;
    private Boolean isRead;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
}
