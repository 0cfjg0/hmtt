package com.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.pojos.AdUserOperation;
import org.springframework.stereotype.Service;

@Service
public interface OperatorService extends IService<AdUserOperation> {
    void saveOperation(AdUserOperation opt);
}
