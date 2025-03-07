package com.example.demo.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisProducerService {
	
	private final String[] queues = {"queue1", "queue2", "queue3", "queue4", "queue5"};
	private AtomicInteger counter = new AtomicInteger(0);
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	public void enqueueQuestion(String questionJson) {
		int questionIndex = counter.getAndIncrement() % queues.length;
		String queue = queues[questionIndex];
		
	    System.out.println("Adding question to Redis: " + questionJson);

		
		redisTemplate.opsForList().rightPush(queue,questionJson);
		
		
		System.out.println("Question added to " + queue);
		
		Long size = redisTemplate.opsForList().size(queue);
        System.out.println("Novi broj elemenata u " + queue + ": " + size);
	}
}
