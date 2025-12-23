package com.zeromarket.server.api.dto.trade;

import com.zeromarket.server.common.enums.TradeStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TradeHistoryRequest {

    private Long tradeId;

    private Long memberId;
    private String role;

    private String keyword;

    private List<String> status;          // 원본 문자열
    private List<TradeStatus> tradeStatus;   // 서비스에서 가공해서 채워넣을 enum값
    private Boolean isHidden;        // 서비스에서 가공해서 채워넣을 isHidden값

    private LocalDate fromDate;
    private LocalDate toDate;
}
