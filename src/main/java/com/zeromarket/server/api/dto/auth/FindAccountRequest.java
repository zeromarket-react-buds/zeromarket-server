package com.zeromarket.server.api.dto.auth;

import lombok.Data;

@Data
public class FindAccountRequest {
    private String loginId;
    private String name;
    private String phone;
    private String newPassword;
}
