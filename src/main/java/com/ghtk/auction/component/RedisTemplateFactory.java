package com.ghtk.auction.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RedisTemplateFactory {
  private final RedisConnectionFactory connectionFactory;
  private final ObjectMapper objectMapper;
  private final Map<Class<?>, RedisTemplate<String, ?>> templates = new HashMap<>();

  public RedisTemplateFactory(RedisConnectionFactory connectionFactory,
      ObjectMapper objectMapper) {
    this.connectionFactory = connectionFactory;
    this.objectMapper = objectMapper;
  }

  @SuppressWarnings("unchecked")
  public <T> RedisTemplate<String, T> get(Class<T> clazz) {
    return (RedisTemplate<String, T>) templates.computeIfAbsent(clazz, this::create);
  }
  
  private <T> RedisTemplate<String, T> create(Class<T> clazz) {
    RedisTemplate<String, T> template = new RedisTemplate<>();
    var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
        objectMapper, clazz);
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.afterPropertiesSet();
    return template;
  }
}
