package com.zeromarket.server.api.service.auth;

import com.zeromarket.server.api.dto.auth.KakaoTokenResponse;
import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

// TODO: KakaoUtil로 바꾸기

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthServiceImpl implements OAuthLoginService {

    private final WebClient webClient;

    @Value("${oauth.kakao.key}")
    private String KAKAO_REST_API_KEY;
    @Value("${oauth.kakao.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${oauth.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    //    카카오 access token 받아오기
    @Override
    public String getAccessToken(String code) {
        KakaoTokenResponse res = webClient.post()
            .uri("https://kauth.kakao.com/oauth/token")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                .with("client_id", KAKAO_REST_API_KEY)
                .with("client_secret", KAKAO_CLIENT_SECRET)
                .with("redirect_uri", REDIRECT_URI)
                .with("code", code))
            .retrieve()
            .bodyToMono(KakaoTokenResponse.class)
            .block();
//            .getAccessToken();

        System.out.println(res.getAccessToken());
        return res.getAccessToken();
    }

//    사용자 정보 조회
    @Override
    public KakaoUserInfo getUserInfo(String accessToken) {
        String authorizationHeader = "Bearer " + accessToken;
        log.info("Authorization Header Value: {}", authorizationHeader); // 로그 추가

        return webClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
            .retrieve()
            .bodyToMono(KakaoUserInfo.class)
            .block();
    }
}
