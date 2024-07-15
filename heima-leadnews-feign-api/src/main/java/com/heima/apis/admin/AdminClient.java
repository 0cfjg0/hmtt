package com.heima.apis.admin;

import com.heima.model.admin.pojos.AdUserOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "leadnews-admin")
public interface AdminClient {

    @PostMapping("/saveOperation")
    @Async
    void saveOperation(AdUserOperation opt);

}
