package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.admin.AdminClient;
import com.heima.common.constants.OperationConstant;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.pojos.AdUserOperation;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmChannelPageDto;
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

    @Resource
    AdminClient adminClient;

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

    @Override
    public ResponseResult delChannels(Integer id) {
        boolean flag = this.removeById(id);
        if(!flag){
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_DELETE_ERROR);
        }
        AdUserOperation opt = AdUserOperation.builder()
                .description("删除频道")
                .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.DELETE)
                .build();
        adminClient.saveOperation(opt);
        return ResponseResult.okResult(200,"删除成功");
    }

    @Override
    public ResponseResult listChannels(WmChannelPageDto dto) {
        LambdaQueryWrapper<WmChannel> wrapper = Wrappers.<WmChannel>lambdaQuery()
                .like(dto.getName()!=null, WmChannel::getName, dto.getName());
        Page page = new Page(dto.getPage(),dto.getSize());
        try {
            this.page(page,wrapper);
        } catch (Exception e) {
            return PageResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_LIST_ERROR);
        }
        PageResponseResult res = new PageResponseResult(dto.getPage(),dto.getSize(), Convert.toInt(page.getTotal()));
        res.setData(page.getRecords());
        AdUserOperation opt = AdUserOperation.builder()
                .description("查询频道")
                .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.SELECT)
                .build();
        adminClient.saveOperation(opt);
        return res;
    }

    @Override
    public ResponseResult saveChannel(WmChannelDto dto) {
        WmChannel wmChannel = BeanUtil.toBean(dto, WmChannel.class);
        boolean flag = this.save(wmChannel);
        if(!flag){
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_SAVE_ERROR);
        }
        AdUserOperation opt = AdUserOperation.builder()
                .description("保存频道")
                .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.INSERT)
                .build();
        adminClient.saveOperation(opt);
        return ResponseResult.okResult(200,"保存成功");
    }

    @Override
    public ResponseResult updateChannel(WmChannelDto dto) {
        WmChannel wmChannel = BeanUtil.toBean(dto, WmChannel.class);
        boolean flag = this.updateById(wmChannel);
        if(!flag){
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_UPDATE_ERROR);
        }
        AdUserOperation opt = AdUserOperation.builder()
                .description("更新频道")
                .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.UPDATE)
                .build();
        adminClient.saveOperation(opt);
        return ResponseResult.okResult(200,"修改成功");
    }


}
