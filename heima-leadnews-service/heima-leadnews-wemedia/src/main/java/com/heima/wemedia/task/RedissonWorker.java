package com.heima.wemedia.task;


import cn.hutool.core.util.ObjectUtil;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.constant.NewsConstants;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmAuditService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedissonWorker {

    @Resource
    RedissonClient redissonClient;

    @Resource
    WmNewsMapper wmNewsMapper;

    @Resource
    WmAuditService wmAuditService;

    @Async
    public void redissonWork(){
        RBlockingQueue<Integer> blockingQueue = redissonClient.getBlockingQueue("publishBlockingQueue");
        while(true){
            try {
                Integer newsId = blockingQueue.poll(10, TimeUnit.SECONDS);
                if (ObjectUtil.isNotNull(newsId)) {
                    log.warn("收到延迟任务id:{}",newsId);
                    WmNews news = wmNewsMapper.selectById(newsId);
                    long articleId = wmAuditService.saveArticle(news);
                    news.setArticleId(articleId);
                    wmAuditService.updateNews(news,WmNews.Status.PUBLISHED, NewsConstants.SUCCESS);
                    log.warn("发布延迟任务完成");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
