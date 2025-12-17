package com.zeromarket.server.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfo {
    private Long id;
    private KakaoAccount kakao_account;

    @Getter @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter @Setter
        public static class Profile {
            private String nickname;
            private String profile_image_url;
            private String thumbnail_image_url;
        }
    }
}

