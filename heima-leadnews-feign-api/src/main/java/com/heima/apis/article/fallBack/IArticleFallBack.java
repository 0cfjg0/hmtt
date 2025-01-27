package com.heima.apis.article.fallBack;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.stereotype.Component;

@Component
public class IArticleFallBack implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        return ResponseResult.okResult("fallBack");
    }

    @Override
    public String getAuthor(Long articleId) {
        return "查询中";
    }
}
