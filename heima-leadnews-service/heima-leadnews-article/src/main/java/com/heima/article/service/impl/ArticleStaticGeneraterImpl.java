package com.heima.article.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ArticleContentMapper;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.article.service.ArticleStaticGenerater;
import com.heima.common.exception.CustomException;
import com.heima.file.service.impl.MinIOFileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.enums.AppHttpCodeEnum;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ArticleStaticGeneraterImpl implements ArticleStaticGenerater {

    @Resource
    ArticleContentMapper articleContentMapper;

    @Resource
    Configuration configuration;

    @Resource
    MinIOFileStorageService minIOFileStorageService;

    @Resource
    ArticleMapper articleMapper;

    @Override
    @Async
    public void generatePage(Long articleId) throws IOException, TemplateException {
        //查询文章内容
        LambdaQueryWrapper<ApArticleContent> wrapper = Wrappers.<ApArticleContent>lambdaQuery()
                .eq(ApArticleContent::getArticleId, articleId);
        ApArticleContent articleContent = articleContentMapper.selectOne(wrapper);
        String content = articleContent.getContent();
        //通过freemarker填充内容
        if(ObjectUtil.isEmpty(content)){
            log.warn("内容为空");
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        StringWriter out = new StringWriter();
        Map<String,Object> data = new HashMap<>();
        Template template = configuration.getTemplate("article.ftl");
        List<Map> res = JSON.parseArray(content, Map.class);
        data.put("content",res);
        //将生成的模版结果生成到输出流
        template.process(data,out);
        //生产HTML上传到minio
        ByteArrayInputStream in = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
        String url = minIOFileStorageService.uploadHtmlFile("", Convert.toStr(articleId) + ".html", in);
        //更新ApArticle的staticUrl字段
        ApArticle apArticle = articleMapper.selectById(articleId);
        apArticle.setStaticUrl(url);
        articleMapper.updateById(apArticle);
        log.warn(url);
    }

}
