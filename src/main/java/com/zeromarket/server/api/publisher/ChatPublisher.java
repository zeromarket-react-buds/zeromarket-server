package com.zeromarket.server.api.publisher;

import com.zeromarket.server.api.dto.chat.ChatDto;

public interface ChatPublisher {
    void publish(ChatDto.ChatMessageRes msg);
}
