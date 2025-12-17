package com.zeromarket.server.api.service.chat;

import com.zeromarket.server.api.dto.chat.ChatDto;

public interface ChatDispatchService {

    ChatDto.ChatMessagePush persistAndPush(ChatDto.ChatMessageRes msg);
}
