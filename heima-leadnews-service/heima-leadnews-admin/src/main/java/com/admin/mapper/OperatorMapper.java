package com.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.admin.pojos.AdUserOperation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperatorMapper extends BaseMapper<AdUserOperation> {
}