package com.zeromarket.server.api.config;

import com.zeromarket.server.api.security.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // CORS 넉넉히
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 서버 -> 클라이언트
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트 -> 서버
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
//    @Value("${rabbitmq.host}")
//    private String host;
//    @Value("${rabbitmq.relay.port}")
//    private int port;
//    @Value("${rabbitmq.relay.system-login}")
//    private String systemLogin;
//    @Value("${rabbitmq.relay.client-passcode}")
//    private String systemPasscode;
//    @Value("${rabbitmq.relay.client-login}")
//    private String clientLogin;
//    @Value("${rabbitmq.relay.client-passcode}")
//    private String clientPasscode;
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // socketJs 클라이언트가 WebSocket 핸드셰이크를 하기 위해 연결할 endpoint를 지정할 수 있다.
//        registry.addEndpoint("/chat/inbox") //  ws://{서버ip}:{port}/chat/inbox를 통해 소켓 연결
//                .setAllowedOriginPatterns("*"); // cors 허용을 위해 꼭 설정해주어야 함. setCredential() 설정시에 AllowedOrigin 과 같이 사용될 경우 오류가 날 수 있으므로 OriginPatterns 설정으로 사용하였음
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        // 메시지 브로커 설정
//        registry.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
//        // stomp 외부 메시지 브로커 사용 허가
//        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
//                .setRelayHost(host)
//                .setRelayPort(port)
//                .setSystemLogin(systemLogin)
//                .setSystemPasscode(systemPasscode)
//                .setClientLogin(clientLogin)
//                .setClientPasscode(clientPasscode);
//
//        // 클라이언트로부터 메시지를 받을 api의 prefix를 설정함
//        // publish
//        // 메시지 보낼 저장될 큐의 prefix
//        registry.setApplicationDestinationPrefixes("/pub");
//
//    }
}