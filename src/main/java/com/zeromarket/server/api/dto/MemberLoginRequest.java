package com.zeromarket.server.api.dto;

import lombok.Data;

@Data
public class MemberLoginRequest {

    private String loginId;
    private String password;
}
