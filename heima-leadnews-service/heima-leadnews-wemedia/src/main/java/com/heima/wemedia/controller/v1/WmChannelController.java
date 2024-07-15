package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.extension.api.R;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmChannelPageDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/del/{id}")
    public ResponseResult delChannels(@PathVariable Integer id){
        return wmChannelService.delChannels(id);
    }

    @PostMapping("/list")
    public ResponseResult listChannels(@RequestBody WmChannelPageDto dto){
        return wmChannelService.listChannels(dto);
    }

    @PostMapping("/save")
    public ResponseResult saveChannels(@RequestBody WmChannelDto dto){
        return wmChannelService.saveChannel(dto);
    }

    @PostMapping("/update")
    public ResponseResult updateChannels(@RequestBody WmChannelDto dto){
        return wmChannelService.updateChannel(dto);
    }
}
