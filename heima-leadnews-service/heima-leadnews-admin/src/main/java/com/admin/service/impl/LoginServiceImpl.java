package com.admin.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.admin.mapper.LoginMapper;
import com.admin.service.LoginService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.admin.dtos.AdminLoginDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.admin.vos.AdminLoginVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.vos.LoginVo;
import com.heima.utils.common.AppJwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CFJG
 */
@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper,AdUser> implements LoginService {

    @Override
    public ResponseResult login(AdminLoginDto dto) {
        String name = dto.getName();
        String password = dto.getPassword();

        //正常登录
        if (!ObjectUtil.isAllEmpty(password,name)) {
            //查询用户是否存在
            LambdaQueryWrapper<AdUser> wrapper = Wrappers.<AdUser>lambdaQuery()
                    .eq(AdUser::getName,name);
            AdUser res = this.getOne(wrapper);
            //用户不存在
            if(ObjectUtil.isEmpty(res)){
                return ResponseResult.errorResult(AppHttpCodeEnum.AD_USER_DATA_NOT_EXIST);
            }

            //用户存在比对密码
            //获取盐
            String salt = res.getSalt();
            String usepwd = res.getPassword();
            String pwd = DigestUtils.md5DigestAsHex((salt + password).getBytes());
            //密码错误
            if(ObjectUtil.notEqual(pwd,usepwd)){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //密码正确->封装返回值
            AdminLoginVo loginVo = AdminLoginVo
                    .builder()
                    .user(AdUser.builder()
                            .id(res.getId())
                            .name(res.getName())
                            .email(res.getEmail())
                            .loginTime(new Date())
                            .nickname(res.getNickname())
                            .phone(res.getPhone())
                            .status(res.getStatus())
                            .createdTime(res.getCreatedTime())
                            .build())
                    .token(AppJwtUtil.getToken(Convert.toLong(res.getId())))
                    .build();
            return ResponseResult.okResult(loginVo);
        }else
        //游客登录
        {
            Map<String,Object> data = new HashMap<>();
            data.put("token",AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(data);
        }
    }
}
