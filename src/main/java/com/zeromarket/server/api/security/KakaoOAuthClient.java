package com.zeromarket.server.api.security;

import com.zeromarket.server.api.dto.auth.KakaoTokenResponse;
import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient {

    private final WebClient webClient;

    @Value("${oauth.kakao.key}")
    private String clientId;

    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

//    카카오 access token 받아오기
    public String requestToken(String code) {
        return webClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                .with("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("redirect_uri", redirectUri)
                .with("code", code))
            .retrieve()
            .bodyToMono(KakaoTokenResponse.class)
            .block()
            .getAccessToken();
    }

//    사용자 정보 조회
    public KakaoUserInfo requestUserInfo(String accessToken) {
        String authorizationHeader = "Bearer " + accessToken;
//        log.info("Authorization Header Value: {}", authorizationHeader);

        return webClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(KakaoUserInfo.class)
            .block();
    }
}

