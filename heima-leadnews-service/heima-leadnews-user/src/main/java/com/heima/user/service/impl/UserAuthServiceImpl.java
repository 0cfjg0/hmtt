package com.heima.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.admin.AdminClient;
import com.heima.common.constants.OperationConstant;
import com.heima.model.admin.pojos.AdUserOperation;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserAuthPageDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.model.wemedia.pojos.WmAuth;
import com.heima.user.Constants.UserConstants;
import com.heima.user.mapper.UserAuthMapper;
import com.heima.user.service.UserAuthService;
import com.heima.user.utils.ThreadLocalUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.ref.WeakReference;

@Service
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, ApUserRealname> implements UserAuthService {

    @Resource
    AdminClient adminClient;

    @Override
    public ResponseResult listAuth(UserAuthPageDto dto) {
        if(ObjectUtil.isEmpty(dto)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        PageResponseResult res = new PageResponseResult(dto.getPage(),dto.getSize(),0);
        LambdaQueryWrapper<ApUserRealname> wrapper = Wrappers.<ApUserRealname>lambdaQuery()
                .eq(dto.getStatus() != null, ApUserRealname::getStatus, dto.getStatus());
        Page page = new Page<ApUserRealname>(dto.getPage(),dto.getSize());
        this.page(page,wrapper);
        res.setTotal(Convert.toInt(page.getTotal()));
        res.setData(page.getRecords());
        AdUserOperation opt = AdUserOperation.builder()
                .description("查询审核用户")
                .userId(Convert.toLong(ThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.SELECT)
                .build();
        adminClient.saveOperation(opt);
        return res;
    }

    @Override
    public ResponseResult authPass(UserAuthPageDto dto) {
        if(ObjectUtil.isEmpty(dto)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUserRealname user = this.getById(dto.getId());
        if(user.getStatus() == UserConstants.USER_AUDIT_SUCCEED){
            return ResponseResult.errorResult(AppHttpCodeEnum.AUDIT_OPERTATION_ERROR);
        }
        user.setStatus(UserConstants.USER_AUDIT_SUCCEED);
        boolean flag = this.updateById(user);
        if(!flag){
            return ResponseResult.errorResult(AppHttpCodeEnum.AUDIT_UPDATE_ERROR);
        }

        AdUserOperation opt = AdUserOperation.builder()
                .description("审核用户通过")
                .userId(Convert.toLong(ThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.UPDATE)
                .build();
        adminClient.saveOperation(opt);
        return ResponseResult.okResult(200,"审核成功");
    }

    @Override
    public ResponseResult authFail(UserAuthPageDto dto) {
        if(ObjectUtil.isEmpty(dto)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUserRealname user = this.getById(dto.getId());
        if(user.getStatus() == UserConstants.USER_AUDIT_FAILED){
            return ResponseResult.errorResult(AppHttpCodeEnum.AUDIT_OPERTATION_ERROR);
        }
        user.setStatus(UserConstants.USER_AUDIT_FAILED);
        boolean flag = this.updateById(user);
        if(!flag){
            return ResponseResult.errorResult(AppHttpCodeEnum.AUDIT_UPDATE_ERROR);
        }

        AdUserOperation opt = AdUserOperation.builder()
                .description("审核用户失败,理由: "+dto.getMsg())
                .userId(Convert.toLong(ThreadLocalUtils.getCurrentUser()))
                .type(OperationConstant.UPDATE)
                .build();
        adminClient.saveOperation(opt);
        return ResponseResult.okResult(200,"审核成功");
    }

}
