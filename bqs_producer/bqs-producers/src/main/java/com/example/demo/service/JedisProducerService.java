package com.example.demo.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisProducerService {
    
    private final String[] queues = {"queue1", "queue2", "queue3", "queue4", "queue5"};
    private AtomicInteger counter = new AtomicInteger(0);
    private final JedisPool jedisPool;

    public JedisProducerService() {
        this.jedisPool = new JedisPool("localhost", 6379);  
    }

    public void enqueueQuestion(String questionJson) {
        int questionIndex = counter.getAndIncrement() % queues.length;
        String queue = queues[questionIndex];

        System.out.println("Adding question to Redis: " + questionJson);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush(queue, questionJson);
            System.out.println("Question added to " + queue);

            Long size = jedis.llen(queue);
            System.out.println("Novi broj elemenata u " + queue + ": " + size);
        }
    }
}

