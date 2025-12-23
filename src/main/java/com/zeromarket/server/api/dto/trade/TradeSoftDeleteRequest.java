package com.zeromarket.server.api.dto.trade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeSoftDeleteRequest {

    @NotBlank
    @Pattern(regexp = "SELLER|BUYER")
    private String deletedBy;
    
}

