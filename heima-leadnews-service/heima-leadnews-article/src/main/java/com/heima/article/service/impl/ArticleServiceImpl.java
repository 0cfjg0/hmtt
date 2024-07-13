package com.heima.article.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleConfigMapper;
import com.heima.article.mapper.ArticleContentMapper;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.article.service.ArticleStaticGenerater;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import freemarker.template.TemplateException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文章信息表，存储已发布的文章 服务实现类
 * </p>
 *
 * @author ghy
 * @since 2023-09-21
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Resource
    ArticleConfigMapper articleConfigMapper;

    @Resource
    ArticleContentMapper articleContentMapper;

    @Resource
    ArticleStaticGenerater articleStaticGenerater;

    @Override
    public ResponseResult load(short type,ArticleHomeDto dto) {
        // 一.校验参数
        //非空校验
        if(ObjectUtil.isEmpty(dto)){
            return ResponseResult.errorResult(400,"参数错误");
        }
        //channel校验
        if(ObjectUtil.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        //时间校验
        if(ObjectUtil.isEmpty(dto.getMaxBehotTime())){
            dto.setMaxBehotTime(Convert.toDate(LocalDateTime.now()));
        }
        if(ObjectUtil.isEmpty(dto.getMinBehotTime())){
            dto.setMinBehotTime(Convert.toDate(LocalDateTime.now()));
        }

        // 二.处理业务
        if(dto.getSize() == null || dto.getSize() == 0){
            dto.setSize(10);
        }
        // 查询条件 频道、状态：已发布【配置表中不下架、不删除】、降序：发布时间
        // 两表连查
        List<ApArticle> list = articleMapper.selectList(type,dto);
        // 三.封装数据
        return ResponseResult.okResult(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult batchSave(ArticleDto dto){
        //校验参数
        if(ObjectUtil.isEmpty(dto)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApArticle article = BeanUtil.toBean(dto, ApArticle.class);
        //保存配置信息
        if (dto.getId() == null) {
            //回填主键
            articleMapper.insert(article);

            ApArticleConfig apArticleConfig = new ApArticleConfig(article.getId());
            int testC = articleConfigMapper.insert(apArticleConfig);
            if(testC==0){
                throw new CustomException(AppHttpCodeEnum.APP_ARTICLE_CONFIG_INSERT_ERROR);
            }
            //保存内容信息
            ApArticleContent apArticleContent = ApArticleContent.builder()
                    .articleId(article.getId())
                    .content(dto.getContent())
                    .build();
            int testCon = articleContentMapper.insert(apArticleContent);
            if(testCon==0){
                throw new CustomException(AppHttpCodeEnum.APP_ARTICLE_CONTENT_INSERT_ERROR);
            }

            //异步生成页面
            try {
                articleStaticGenerater.generatePage(article.getId());
            } catch (TemplateException | IOException e) {
                throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            }
        }else{
            //回填主键
            articleMapper.updateById(article);

            //更新内容信息
            ApArticleContent apArticleContent = ApArticleContent.builder()
                    .articleId(article.getId())
                    .content(dto.getContent())
                    .build();
            int testCon = articleContentMapper.update(apArticleContent, Wrappers
                    .<ApArticleContent>lambdaUpdate()
                    .eq(ApArticleContent::getArticleId,article.getId()));
            if(testCon==0){
                throw new CustomException(AppHttpCodeEnum.APP_ARTICLE_CONTENT_INSERT_ERROR);
            }

            //异步生成页面
            try {
                articleStaticGenerater.generatePage(article.getId());
            } catch (TemplateException | IOException e) {
                throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
            }
        }

        return ResponseResult.okResult(article.getId());
    }
}
