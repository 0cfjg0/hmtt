package com.heima.wemedia.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    //news队列
    public Queue newsQueue(){
        //创建一个持久化队列
        return QueueBuilder.durable("news.queue")
                //绑定死信交换机
                .deadLetterExchange("dl.news.exchange")
                //绑定死信key
                .deadLetterRoutingKey("ttl")
                //设置队列过期时间
                .ttl(5000)
                .build();
    }

    @Bean
    //news交换机
    public DirectExchange newsDirect(){
        return ExchangeBuilder.directExchange("news.direct").build();
    }

    @Bean
    public Binding newsQueueToNewsDirect(Queue newsQueue, DirectExchange newsDirect){
        return BindingBuilder.bind(newsQueue).to(newsDirect).with("article");
    }
}
