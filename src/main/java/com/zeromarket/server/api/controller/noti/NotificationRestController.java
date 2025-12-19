package com.zeromarket.server.api.controller.noti;

import com.zeromarket.server.api.dto.noti.NotificationDto;
import com.zeromarket.server.api.dto.noti.UnreadCountRes;
import com.zeromarket.server.api.security.CustomUserDetails;
import com.zeromarket.server.api.service.noti.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@SecurityRequirement(name = "bearer")
public class NotificationRestController {

    private final NotificationService notificationService;

    @Operation(
            summary = "내 알림 목록 조회",
            description = "로그인한 사용자의 알림 목록을 최신순으로 조회합니다. size 만큼만 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료)", content = @Content)
    })
    @GetMapping("/notifications")
    public List<NotificationDto> getMyNotifications(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "가져올 알림 개수 (기본 50)", example = "50")
            @RequestParam(defaultValue = "50") int size
    ) {
        Long memberId = userDetails.getMemberId();
        return notificationService.getMyNotifications(memberId, size);
    }

    @Operation(
            summary = "읽지 않은 알림 개수 조회",
            description = "로그인한 사용자의 읽지 않은 알림 개수를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UnreadCountRes.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료)", content = @Content)
    })
    @GetMapping("/notifications/unread-count")
    public UnreadCountRes getUnreadCount(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        int count = notificationService.getUnreadCount(memberId);
        return UnreadCountRes.builder().count(count).build();
    }

    @Operation(
            summary = "알림 1건 읽음 처리",
            description = "notificationId에 해당하는 알림을 읽음 처리합니다. (본인 알림만 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "처리 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료)", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음(타인 알림 접근)", content = @Content),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/notifications/{notificationId}/read")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void markAsRead(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "읽음 처리할 알림 ID", example = "123")
            @PathVariable Long notificationId
    ) {
        Long memberId = userDetails.getMemberId();
        notificationService.markAsRead(memberId, notificationId);
    }

    @Operation(
            summary = "채팅방 알림 일괄 읽음 처리",
            description = "chatRoomId에 해당하는 채팅방에서 발생한 알림/메시지를 읽음 처리합니다. (본인 채팅방만 가능)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "처리 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패(토큰 누락/만료)", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음(타인 채팅방 접근)", content = @Content),
            @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음", content = @Content)
    })
    @PatchMapping("/chat-rooms/{chatRoomId}/notifications/read")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void markChatRoomAsRead(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "읽음 처리할 채팅방 ID", example = "10")
            @PathVariable Long chatRoomId
    ) {
        Long memberId = userDetails.getMemberId();
        notificationService.markChatRoomAsRead(memberId, chatRoomId);
    }
}
