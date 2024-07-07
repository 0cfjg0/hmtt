package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.utils.WMThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ghy
 * @version 1.0.1
 * @date 2024-07-06 17:48:19
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Override
    public ResponseResult findPage(WmNewsPageReqDto dto) {
        // 一.校验参数
        if(dto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer wmUserId = WMThreadLocalUtils.getCurrentUser();
        if(wmUserId == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        dto.checkParam();
        // 二.处理业务
        // 分页
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        // 条件
        Short status = dto.getStatus();
        Date begin = dto.getBeginPubDate();
        Date end = dto.getEndPubDate();
        Integer channelId = dto.getChannelId();
        String keyword = dto.getKeyword();
        this.lambdaQuery()
                .eq(status != null, WmNews::getStatus, status)
                .between(begin != null && end != null, WmNews::getPublishTime, begin, end)
                .eq(channelId != null, WmNews::getChannelId, channelId)
                .like(StringUtils.isNotBlank(keyword), WmNews::getTitle, keyword)
                .orderByDesc(WmNews::getCreatedTime)
                .page(page);
        // 三.封装数据
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }
}
