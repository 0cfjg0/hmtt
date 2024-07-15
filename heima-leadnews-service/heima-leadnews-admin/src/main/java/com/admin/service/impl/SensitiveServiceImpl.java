package com.admin.service.impl;

import com.admin.service.SensitiveService;
import com.heima.apis.wmMedia.WmMediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SensitiveServiceImpl implements SensitiveService {

    @Resource
    WmMediaClient wmMediaClient;

    @Override
    public ResponseResult delete(Integer id) {
        return wmMediaClient.delete(id);
    }

    @Override
    public ResponseResult list(WmSensitivePageDto dto) {
        return wmMediaClient.sensitiveList(dto);
    }
}
