package com.zeromarket.server.api.service.noti;

import com.zeromarket.server.api.dto.noti.NotificationDto;
import jakarta.validation.Valid;
import java.util.List;

public interface NotificationService {

    void upsertChatNotification(Long receiverId, Long chatRoomId, String body);

    public void publish(List<Long> memberIds, NotificationDto req);

    List<NotificationDto> getMyNotifications(Long memberId, int size);

    int getUnreadCount(Long memberId);

    void markAsRead(Long memberId, Long notificationId);

    int markChatRoomAsRead(Long memberId, Long chatRoomId);

    int markReadByRef(NotificationDto req);
}