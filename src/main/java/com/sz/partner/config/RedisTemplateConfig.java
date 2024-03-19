package com.sz.partner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * RedisTemplate 配置  指定Redis配置：自定义Redis的序列化操作
 */
@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 指定Redis的连接器
        redisTemplate.setConnectionFactory(connectionFactory);
        // 指定字符串类型的序列号器
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }
}
