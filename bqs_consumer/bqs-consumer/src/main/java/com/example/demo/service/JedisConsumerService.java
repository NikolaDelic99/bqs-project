package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.QuestionRepository;
import com.example.demo.model.QuestionEntity;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisConsumerService {

    private final String[] queues = {"queue1", "queue2", "queue3", "queue4", "queue5"};

    private final JedisPool jedisPool;
    private final QuestionRepository questionRepository;
    private final QuestionValidator validator;

    @Autowired
    public JedisConsumerService(QuestionRepository questionRepository, QuestionValidator validator) {
        this.jedisPool = new JedisPool("localhost", 6379); 
        this.questionRepository = questionRepository;
        this.validator = validator;
    }

    @PostConstruct
    public void startConsumers() {
        for (String queue : queues) {
            new Thread(() -> {
                try (Jedis jedis = jedisPool.getResource()) {
                    while (true) {
                        String questionJson = jedis.lpop(queue); 
                        if (questionJson != null) {
                            boolean isValid = validator.validate(questionJson);
                            QuestionEntity question = new QuestionEntity();
                            question.setBody(questionJson);
                            question.setValid(isValid);
                            questionRepository.save(question);
                        }
                    }
                }
            }).start();
        }
    }
}

