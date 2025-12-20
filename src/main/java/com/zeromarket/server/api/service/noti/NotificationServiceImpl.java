package com.zeromarket.server.api.service.noti;

import com.zeromarket.server.api.dto.noti.NotificationDto;
import com.zeromarket.server.api.mapper.noti.NotificationMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void upsertChatNotification(Long receiverId, Long chatRoomId, String body) {
        notificationMapper.upsertChatNotification(
            receiverId,
            chatRoomId,
            body,
            "/chats/" + chatRoomId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getMyNotifications(Long memberId, int size) {
        return notificationMapper.findNotificationsByMember(memberId, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return notificationMapper.countUnreadByMember(memberId);
    }

    @Override
    public void markAsRead(Long memberId, Long notificationId) {
        notificationMapper.markAsRead(memberId, notificationId);
    }

    @Override
    public void markChatRoomAsRead(Long memberId, Long chatRoomId) {
        notificationMapper.markChatRoomAsRead(memberId, chatRoomId);
    }
}
