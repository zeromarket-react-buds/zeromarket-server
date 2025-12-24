package com.zeromarket.server.api.dto.noti;

import com.zeromarket.server.common.enums.NotificationRefType;
import com.zeromarket.server.common.enums.NotificationType;
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
    private NotificationType notificationType;
    private NotificationRefType refType;
    private Long refId;
    private String body;
    private String linkUrl;
    private Boolean isRead;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
    private Long memberId;

    public static NotificationDto builderByProductCreate(Long productId, String body) {
        return NotificationDto.builder()
            .notificationType(NotificationType.KEYWORD_MATCH)
            .refType(NotificationRefType.PRODUCT)
            .refId(productId)
            .body(body)
            .linkUrl("/products/" + productId)
            .build();
    }

    public NotificationDto withMemberId(Long memberId) {
        return NotificationDto.builder()
            .memberId(memberId)
            .notificationType(this.notificationType)
            .refType(this.refType)
            .refId(this.refId)
            .body(this.body)
            .linkUrl(this.linkUrl)
            .isRead(false)
            .build();
    }

}
