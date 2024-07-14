package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class KafkaController {

    @Resource
    KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/getMessage")
    public String getKafkaMessage() throws ExecutionException, InterruptedException {
        kafkaTemplate.send("topic", "kafkaTest");
        return new String("success");
    }

}
