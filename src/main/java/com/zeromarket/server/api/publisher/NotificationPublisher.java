package com.zeromarket.server.api.publisher;

import com.zeromarket.server.api.dto.noti.NotificationPush;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publish(NotificationPush push) {
        if (push == null || push.getReceiverIds() == null) return;

        for (Long receiverId : push.getReceiverIds()) {
            String dest = "/sub/notification/" + receiverId;

            // payload는 필요 최소만 보내도 됨 (프론트가 unread-count 재조회)
            messagingTemplate.convertAndSend(dest, push);
        }
    }
}
