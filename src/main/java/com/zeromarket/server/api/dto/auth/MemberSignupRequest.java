package com.zeromarket.server.api.dto.auth;

import lombok.Data;

@Data
public class MemberSignupRequest {

    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String email;
}
