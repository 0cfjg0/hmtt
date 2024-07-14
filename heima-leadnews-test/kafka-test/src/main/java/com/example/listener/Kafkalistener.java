package com.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Kafkalistener {

    @KafkaListener(topics = "itheima-topic")
    public void listener(String message){
        log.warn("kafka收到消息:{}",message);
    }

}
