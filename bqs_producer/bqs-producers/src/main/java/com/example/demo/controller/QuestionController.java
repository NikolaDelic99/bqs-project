package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RedisProducerService;



@RestController
@RequestMapping("/api/questions")
public class QuestionController {
	
	@Autowired
	private RedisProducerService redisService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	public QuestionController(RedisProducerService redisService) {
		this.redisService = redisService;
	
	}
	
	@PostMapping("/{guid}")
	public ResponseEntity<String> receiveQuetion(@RequestHeader("API-KEY") String apiKey, 
			@PathVariable String guid,
			@RequestBody String questionJson){
		if(!apiKey.equals("MY_SECRET_KEY")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid apiKey");
		}
		
		System.out.println("Received question: " + questionJson);
		
		redisService.enqueueQuestion(questionJson);
		
		return ResponseEntity.ok("Question received with guid " + guid);
	}
	
	@PostMapping("/test-redis")
	public ResponseEntity<String> testRedis() {
	    redisTemplate.opsForList().rightPush("testQueue", "Test Message");
	    Long size = redisTemplate.opsForList().size("testQueue");

	    return ResponseEntity.ok("Redis queue size: " + size);
	}

}
