package com.zeromarket.server.api.dto.chat;

import com.zeromarket.server.api.dto.noti.NotificationPush;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatPersistResult {
    private ChatDto.ChatMessagePush chatPush;
    private NotificationPush notificationPush;
}