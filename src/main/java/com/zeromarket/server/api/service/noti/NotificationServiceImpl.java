package com.zeromarket.server.api.service.noti;

import com.zeromarket.server.api.dto.noti.NotificationDto;
import com.zeromarket.server.api.dto.noti.NotificationPush;
import com.zeromarket.server.api.mapper.noti.NotificationMapper;
import com.zeromarket.server.api.publisher.NotificationPublisher;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationPublisher notificationPublisher;

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
    public void publish(List<Long> memberIds, NotificationDto baseReq) {
        if (memberIds == null || memberIds.isEmpty()) {
            return;
        }
        if (baseReq == null) {
            throw new IllegalArgumentException("baseReq is null");
        }

        for (Long memberId : memberIds.stream().distinct().toList()) {
            NotificationDto row = baseReq.withMemberId(memberId);
            notificationMapper.insertNotification(row);
        }

        // Push는 한 번만 (여러명에게)
        NotificationPush push = NotificationPush.builder()
            .notificationType(baseReq.getNotificationType())
            .refType(baseReq.getRefType())
            .refId(baseReq.getRefId())
            .body(baseReq.getBody())
            .linkUrl(baseReq.getLinkUrl())
            .createdAt(OffsetDateTime.now().toString())
            .receiverIds(memberIds.stream().distinct().toList())
            .build();

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        notificationPublisher.publish(push);
                    }
                });
        } else {
            notificationPublisher.publish(push);
        }
    }

    private NotificationPush insertNotification(NotificationDto notificationDto) {
        notificationMapper.insertNotification(notificationDto);
        NotificationPush push = new NotificationPush();
        BeanUtils.copyProperties(notificationDto, push);
        return push;
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
    public int markChatRoomAsRead(Long memberId, Long chatRoomId) {
        return notificationMapper.markChatRoomAsRead(memberId, chatRoomId);
    }

    @Override
    public int markReadByRef(NotificationDto req) {
        return notificationMapper.markReadByRef(req);
    }
}
