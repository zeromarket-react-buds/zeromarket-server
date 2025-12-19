package com.zeromarket.server.api.dto.noti;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UnreadCountRes {
    private int count;
}
