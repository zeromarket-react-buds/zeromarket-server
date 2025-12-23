package com.zeromarket.server.api.config;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitConfig {

    public static final String CHAT_EXCHANGE = "chat.topic";
    public static final String ROUTING_KEY_PREFIX = "room."; // room.1, room.2 ...

    // 일단은 이름 고정으로 하나 - UI에서 확인 쉬움
    @Bean
    public Queue serverQueue() {
        return QueueBuilder.durable("chat.queue.server").build();
    }


    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE, true, false);
    }

    @Bean
    public Binding serverBinding(TopicExchange chatExchange, Queue serverQueue) {
        // room.* : room.1, room.2 ... 모두 수신
        return BindingBuilder.bind(serverQueue).to(chatExchange).with("room.*");
    }


//    @Value("${rabbitmq.chat.queue.name}")
//    private String chatQueueName;
//    @Value("${rabbitmq.chat.exchange.name}")
//    private String chatExchangeName;
//    @Value("${rabbitmq.chat.routing.key}")
//    private String routingKey;
//
//    @Value("${rabbitmq.host}")
//    private String host;
//    @Value("${rabbitmq.port}")
//    private int port;
//    @Value("${rabbitmq.virtual-host}")
//    private String virtualHost;
//    @Value("${rabbitmq.username}")
//    private String username;
//    @Value("${rabbitmq.password}")
//    private String password;
//
//    // Queue 등록
//    @Bean
//    public Queue queue() {
//        return new Queue(chatQueueName, true);
//    }
//
//    // Exchange 등록
//    // new TopicExchange(chatExchangeName) -> 설정한 이름으로 TopicExchange를 생성
//    @Bean
//    public TopicExchange exchange() {
//        return new TopicExchange(chatExchangeName);
//    }
//
//    // Exchange와 Queue바인딩
//    @Bean
//    public Binding binding(Queue queue, TopicExchange exchange) {
//
//        //  queue와 exchange를 routingKey(여기선 *.room.*)를 통해 바인딩
//        return BindingBuilder
//            .bind(queue)
//            .to(exchange)
//            .with(routingKey);
//    }
//
//    // RabbitMQ와의 메시지 통신을 담당하는 클래스
//    @Bean
//    public RabbitTemplate rabbitTemplate() {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
//
//        // 이 세팅을 통해 RabbitTemplate을 통해 메시지 전송 시 따로 전송할 exchangeName을 설정할 필요 없음
//        rabbitTemplate.setExchange(chatExchangeName);
//
//        return rabbitTemplate;
//    }
//
//    // RabbitMQ와의 연결을 관리하는 클래스
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory factory = new CachingConnectionFactory();
//        factory.setHost(host);
//        factory.setPort(port);
//        factory.setVirtualHost(virtualHost);
//        factory.setUsername(username);
//        factory.setPassword(password);
//        return factory;
//    }
}