package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.vos.WmSensitiveDto;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Resource
    WmSensitiveService wmSensitiveService;

    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id){
        return wmSensitiveService.delete(id);
    }

    @PostMapping("/list")
    ResponseResult<WmSensitive> sensitiveList(@RequestBody WmSensitivePageDto dto){
        return wmSensitiveService.sensitiveList(dto);
    }

    @PostMapping("/save")
    ResponseResult save(@RequestBody WmSensitiveDto dto){
        return wmSensitiveService.saveSensitive(dto);
    }
    @PostMapping("update")
    ResponseResult update(@RequestBody WmSensitiveDto dto){
        return wmSensitiveService.updateSensitive(dto);
    }
}
