package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleConfigMapper;
import com.heima.article.service.ArticleConfigService;
import com.heima.model.article.pojos.ApArticleConfig;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP已发布文章配置表 服务实现类
 * </p>
 *
 * @author ghy
 * @since 2023-09-21
 */
@Service
public class ArticleConfigServiceImpl extends ServiceImpl<ArticleConfigMapper, ApArticleConfig> implements ArticleConfigService {

}
