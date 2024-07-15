package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.admin.AdminClient;
import com.heima.common.constants.OperationConstant;
import com.heima.model.admin.pojos.AdUserOperation;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmSensitivePageDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.vos.WmSensitiveDto;
import com.heima.model.wemedia.vos.WmSensitiveVo;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import com.heima.wemedia.utils.WMThreadLocalUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    @Resource
    WmSensitiveMapper wmSensitiveMapper;

    @Resource
    AdminClient adminClient;

    @Override
    public ResponseResult delete(Integer id) {
        int i = wmSensitiveMapper.deleteById(id);
        if(i==0){
            return ResponseResult.errorResult(200,"删除失败");
        }
        AdUserOperation opt = AdUserOperation.builder()
                .description("删除敏感词")
                .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.DELETE)
                .build();
        adminClient.saveOperation(opt);
        return ResponseResult.okResult(200,"删除成功");
    }

    @Override
    public ResponseResult<PageResponseResult> sensitiveList(WmSensitivePageDto dto) {
        Page page = new Page(dto.getPage(),dto.getSize());
        Page res = null;
        try {
            res = wmSensitiveMapper.selectPage(page, Wrappers.lambdaQuery());
            List<WmSensitive> sensitives = res.getRecords();
            List<WmSensitiveVo> resVo = sensitives.stream().map(item -> new WmSensitiveVo(item.getId(), item.getSensitives(), item.getCreatedTime().getTime())).collect(Collectors.toList());
            page.setRecords(resVo);
        } catch (Exception e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SENSITIVE_LIST_ERROR);
        }

        PageResponseResult resVo = new PageResponseResult(Convert.toInt(page.getCurrent())
                ,Convert.toInt(page.getSize())
                ,Convert.toInt(page.getTotal()));

        resVo.setData(page.getRecords());

        AdUserOperation opt = AdUserOperation.builder()
                .description("查询敏感词")
                .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.SELECT)
                .build();
        adminClient.saveOperation(opt);
        return resVo;
    }

    @Override
    public ResponseResult saveSensitive(WmSensitiveDto dto) {
        WmSensitive sensitive = BeanUtil.toBean(dto, WmSensitive.class);
        sensitive.setCreatedTime(new Date());
        boolean flag = this.save(sensitive);
        if(flag){
            AdUserOperation opt = AdUserOperation.builder()
                    .description("保存敏感词")
                    .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                    .type(OperationConstant.INSERT)
                    .build();
            adminClient.saveOperation(opt);
            return ResponseResult.okResult(200,"保存成功");
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SENSITIVE_SAVE_ERROR);
        }
    }

    @Override
    public ResponseResult updateSensitive(WmSensitiveDto dto) {
        WmSensitive sensitive = BeanUtil.toBean(dto, WmSensitive.class);
        sensitive.setCreatedTime(new Date());
        boolean flag = this.updateById(sensitive);
        if(flag){
            AdUserOperation opt = AdUserOperation.builder()
                    .description("更新敏感词")
                    .userId(Convert.toLong(WMThreadLocalUtils.getCurrentUser()))
                    .type(OperationConstant.UPDATE)
                    .build();
            adminClient.saveOperation(opt);
            return ResponseResult.okResult(200,"更新成功");
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SENSITIVE_UPDATE_ERROR);
        }
    }
}
