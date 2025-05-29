package com.lamt2.orchestrator;

import com.lamt2.orchestrator.configuration.redis.RedisConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(RedisConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class RedisServiceTest {
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void redisServiceTest() {
        String testing = "testing string";
        redisTemplate.opsForValue().set("test_123", testing);
        String value = (String) redisTemplate.opsForValue().get("test_123");
        Assertions.assertEquals(testing, value);
    }
}
