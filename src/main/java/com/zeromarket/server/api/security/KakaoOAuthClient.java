package com.zeromarket.server.api.security;

import com.zeromarket.server.api.dto.auth.KakaoTokenResponse;
import com.zeromarket.server.api.dto.auth.KakaoUserInfo;
import com.zeromarket.server.common.enums.ErrorCode;
import com.zeromarket.server.common.exception.ApiException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    @Value("${oauth.kakao.admin-key}")
    String adminKey; // 카카오 unlink에 사용하는 관리자 키

    // 카카오 access token 요청
    public String requestToken(String code) {
        try {
            KakaoTokenResponse response = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("client_id", clientId)
                    .with("client_secret", clientSecret)
                    .with("redirect_uri", redirectUri)
                    .with("code", code))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

            if (response == null || response.getAccessToken() == null) {
                log.error("카카오 토큰 응답이 null입니다.");
                throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
            }

            return response.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("카카오 토큰 요청 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 401) {
                throw new ApiException(ErrorCode.INVALID_AUTHORIZATION_CODE);
            }
            throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        }
    }

    // redirectUri를 덮어써서 토큰 요청
    public String requestToken(String code, String redirectUriOverride) {
        // 안전을 위해 사전에 허용한 redirectUri만 사용
        String effectiveRedirect = redirectUri;
        if (redirectUriOverride != null && !redirectUriOverride.isBlank()) {
            if (!redirectUriOverride.equals(redirectUri)) {
                throw new ApiException(ErrorCode.INVALID_REQUEST);
            }
            effectiveRedirect = redirectUriOverride;
        }

        try {
            KakaoTokenResponse response = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("client_id", clientId)
                    .with("client_secret", clientSecret)
                    .with("redirect_uri", effectiveRedirect)
                    .with("code", code))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

            if (response == null || response.getAccessToken() == null) {
                log.error("카카오 토큰 응답이 null입니다.");
                throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
            }

            return response.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("카카오 토큰 요청 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 401) {
                throw new ApiException(ErrorCode.INVALID_AUTHORIZATION_CODE);
            }
            throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        }
    }

    // 카카오 사용자 정보 조회
    public KakaoUserInfo requestUserInfo(String accessToken) {
        try {
            KakaoUserInfo userInfo = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();

            if (userInfo == null) {
                log.error("카카오 사용자 정보 응답이 null입니다.");
                throw new ApiException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
            }

            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("카카오 사용자 정보 요청 실패: status={}, body={}",
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);

        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 중 예상치 못한 오류", e);
            throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
        }
    }

    // 카카오 계정 연결 해제
    public void unlinkWithAdminKey(String kakaoUserId) {

        webClient.post()
            .uri("https://kapi.kakao.com/v1/user/unlink")
            .header("Authorization", "KakaoAK " + adminKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromFormData("target_id_type", "user_id")
                .with("target_id", kakaoUserId))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                response.bodyToMono(String.class).flatMap(body -> {
                    log.error("카카오 계정 해제 실패. Status: {}, Response Body: {}", response.statusCode().value(), body);
                    throw new ApiException(ErrorCode.KAKAO_UNLINK_FAILED);
                }))
            .bodyToMono(Map.class)
            .block();
    }
}
