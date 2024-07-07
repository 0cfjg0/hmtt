package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.pojos.ApArticleConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * APP已发布文章配置表 Mapper 接口
 * </p>
 *
 * @author ghy
 * @since 2023-09-21
 */
@Mapper
public interface ArticleConfigMapper extends BaseMapper<ApArticleConfig> {

}
