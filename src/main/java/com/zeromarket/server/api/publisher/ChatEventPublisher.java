package com.zeromarket.server.api.publisher;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.zeromarket.server.api.dto.chat.ChatDto.ChatReadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onChatReadUpdated(ChatReadEvent event) {

        messagingTemplate.convertAndSend(
            "/sub/chat/room/" + event.getChatRoomId() + "/read",
            event
        );
    }
}
