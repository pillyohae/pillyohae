package com.example.pillyohae.global.config;

import com.example.pillyohae.global.message_queue.RedisMessageSubscriber;
import com.example.pillyohae.global.message_queue.publisher.MessagePublisher;
import com.example.pillyohae.global.message_queue.publisher.RedisMessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    /**
     * Redis 서버와 애플리케이션 간 연결을 설정하고 관리, LettuceConnectionFactory 사용
     *
     * @return 연결 팩토리를 생성
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //LettuceConnectionFactory 는 Lettuce 클라이언트를 사용하여 연결 팩토리를 생성해주는 역할
        //호스트와 포트 정보를 사용하여 Redis 서버와의 연결 설정을 해줌.
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * Redis 와의 데이터 입출력을 위한 주요 인터페이스 RedisTemplate Key-Value 구조로 데이터를 저장, 조회, 삭제 등의 작업을 수행하는 역할
     * 직렬화,역직렬화 설정을 통해 데이터 형식을 관리할 수 있음
     *
     * @return 설정된 template 리턴
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        // Redis에 저장할 Key를 String 형태로 직렬화
        template.setKeySerializer(new StringRedisSerializer());

        // Redis에 저장할 Value를 String 형태로 직렬화
        template.setValueSerializer(new StringRedisSerializer());

        // Redis 연결 팩토리 설정
        template.setConnectionFactory(redisConnectionFactory());

        return template;
    }

    /**
     * string-object 저장을 위한 redis template
     *
     * @return 설정된 template 리턴
     */
    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, Integer> intRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Integer.class)); // Long 값 직렬화
        return redisTemplate;
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber(new ObjectMapper()));
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("messageQueue");
    }


    @Bean
    MessagePublisher redisPublisher() {
        return new RedisMessagePublisher(objectRedisTemplate(), topic());
    }

    @Bean
    public RedissonClient redissonClient() {
        RedissonClient redisson = null;
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);
        redisson = Redisson.create(config);
        return redisson;
    }


}
