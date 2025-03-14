package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.JedisProducerService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/api/jedisquestions")
public class JedisQuestionController {
    
    private final JedisProducerService redisService;
    private final JedisPool jedisPool;

    
    public JedisQuestionController(JedisProducerService redisService) {
        this.redisService = redisService;
        this.jedisPool = new JedisPool("localhost", 6379);     }

    @PostMapping("/{guid}")
    public ResponseEntity<String> receiveQuestion(@RequestHeader("API-KEY") String apiKey, 
                                                  @PathVariable String guid,
                                                  @RequestBody String questionJson) {
        if (!apiKey.equals("MY_SECRET_KEY")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API key");
        }

        System.out.println("Received question: " + questionJson);

        redisService.enqueueQuestion(questionJson);

        return ResponseEntity.ok("Question received with guid " + guid);
    }

    @PostMapping("/test-redis")
    public ResponseEntity<String> testRedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush("testQueue", "Test Message");
            Long size = jedis.llen("testQueue");

            return ResponseEntity.ok("Redis queue size: " + size);
        }
    }
}

