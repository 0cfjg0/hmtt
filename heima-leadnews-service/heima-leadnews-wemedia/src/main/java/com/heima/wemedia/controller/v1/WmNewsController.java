package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        log.warn("dto:{}", dto);
        return wmNewsService.findPage(dto);
    }

}
