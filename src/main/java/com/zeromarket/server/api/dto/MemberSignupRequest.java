package com.zeromarket.server.api.dto;

import lombok.Data;

@Data
public class MemberSignupRequest {

    private String loginId;
    private String password;
//    private String name;
//    private String nickname;
}
