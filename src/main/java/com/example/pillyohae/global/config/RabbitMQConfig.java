package com.example.pillyohae.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정을 담당하는 Spring Boot 설정 클래스. RabbitMQ의 큐, 익스체인지, 바인딩을 설정하고, 메시지를 직렬화/역직렬화할 수 있도록
 * RabbitTemplate을 설정한다.
 */
@Configuration
public class RabbitMQConfig {

    // RabbitMQ 서버의 호스트 정보
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    // RabbitMQ 서버의 포트 번호
    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    // RabbitMQ 접속을 위한 사용자명
    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    // RabbitMQ 접속을 위한 비밀번호
    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

    // 사용할 큐의 이름
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    // 사용할 익스체인지의 이름
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    // 큐와 익스체인지를 연결할 때 사용할 라우팅 키
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * 지정된 큐 이름으로 Queue 빈을 생성. 이 큐는 메시지를 저장하며, RabbitMQ에서 관리된다.
     *
     * @return Queue 빈 객체
     */
    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }

    /**
     * DirectExchange를 사용하여 특정 라우팅 키를 기반으로 메시지를 큐에 전달.
     *
     * @return DirectExchange 객체
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    /**
     * 큐와 익스체인지를 특정 라우팅 키를 이용하여 바인딩한다.
     *
     * @param queue    메시지를 저장할 Queue 객체
     * @param exchange 메시지를 라우팅할 DirectExchange 객체
     * @return Binding 객체
     */
    @Bean
    public Binding directbinding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    /**
     * RabbitMQ와의 연결을 위한 ConnectionFactory를 설정한다. ConnectionFactory는 메시지 브로커와 연결을 유지하며, 메시지를 주고받을 수
     * 있도록 한다.
     *
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        return connectionFactory;
    }

    /**
     * RabbitTemplate을 사용하여 메시지를 보내고 받을 수 있도록 설정. JSON 형식의 메시지를 직렬화/역직렬화할 수 있도록 설정.
     *
     * @param connectionFactory RabbitMQ와의 연결을 관리하는 ConnectionFactory 객체
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Jackson2JsonMessageConverter를 사용하여 메시지를 JSON 형식으로 변환. RabbitMQ에서 메시지를 주고받을 때 JSON 형식으로
     * 직렬화/역직렬화할 수 있도록 한다.
     *
     * @return MessageConverter 객체
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
