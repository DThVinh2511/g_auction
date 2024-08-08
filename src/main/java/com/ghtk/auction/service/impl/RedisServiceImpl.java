package com.ghtk.auction.service.impl;

import com.ghtk.auction.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {
	private final RedisTemplate<String,String> redisTemplate;
	@Autowired
	public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
//	@Override
//	public void saveRedis(String key, String value) {
//		redisTemplate.opsForValue().set(key, value, 3, TimeUnit.MINUTES);
//	}
}
