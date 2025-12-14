package com.zeromarket.server.api.service.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeromarket.server.api.dto.product.ProductAiDraftRequest;
import com.zeromarket.server.api.dto.product.ProductAiDraftResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiDraftService {

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
    public AiDraftService(
        WebClient.Builder builder,
        @Value("${azure.openai.endpoint}") String endpoint
    ) {
        // endpoint 끝에 / 가 있으면 제거해서 주소가 두 번 슬래시로 이어지는 문제 막음
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        // base 주소를 기본 URL로 쓰는 WebClient를 만들어 저장
        this.webClient = builder.baseUrl(base).build();
    }

    // 프론트에서 받은 요청을 바탕으로 AI 초안 결과(title, description)를 만들어 반환
    public ProductAiDraftResponse generate(ProductAiDraftRequest req) {
        try {
            String prompt = buildPrompt(req); // 요청 데이터를 문장 형태로 정리해서 AI에게 보낼 프롬프트 문자열로

            // Azure OpenAI에 보낼 요청 바디(JSON)를 만들기 위한 Map
            Map<String, Object> body = new HashMap<>();
            body.put("messages", List.of(
                // AI에게 규칙을 강제하는 시스템 지시문
                Map.of("role", "system", "content",
                    "너는 중고거래 상품 등록 도우미다. 반드시 한국어로만 작성한다. "
                        + "단어는 실제 한국인이 쓰는 단어들로만 사용한다."
                        + "제공된 caption/tags와 입력 정보로 확실한 것만 작성한다. "
                        + "추측이 필요한 내용(브랜드, 모델명, 사용기간 등)은 쓰지 않는다. "
                        + "출력은 반드시 JSON 하나로만 반환한다. 키는 title, description 두 개만 허용한다."
                ),
                Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.4); // AI가 얼마나 다양하게 답할지 조절. 낮을수록 더 안정적이고 비슷한 결과
            body.put("max_tokens", 500); // AI 응답 길이 최대치를 제한

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
                throw new RuntimeException("AI Draft 응답이 비어있습니다.");
            }

            // Azure OpenAI의 전체 응답 JSON을 파싱
            JsonNode root = objectMapper.readTree(json);
            // 응답에서 실제 AI가 말한 내용(choices[0].message.content)을 꺼냄
            String content = root.path("choices").path(0).path("message").path("content").asText("");

            if (content.isBlank()) {
                throw new RuntimeException("AI Draft content가 비어있습니다.");
            }

            JsonNode out = objectMapper.readTree(content); // AI가 content에 JSON 문자열을 넣어줬다고 가정하고 다시 파싱

            // title / description 키의 값을 문자열로. 없으면 빈 문자열
            String title = out.path("title").asText("");
            String description = out.path("description").asText("");

            // 설명 문장을 보기 좋게 줄바꿈을 정리
            description = normalizeDescription(description);

            return new ProductAiDraftResponse(title, description);

        } catch (Exception e) {
            throw new RuntimeException("AI Draft 생성 실패", e);
        }
    }

    private String normalizeDescription(String s) {
        if (s == null) return "";
        String text = s.replace("\r\n", "\n").replace("\r", "\n");

        // 문장 중간 개행 제거(공백으로 합침)
        text = text.replaceAll("[ \t]*\n+[ \t]*", " ");

        // 문장 끝(.,!,?) 뒤에서만 개행 추가
        text = text.replaceAll("([.!?])\\s+", "$1\n");

        // 여러 개행 정리 (줄바꿈이 너무 많은 경우 2줄까지만 남기고 정리)
        text = text.replaceAll("\n{3,}", "\n\n").trim();

        return text; // 정리된 설명을 반환
    }

    // 요청 DTO를 사람이 읽을 수 있는 프롬프트 문자열로
    private String buildPrompt(ProductAiDraftRequest req) {
        // caption이 null이면 빈값, 있으면 앞뒤 공백 제거
        String caption = req.getCaption() == null ? "" : req.getCaption().trim();
        // tags가 null이면 빈 리스트로 처리
        List<String> tags = req.getTags() == null ? List.of() : req.getTags();

        // 문자열을 효율적으로 이어붙이기 위한 객체
        StringBuilder sb = new StringBuilder();
        sb.append("다음 정보를 바탕으로 중고거래 상품 등록용 초안을 만들어\n");
        sb.append("caption: ").append(caption).append("\n");
        sb.append("tags: ").append(String.join(", ", tags)).append("\n");

        if (req.getSellPrice() != null) {
            sb.append("sellPrice: ").append(req.getSellPrice()).append("\n");
        }

        sb.append("반환 형식(반드시 그대로):\n");
        sb.append("{\"title\":\"...\",\"description\":\"...\"}\n");
        sb.append("설명은 줄바꿈 포함 가능하지만, 마침표가 나올때까지는 한줄을 지키고 JSON 문자열 규칙을 지킨다\n");
        sb.append("title은 15자 이내, description은 5~8줄 정도로 간결하게.\n");
        sb.append("tags에 사람이나 손 같은것이 나올 때는 해당 부분은 무시한다\n");
        
        return sb.toString(); // 프롬프트 문자열을 반환
    }
}
