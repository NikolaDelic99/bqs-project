package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.QuestionRepository;
import com.example.demo.model.QuestionEntity;

import jakarta.annotation.PostConstruct;

@Service
public class RedisConsumerService {
	
	private final String[] queues = {"queue1", "queue2", "queue3", "queue4", "queue5"};
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private QuestionValidator validator;
	
	@PostConstruct
	public void startConsumers() {
		for(String queue : queues) {
			new Thread(() -> {
				while(true) {
					String questionJson = redisTemplate.opsForList().leftPop(queue);
					if(questionJson != null) {
						boolean isValid = validator.validate(questionJson);
						QuestionEntity question = new QuestionEntity();
						question.setBody(questionJson);
						question.setValid(isValid);
						questionRepository.save(question);
					}
				}
			}).start();
		}
	}

}
