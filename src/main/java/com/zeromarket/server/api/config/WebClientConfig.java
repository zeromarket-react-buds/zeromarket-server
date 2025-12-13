package com.zeromarket.server.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// 스프링에서 Azure Vision API 같은 외부 HTTP API를 호출하기 위한 도구를
// 공용으로 만들어 주는 설정 파일
// 여기서 만들어지는 객체는 전역에서 재사용
@Configuration
// WebClient 관련 설정만 모아둔 클래스. 기능 로직이 아니라 환경 설정용 클래스
public class WebClientConfig {


    // 이 메서드의 리턴값을 스프링이 관리하는 객체(Bean)로 등록
    // 다른 클래스에서 new로 만들지 않아도 주입 받아 쓰는게 가능
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build(); //WebClient 객체를 하나 생성
    }
}
