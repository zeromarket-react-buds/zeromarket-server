package com.zeromarket.server.api.dto.mypage;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesProductRequest {
    private Long sellerId;
    private Long cursorProductId;     // 다음 페이지 탐색 Key
    private LocalDateTime cursorCreatedAt;
    private Long loginMemberId;       // 찜 여부 확인용
    private int size = 10;
    private boolean includeHidden;
}
