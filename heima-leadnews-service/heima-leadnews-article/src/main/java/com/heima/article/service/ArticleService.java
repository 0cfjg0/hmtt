package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ArticleService extends IService<ApArticle>{

    ResponseResult load(short type,ArticleHomeDto dto);

    ResponseResult batchSave(ArticleDto dto);
}
