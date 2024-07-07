package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleContentMapper;
import com.heima.article.service.ArticleContentService;
import com.heima.model.article.pojos.ApArticleContent;
import org.springframework.stereotype.Service;

@Service
public class ArticleContentServiceImpl extends ServiceImpl<ArticleContentMapper, ApArticleContent> implements ArticleContentService {
}
