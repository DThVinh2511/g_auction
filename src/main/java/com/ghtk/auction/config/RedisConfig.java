package com.ghtk.auction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@EnableRedisRepositories
@Configuration
public class RedisConfig {
	@Value("${spring.data.redis.port}")
	private int redisPort;
	@Value("${spring.data.redis.host}")
	private String redisHost;
	
	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		//Tạo standalone Connection tới Redis
		return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
	}
	
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

  @Bean
  public ObjectMapper objectMapper() {
    return JsonMapper.builder()
      .addModule(new JavaTimeModule())
      .build();
    
  }
}