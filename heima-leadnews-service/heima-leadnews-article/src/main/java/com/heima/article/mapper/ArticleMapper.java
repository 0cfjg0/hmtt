package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

/**
 * @author CFJG
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ApArticle> {

    List<ApArticle> selectList(@Param("type") short type , @Param("dto") ArticleHomeDto dto);


}
