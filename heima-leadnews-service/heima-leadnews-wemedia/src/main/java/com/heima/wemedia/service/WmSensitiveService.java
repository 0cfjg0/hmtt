package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.vos.WmSensitiveDto;
import org.springframework.stereotype.Service;

@Service
public interface WmSensitiveService extends IService<WmSensitive> {
    ResponseResult delete(Integer id);

    ResponseResult sensitiveList(WmSensitivePageDto dto);

    ResponseResult saveSensitive(WmSensitiveDto dto);

    ResponseResult updateSensitive(WmSensitiveDto dto);
}
