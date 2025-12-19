package com.zeromarket.server.api.service.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeromarket.server.api.dto.product.ProductEnvScoreRequest;
import com.zeromarket.server.api.dto.product.ProductEnvScoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EnvScoreService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // application.yml에 있는 키값 읽어옴
    @Value("${azure.openai.api-key}")
    private String apiKey;

    @Value("${azure.openai.deployment}")
    private String deployment;

    @Value("${azure.openai.api-version}")
    private String apiVersion;

    // WebClient를 생성하고 endpoint를 설정하는 생성자
    public EnvScoreService(
        WebClient.Builder builder,
        @Value("${azure.openai.endpoint}") String endpoint
    ) {
        // endpoint 끝에 / 가 있으면 제거해서 주소가 두 번 슬래시로 이어지는 문제 막음
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        // base 주소를 기본 URL로 쓰는 WebClient를 만들어 저장
        this.webClient = builder.baseUrl(base).build();
    }

    // Vision 결과(caption, tags)를 기반으로 환경점수 계산
    public ProductEnvScoreResponse calculate(ProductEnvScoreRequest req) {
        try {
            String caption = req.getCaption() == null ? "" : req.getCaption().trim();
            List<String> tags = req.getTags() == null ? List.of() : req.getTags();

            String prompt = buildEnvPrompt(caption, tags); // Vision 결과(caption, tags)를 기반으로 AI에 전달할 프롬프트 생성

            // Azure OpenAI에 보낼 요청 바디(JSON)를 만들기 위한 Map
            Map<String, Object> body = new HashMap<>();
            body.put("messages", List.of(
                // AI에게 규칙을 강제하는 시스템 지시문
                Map.of("role", "system", "content",
                    "너는 중고거래 앱에 등록되는 상품을 환경 점수 값을 추정하는 계산기다. "
                        + "ai_tags, ai_caption을 참고해 환경 점수를 추측하면 되는데 기준은 해당 제품이 만들어질때 탄소 발생이 많은 제품이다."
                        + "플라스틱 제품, 에너지 사용이 많은 가전제품, 냉매가 필요한 냉장/냉동 제품, 자동차, 타이어, 건축자재, 가구 같은 것들은 점수를 높게 책정한다."
                        + "environmentScore(환경점수)는 최대 100점, 최소 1점으로 잡는다."
                        + "출력은 반드시 JSON 하나로만 반환한다. 키는 environmentScore 하나만 허용한다."
                ),
                Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.4); // AI가 얼마나 다양하게 답할지 조절. 낮을수록 더 안정적이고 비슷한 결과
            body.put("max_tokens", 30); // AI 응답 길이 최대치를 제한

            // POST 요청 시작
            String json = webClient.post()
                // 요청 주소. deployment를 경로 변수로 넣고 api-version을 쿼리 파라미터로
                .uri(uriBuilder -> uriBuilder
                    .path("/openai/deployments/{deployment}/chat/completions")
                    .queryParam("api-version", apiVersion)
                    .build(deployment)
                )
                .header("api-key", apiKey) // Azure OpenAI는 보통 api-key 헤더로 인증
                .contentType(MediaType.APPLICATION_JSON) // 요청 바디가 JSON
                .accept(MediaType.APPLICATION_JSON) // 응답도 JSON
                .bodyValue(body) // 만든 Map body를 요청 바디로
                .retrieve() // 요청을 보내고 응답을 받기 시작
                .bodyToMono(String.class) // 응답 바디를 String으로 받음
                .block(); // 비동기(Mono)를 현재 스레드에서 기다려서 결과 문자열을 바로 받음

            if (json == null || json.isBlank()) {
                throw new RuntimeException("envGrade 응답이 비어있습니다.");
            }

            // Azure OpenAI의 전체 응답 JSON을 파싱
            JsonNode root = objectMapper.readTree(json);
            // 응답에서 실제 AI가 말한 내용(choices[0].message.content)을 꺼냄
            String content = root.path("choices").path(0).path("message").path("content").asText("");

            if (content == null || content.isBlank()) {
                throw new RuntimeException("envGrade content가 비어있습니다.");
            }

            JsonNode out = objectMapper.readTree(content); // content는 JSON 문자열이므로 다시 파싱
            long score = out.path("environmentScore").asLong(-1);

            // 범위 검증
            if (score < 1 || score > 100) {
                throw new RuntimeException("environmentScore 범위 오류: " + score);
            }

            ProductEnvScoreResponse res = new ProductEnvScoreResponse();
            res.setEnvironmentScore(score);
            return res;

        } catch (Exception e) {
            log.error("환경점수 계산 실패", e);
            return null;
        }
    }

    private String buildEnvPrompt(String caption, List<String> tags) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음 vision 결과를 참고해 환경점수(environmentScore)를 추정해.\n");
        sb.append("caption: ").append(caption).append("\n");
        sb.append("tags: ").append(String.join(", ", tags)).append("\n");
        sb.append("반환 형식(반드시 그대로):\n");
        sb.append("{\"environmentScore\":50}\n");
        sb.append("1~100 정수만. 사람/손/배경 태그는 무시.\n");
        return sb.toString();
    }
}
