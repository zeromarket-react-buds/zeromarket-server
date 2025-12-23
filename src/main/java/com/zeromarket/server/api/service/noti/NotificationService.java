package com.zeromarket.server.api.service.noti;

import com.zeromarket.server.api.dto.noti.NotificationDto;
import java.util.List;

public interface NotificationService {

    void upsertChatNotification(Long receiverId, Long chatRoomId, String body);

    List<NotificationDto> getMyNotifications(Long memberId, int size);

    int getUnreadCount(Long memberId);

    void markAsRead(Long memberId, Long notificationId);

    void markChatRoomAsRead(Long memberId, Long chatRoomId);
}