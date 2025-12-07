package com.zeromarket.server.api.dto.chat;

import com.zeromarket.server.common.enums.TradeStatus;
import com.zeromarket.server.common.enums.TradeType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class ChatInfoWithMessageResponse {
    private Long chatRoomId;
    private Long productId;
    private String productTitle;
    private Long sellPrice;
    private TradeType tradeType;
    private TradeStatus tradeStatus; // TODO: 따로 객체로 가지고 있을지 고민중
    private Long buyerId;
    private String buyerProfileImage;
    private String buyerNickname;
    private Long sellerId;
    private String sellerProfileImage;
    private String sellerNickname;
    private String productImage;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime buyerDeletedAt;
    private LocalDateTime sellerDeletedAt;
    private List<ChatMessageResponse> chatMessages; // TODO: API 분리할지?
}
