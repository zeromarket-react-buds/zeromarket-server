package com.zeromarket.server.api.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MemberAddressDto {
    private String name;
    private Long addressId;
    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String addrBase;
    private String addrDetail;
    @JsonProperty("isDefault")
    private boolean isDefault;
}
