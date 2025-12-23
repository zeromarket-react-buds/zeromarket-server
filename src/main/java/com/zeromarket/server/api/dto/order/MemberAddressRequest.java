package com.zeromarket.server.api.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter // 추가
@NoArgsConstructor // 추가
public class MemberAddressRequest {
    private String name;
    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String addrBase;
    private String addrDetail;
    @JsonProperty("isDefault")
    private boolean isDefault;
}
