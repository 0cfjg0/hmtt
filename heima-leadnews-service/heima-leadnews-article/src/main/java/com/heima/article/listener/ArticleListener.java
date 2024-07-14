package com.heima.article.listener;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ArticleConfigMapper;
import com.heima.common.exception.CustomException;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ArticleListener {

    @Resource
    ArticleConfigMapper articleConfigMapper;

    @KafkaListener(topics = "article-Topic")
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(String message){
        if(StrUtil.isNotBlank(message)){
            Map msg = JSONUtil.toBean(message, Map.class);
            Long articleId = Convert.toLong(msg.get("articleId"));
            Integer enable = Convert.toInt(msg.get("enable"));
            LambdaUpdateWrapper<ApArticleConfig> wrapper = Wrappers.<ApArticleConfig>lambdaUpdate()
                    .set(ApArticleConfig::getIsDown, enable == 0)
                    .eq(ApArticleConfig::getArticleId, articleId);
            int update = articleConfigMapper.update(null, wrapper);
            if(update == 0){
                throw new CustomException(AppHttpCodeEnum.ARTICLE_UP_OR_DOWN_ERROR);
            }
        }
    }

}
