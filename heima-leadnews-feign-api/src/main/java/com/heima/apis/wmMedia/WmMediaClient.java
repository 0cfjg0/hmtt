package com.heima.apis.wmMedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author CFJG
 */
@FeignClient(value = "leadnews-wemedia")
public interface WmMediaClient {

    @DeleteMapping("/api/v1/sensitive/delete")
    ResponseResult delete(Integer id);

    @PostMapping("/api/v1/sensitive/list")
    ResponseResult sensitiveList(@RequestBody WmSensitivePageDto dto);
}
