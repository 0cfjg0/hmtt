package com.heima.wemedia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.utils.WMThreadLocalUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {

    @Resource
    WmChannelMapper wmChannelMapper;

    @Override
    public ResponseResult<WmChannel> getChannels() {
        Integer userId = WMThreadLocalUtils.getCurrentUser();
        if(ObjectUtil.isEmpty(userId)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        //业务
        List<WmChannel> res = wmChannelMapper.selectList(Wrappers.<WmChannel>lambdaQuery());
        return ResponseResult.okResult(res);
    }
}
