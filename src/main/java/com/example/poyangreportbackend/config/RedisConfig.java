package com.example.poyangreportbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    // Reactive 模板
    @Bean(name = "reactiveRedisTemplate")
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext.<String, Object>
                        newSerializationContext()
                .key(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer()) // GenericJackson2JsonRedisSerializer
                .hashKey(new StringRedisSerializer())
                .hashValue(new GenericJackson2JsonRedisSerializer()) // GenericJackson2JsonRedisSerializer
                .build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
    }

    // 同步模板
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Autowired RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer()); // GenericJackson2JsonRedisSerializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // GenericJackson2JsonRedisSerializer
        template.setEnableTransactionSupport(true);
        return template;
    }

}