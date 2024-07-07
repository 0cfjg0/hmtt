package com.heima.article.service.impl;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
