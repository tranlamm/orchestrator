package com.lamt2.orchestrator.configuration.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.prefix.set_model_running}")
    public static String KEY_MODEL_RUNNING_SET;
    @Value("${spring.redis.prefix.model_param}")
    public static String KEY_PREFIX_MODEL_RUNNING_PARAM;
    @Value("${spring.redis.prefix.model_info}")
    public static String KEY_PREFIX_MODEL_RUNNING_INFO;
    @Value("${spring.redis.prefix.model_train}")
    public static String KEY_PREFIX_MODEL_RUNNING_TRAIN;
    @Value("${spring.redis.prefix.model_validation}")
    public static String KEY_PREFIX_MODEL_RUNNING_VALIDATION;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
