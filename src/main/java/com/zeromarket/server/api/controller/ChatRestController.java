package com.zeromarket.server.api.controller;

import com.zeromarket.server.api.dto.BoardResponse;
import com.zeromarket.server.api.dto.ChatMessageResponse;
import com.zeromarket.server.api.dto.ChatRoomRequest;
import com.zeromarket.server.api.dto.ChatRoomResponse;
import com.zeromarket.server.api.service.ChatService;
import com.zeromarket.server.common.entity.ChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
@Tag(name = "Chat API", description = "채팅 API")
public class ChatRestController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 번호 조회", description = "상품 ID로 채팅방 번호 조회")
    @GetMapping("/room")
    public ResponseEntity<Long> getChatRoomId(@RequestParam Long productId) {
        Long memberId = 18L; // TODO: SecurityContext에서 받아와야 됨.
        // TODO: memberId없으면 예외처리
        Long chatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, memberId);

        return ResponseEntity.ok(chatRoomId);
    }

    @Operation(summary = "채팅방 조회", description = "상품 ID로 채팅방 조회")
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable Long chatRoomId) {
        Long memberId = 18L; // TODO: SecurityContext에서 받아와야 됨.
        // TODO: memberId없으면 예외처리
        List<ChatMessageResponse> chatMessages = chatService.selectChatMessages(chatRoomId, memberId);

        return ResponseEntity.ok(chatMessages);
    }
}
