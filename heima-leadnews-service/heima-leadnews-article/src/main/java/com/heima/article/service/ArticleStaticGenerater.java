package com.heima.article.service;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface ArticleStaticGenerater {

    public void generatePage(Long articleId) throws IOException, TemplateException;

}
