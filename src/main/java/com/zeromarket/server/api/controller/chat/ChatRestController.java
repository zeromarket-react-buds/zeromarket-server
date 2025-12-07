package com.zeromarket.server.api.controller.chat;

import com.zeromarket.server.api.dto.chat.ChatInfoWithMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatMessageResponse;
import com.zeromarket.server.api.dto.chat.ChatRecentMessageResponse;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.chat.ChatService;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "채팅방 목록 조회", description = "유저의 채팅방 목록 조회")
    @GetMapping
    public ResponseEntity<List<ChatRecentMessageResponse>> getChatRooms(
        @AuthenticationPrincipal CustomUserDetails userDetail) {
        if (userDetail == null || userDetail.getMemberId() == null || userDetail.getMemberId() <= 0L) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        List<ChatRecentMessageResponse> list = chatService.selectRecentChatMessages(userDetail.getMemberId());

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "채팅방 번호 조회", description = "상품 ID로 채팅방 번호 조회")
    @GetMapping("/room")
    public ResponseEntity<Long> getChatRoomId(@RequestParam Long productId,
        @AuthenticationPrincipal CustomUserDetails userDetail) {
        if (userDetail == null || userDetail.getMemberId() == null || userDetail.getMemberId() <= 0L) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        Long chatRoomId = chatService.selectChatRoomByProductIdBuyerId(productId, userDetail.getMemberId());

        return ResponseEntity.ok(chatRoomId);
    }

    @Operation(summary = "채팅 정보 및 메시지 목록 조회", description = "채팅방 ID로 채팅 정보 및 메시지 목록 조회")
    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatInfoWithMessageResponse> getChatInfoWithMessages(@PathVariable Long chatRoomId, @AuthenticationPrincipal CustomUserDetails userDetail) {
        if (userDetail == null || userDetail.getMemberId() == null || userDetail.getMemberId() <= 0L) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        ChatInfoWithMessageResponse chatInfoWithMessages = chatService.selectChatInfoWithMessages(chatRoomId, userDetail.getMemberId());

        return ResponseEntity.ok(chatInfoWithMessages);
    }

    @Operation(summary = "채팅메시지 조회", description = "채팅방 ID로 채팅메시지 조회")
    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable Long chatRoomId, @AuthenticationPrincipal CustomUserDetails userDetail) {
        if (userDetail == null || userDetail.getMemberId() == null || userDetail.getMemberId() <= 0L) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }
        List<ChatMessageResponse> chatMessages = chatService.selectChatMessages(chatRoomId, userDetail.getMemberId());

        return ResponseEntity.ok(chatMessages);
    }


}
