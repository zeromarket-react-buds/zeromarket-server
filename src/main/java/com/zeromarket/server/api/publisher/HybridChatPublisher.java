package com.zeromarket.server.api.publisher;

import com.zeromarket.server.api.dto.chat.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Primary // ChatPublisher 주입 시 이게 기본으로 선택되게
public class HybridChatPublisher implements ChatPublisher {

    private final RabbitMqChatPublisher rabbit;
    private final LocalWebSocketChatPublisher local;
    private final RabbitTemplate rabbitTemplate; // health check 용

    // 간단 서킷브레이커
    private volatile long nextRabbitTryAtMs = 0L;     // 이 시간 전엔 Rabbit 시도 스킵
    private volatile int consecutiveFailures = 0;

    // 튜닝값
    private static final long COOLDOWN_BASE_MS = 30_000;  // 최소 30초
    private static final long COOLDOWN_MAX_MS  = 120_000; // 최대 120초

    @Override
    public void publish(ChatDto.ChatMessageRes msg) {

        // 1) 최근에 계속 실패했으면 Rabbit 시도 자체를 잠깐 스킵 → 바로 local
        long now = System.currentTimeMillis();
        if (now < nextRabbitTryAtMs) {
            local.publish(msg);
            return;
        }

        // 2) Rabbit 먼저 시도
        try {
            // (선택) 아주 가벼운 핑: broker 연결이 살아있는지 빠르게 체크
            // 실패하면 예외 → catch로 빠짐
            rabbitTemplate.execute(channel -> {
                channel.basicQos(1); // no-op 수준, 채널 열림 확인용
                return null;
            });

            rabbit.publish(msg);

            // 성공하면 실패 카운터 리셋
            consecutiveFailures = 0;
            nextRabbitTryAtMs = 0L;
        } catch (Exception e) {
            // 3) 실패하면 local로 우회 + “잠깐은 rabbit 시도 스킵”
            consecutiveFailures++;
            long cooldown = Math.min(COOLDOWN_MAX_MS, COOLDOWN_BASE_MS * consecutiveFailures);
            nextRabbitTryAtMs = now + cooldown;

            log.warn("[PUBLISH:FALLBACK] Rabbit unavailable -> local. failCount={}, cooldownMs={}",
                    consecutiveFailures, cooldown, e);

            local.publish(msg);
        }
    }
}

