package com.heima.wemedia.listener;

import cn.hutool.core.util.ObjectUtil;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.constant.NewsConstants;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;



@Slf4j
@Component
public class DeadMessageListener {

    @Resource
    WmNewsMapper wmNewsMapper;

    @Resource
    WmAuditService wmAuditService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "dl.news.queue",durable = "true"),
                    exchange = @Exchange(name = "dl.news.exchange",type = ExchangeTypes.DIRECT),
                    key = {"ttl"}
            )
    })
    public void doMessage(String message){
        log.warn("死信队列收到消息:{}",message);
        Integer newsId = Integer.parseInt(message);
        if (ObjectUtil.isNotNull(newsId)) {
            log.warn("收到延迟任务id:{}",newsId);
            WmNews news = wmNewsMapper.selectById(newsId);
            long articleId = wmAuditService.saveArticle(news);
            news.setArticleId(articleId);
            wmAuditService.updateNews(news,WmNews.Status.PUBLISHED, NewsConstants.SUCCESS);
            log.warn("发布延迟任务完成");
        }
    }

}
