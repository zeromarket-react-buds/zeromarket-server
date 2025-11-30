package com.zeromarket.server.api.dto;

import com.zeromarket.server.common.enums.TradeStatus;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TradeHistoryRequest {
    private Long memberId;
    private String keyword;
}
