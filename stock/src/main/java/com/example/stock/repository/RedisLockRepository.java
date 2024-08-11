package com.example.stock.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisLockRepository {
	// 레디스를 사용하기 위해 RedisTemplate 클래스를 변수로 추가한다.
	private RedisTemplate<String, String> redisTemplate;

	public RedisLockRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// Lock 메서드를 구현한다.
	public Boolean lock(Long key) {
		return redisTemplate
			.opsForValue()
			.setIfAbsent(// setnx 명령어 호출
				generateKey(key), 			// key
				"lock", 					// value
				Duration.ofMillis(3_000) 	// lock timeout 지정(e.g. 3000ms(3sec))
			);
	}

	// Lock을 해제하기 위한 메서드
	public Boolean unlock(Long key) {
		return redisTemplate.delete(generateKey(key));
	}


	private String generateKey(Long key) {
		return key.toString();
	}
}