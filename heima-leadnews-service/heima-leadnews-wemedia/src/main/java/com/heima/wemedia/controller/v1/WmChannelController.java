package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Resource
    WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult<WmChannel> getChannels(){
        return wmChannelService.getChannels();
    }

}
