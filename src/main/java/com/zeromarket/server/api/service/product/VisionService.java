package com.zeromarket.server.api.service.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeromarket.server.api.dto.product.ProductEnvScoreRequest;
import com.zeromarket.server.api.dto.product.ProductEnvScoreResponse;
import com.zeromarket.server.api.dto.product.ProductVisionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
// Azure Vision API와 실제로 통신하는 곳. 외부 API 호출 + 응답 파싱 담당
public class VisionService {

    // Azure Vision API 호출용 HTTP 클라이언트
    private final WebClient webClient;
    // JSON 문자열을 Java 객체처럼 다루기 위함. Vision API 응답 파싱에 사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final EnvScoreService envScoreService;

    // application.yml에 있는 Azure Vision API 키 읽어옴 (보안 처리)
    @Value("${azure.vision.key}")
    private String key;

    // WebClient.Builder를 주입. Vision API의 엔드포인트 주입
    public VisionService(WebClient.Builder builder,
                         @Value("${azure.vision.endpoint}") String endpoint,
                         EnvScoreService envScoreService) {

        // 주소 끝에 /가 있으면 제거
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        this.webClient = builder.baseUrl(base).build(); // Azure Vision API를 기본 주소로 사용하는 WebClient 생성
        this.envScoreService = envScoreService;
    }

    // 이미지 바이트를 Azure Vision에 전송, 응답 JSON을 파싱, caption/tags만 뽑아서 반환
    public ProductVisionResponse analyze(byte[] imageBytes, String contentType) {

        // 기본 타입은 바이너리 데이터
        MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
        // 실제 이미지 타입이 있으면 그걸 사용
        if (contentType != null && !contentType.isBlank()) {
            mt = MediaType.parseMediaType(contentType);
        }

        // POST 요청 시작
        String json = webClient.post()
            .uri(uriBuilder -> uriBuilder // Azure Vision API의 분석 엔드포인트
                .path("/computervision/imageanalysis:analyze")
                .queryParam("api-version", "2024-02-01") // api-version 지정
                .queryParam("features", "caption,tags") // caption, tags 기능만 사용
                .build()
            )
            .header("Ocp-Apim-Subscription-Key", key) // Azure 인증 키
            .contentType(mt) // 이미지의 실제 Content-Type을 전송
            .accept(MediaType.APPLICATION_JSON) // 응답은 JSON으로
            .bodyValue(imageBytes) // 이미지 파일의 실제 바이트를 요청 body로 전송
            .retrieve()
            .bodyToMono(String.class)
            .block(); // 결과가 올 때까지 기다림


        if (json == null || json.isBlank()) {
            throw new RuntimeException("Vision 응답이 비어있습니다.");
        }

        try {
            JsonNode root = objectMapper.readTree(json); // JSON 문자열을 트리 구조로 변환

            // caption 추출
            String caption = "";
            // Azure 응답 중 captionResult.text 위치 접근
            JsonNode captionNode = root.path("captionResult").path("text");
            if (!captionNode.isMissingNode() && !captionNode.isNull()) {
                caption = captionNode.asText(""); // 값이 있으면 문자열, 없으면 빈 문자열
            }

            // tags 추출
            List<String> tags = new ArrayList<>();
            // 태그 배열 위치 접근
            JsonNode tagsNode = root.path("tagsResult").path("values");
            // 태그 하나씩 꺼내서 name 값만 리스트에 추가
            if (tagsNode.isArray()) {
                for (JsonNode t : tagsNode) {
                    String name = t.path("name").asText("");
                    if (!name.isBlank()) {
                        tags.add(name);
                    }
                }
            }
            Long envScore = null;
            try {
                ProductEnvScoreRequest envReq = new ProductEnvScoreRequest();
                envReq.setCaption(caption);
                envReq.setTags(tags);

                ProductEnvScoreResponse envRes = envScoreService.calculate(envReq);
                envScore = envRes != null ? envRes.getEnvironmentScore() : null;
            } catch (Exception e) {
                System.out.println("envScore 계산 실패: " + e.getMessage());
            }
            // DTO에 세팅
            ProductVisionResponse res = new ProductVisionResponse();
            res.setCaption(caption);
            res.setTags(tags);
            res.setEnvironmentScore(envScore);
            return res;

        } catch (Exception e) {
            throw new RuntimeException("Vision 응답 파싱 실패", e);
        }
    }
}

