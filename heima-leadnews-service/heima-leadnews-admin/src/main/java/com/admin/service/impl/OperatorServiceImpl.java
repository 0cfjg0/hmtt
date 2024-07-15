package com.admin.service.impl;

import com.admin.mapper.OperatorMapper;
import com.admin.service.OperatorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.admin.pojos.AdUserOperation;
import org.springframework.stereotype.Service;

@Service
public class OperatorServiceImpl extends ServiceImpl<OperatorMapper, AdUserOperation>  implements OperatorService{

    @Override
    public void saveOperation(AdUserOperation opt) {
        this.save(opt);
    }
}
