package com.admin.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import org.springframework.stereotype.Service;

@Service
public interface SensitiveService {
    ResponseResult delete(Integer id);

    ResponseResult list(WmSensitivePageDto dto);
}
