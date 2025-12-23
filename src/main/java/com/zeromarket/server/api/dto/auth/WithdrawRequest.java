package com.zeromarket.server.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    private Integer withdrawalReasonId;
    private String withdrawalReasonDetail;
}
