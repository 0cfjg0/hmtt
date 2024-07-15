package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmChannelPageDto;
import com.heima.model.wemedia.pojos.WmChannel;
import org.springframework.stereotype.Service;

@Service
public interface WmChannelService extends IService<WmChannel> {
    ResponseResult<WmChannel> getChannels();

    ResponseResult delChannels(Integer id);

    ResponseResult listChannels(WmChannelPageDto dto);

    ResponseResult saveChannel(WmChannelDto dto);

    ResponseResult updateChannel(WmChannelDto dto);
}
