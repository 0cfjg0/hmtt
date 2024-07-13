package com.heima.redisson.Task;

import com.heima.redisson.controller.RedissonController;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonWorker {

    @Resource
    RedissonClient redissonClient;

    @Async
    public void redissonWork(){
        RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue("RedissonBlockingQueue");
        while(true){
            try {
                String poll = blockingQueue.poll(10, TimeUnit.SECONDS);
                System.out.println(poll);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
