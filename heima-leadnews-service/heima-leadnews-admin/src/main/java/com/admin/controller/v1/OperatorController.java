package com.admin.controller.v1;

import com.admin.service.OperatorService;
import com.heima.model.admin.pojos.AdUserOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class OperatorController {

    @Resource
    OperatorService operatorService;

    @PostMapping ("/saveOperation")
    public void saveOperation (@RequestBody AdUserOperation opt){
        operatorService.saveOperation(opt);
    }
}
