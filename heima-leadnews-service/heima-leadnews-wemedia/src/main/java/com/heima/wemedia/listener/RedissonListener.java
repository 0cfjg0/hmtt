package com.heima.wemedia.listener;

import com.heima.wemedia.task.RedissonWorker;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class RedissonListener {

    @Resource
    RedissonClient redissonClient;

    @Resource
    RedissonWorker redissonWorker;

    @PostConstruct
    public void start(){
        redissonWorker.redissonWork();
    }

}
