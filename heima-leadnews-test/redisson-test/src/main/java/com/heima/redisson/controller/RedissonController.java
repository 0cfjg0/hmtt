package com.heima.redisson.controller;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RestController
public class RedissonController {

    @Resource
    RedissonClient redissonClient;

    @GetMapping("/redissonTest/{TTL}")
    public String redissonTest(@PathVariable(name = "TTL")Long TTL){
        RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue("RedissonBlockingQueue");
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.offer("Test",TTL, TimeUnit.SECONDS);
        return LocalDateTime.now().toString();
    }
}
