package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.extension.api.R;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ghy
 * @version 1.0.1
 * @date 2024-07-06 17:45:48
 */
@RestController
@RequestMapping("/api/v1/news")
@Slf4j
public class WmNewsController {

    @Resource
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmNewsPageReqDto dto){
        log.info("dto:{}", dto);
        return wmNewsService.findPage(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto dto){
        log.info("dto:{}", dto);
        return wmNewsService.submit(dto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult getDetail(@PathVariable Long id){
        log.info("id:{}",id.toString());
        return wmNewsService.getDetail(id);
    }

    @GetMapping("/del_news/{id}")
    public ResponseResult deleteNews(@PathVariable Integer id){
        log.info("id:{}",id.toString());
        return wmNewsService.deleteNews(id);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUpNews(@RequestBody WmNewsDto wmNewsDto){
        log.info("dto:{}",wmNewsDto);
        return wmNewsService.downOrUpNews(wmNewsDto);
    }

}
