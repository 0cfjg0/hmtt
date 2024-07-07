package com.heima.article;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ArticleContentMapper;
import com.heima.article.mapper.ArticleMapper;
import com.heima.file.service.impl.MinIOFileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleTest {

    @Resource
    ArticleMapper articleMapper;

    @Resource
    Configuration configuration;

    @Resource
    ArticleContentMapper articleContentMapper;

    @Resource
    MinIOFileStorageService minIOFileStorageService;

    @Test
    public void articleTest() throws IOException, TemplateException {
        //查询文章内容
        Long articleId = 1383827911810011137L;
        LambdaQueryWrapper<ApArticleContent> wrapper = Wrappers.<ApArticleContent>lambdaQuery()
                .eq(ApArticleContent::getArticleId, articleId);
        ApArticleContent articleContent = articleContentMapper.selectOne(wrapper);
        String content = articleContent.getContent();
        //通过freemarker填充内容
        if(ObjectUtil.isEmpty(content)){
            System.out.println("内容为空");
            return;
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
        System.out.println(url);
    }

}
