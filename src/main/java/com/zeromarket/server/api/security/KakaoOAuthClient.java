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
    String adminKey; // 환경 변수에서 관리

//    카카오 access token 받아오기
    public String requestToken(String code) {
        // WebClient는 다음 상황에서 자동으로 예외를 던집니다:
        // 1. 4xx 에러 → WebClientResponseException (기본적으로)
        // 2. 5xx 에러 → WebClientResponseException (기본적으로)
        // 3. 네트워크 오류 → WebClientRequestException
        // 4. 타임아웃 → TimeoutException
        // 5. block()에서 null 반환 시 별도 처리 필요
        try {
            KakaoTokenResponse response = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("client_id", clientId)
                    .with("client_secret", clientSecret)
                    .with("redirect_uri", redirectUri)
                    .with("code", code))
                .retrieve() // 여기서 4xx, 5xx 시 자동으로 예외 발생
                .bodyToMono(KakaoTokenResponse.class)
                .block(); // 블로킹 중 에러 발생 가능 (block()이 null을 반환하면 .getAccessToken()에서 NullPointerException 발생)
//                .getAccessToken();

            if (response == null || response.getAccessToken() == null) {
                log.error("카카오 토큰 응답이 null입니다.");
                throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED); // 카카오 토큰 요청에 실패했습니다.
//                throw new KakaoTokenException("Kakao token response is null");
            }

            return response.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("카카오 토큰 요청 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());

            // 401: 유효하지 않은 인증 코드
            if (e.getStatusCode().value() == 401) {
                throw new ApiException(ErrorCode.INVALID_AUTHORIZATION_CODE); // 유효하지 않은 인증 코드입니다.
//                throw new InvalidAuthorizationCodeException("Invalid authorization code", e);
            }
            // 그 외 4xx, 5xx
            throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED); // 카카오 토큰 요청에 실패했습니다.
//            throw new KakaoTokenException("Kakao token request failed: " + e.getMessage(), e);

        }
    }

//    사용자 정보 조회
    public KakaoUserInfo requestUserInfo(String accessToken) {
        try {
            String authorizationHeader = "Bearer " + accessToken;
//        log.info("Authorization Header Value: {}", authorizationHeader);

            KakaoUserInfo userInfo = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();

            if (userInfo == null) {
                log.error("카카오 회원 정보 응답이 null입니다.");
                throw new ApiException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
//                throw new KakaoUserInfoException("Kakao user info response is null");
            }

            return userInfo;

        } catch (WebClientResponseException e) {
            log.error("카카오 회원 정보 요청 실패: status={}, body={}",
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException(ErrorCode.KAKAO_USER_INFO_REQUEST_FAILED);
//            throw new KakaoUserInfoException("Failed to get Kakao user info: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("Unexpected error while requesting Kakao user info", e);
            throw new ApiException(ErrorCode.KAKAO_TOKEN_REQUEST_FAILED);
//            throw new KakaoUserInfoException("Failed to request Kakao user info", e);
        }
    }

//      카카오 연결 해제
    public void unlinkWithAdminKey(String kakaoUserId) {

        webClient.post()
            .uri("https://kapi.kakao.com/v1/user/unlink")
            .header("Authorization", "KakaoAK " + adminKey) // Admin Key 사용
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body(BodyInserters.fromFormData("target_id_type", "user_id")
                .with("target_id", kakaoUserId))
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                // 에러 처리: 연결 끊기 실패 시 ApiException 던지기
                response.bodyToMono(String.class).flatMap(body -> {
                    log.error("카카오 연결 끊기 실패. Status: {}, Response Body: {}", response.statusCode().value(), body);
                    throw new ApiException(ErrorCode.KAKAO_UNLINK_FAILED);
                }))
            .bodyToMono(Map.class) // 응답은 해제된 사용자 ID를 포함한 JSON
            .block();
    }
}

