package com.heima.user.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.vos.LoginVo;
import com.heima.user.mapper.UserMapper;
import com.heima.user.service.UserService;
import com.heima.utils.common.AppJwtUtil;
import org.apache.zookeeper.Login;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, ApUser> implements UserService{

    @Override
    public ResponseResult login(LoginDto dto) {
        String phone = dto.getPhone();
        String password = dto.getPassword();

        //正常登录
        if (!ObjectUtil.isAllEmpty(password,phone)) {
            //查询用户是否存在
            LambdaQueryWrapper<ApUser> wrapper = Wrappers.<ApUser>lambdaQuery()
                                                        .eq(ApUser::getPhone,phone);
            ApUser res = this.getOne(wrapper);
            //用户不存在
            if(ObjectUtil.isEmpty(res)){
                return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
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
            LoginVo loginVo = LoginVo
                    .builder()
                    .user(ApUser.builder().id(res.getId()).phone(phone).name(res.getName()).build())
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
