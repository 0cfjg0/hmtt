package com.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdminLoginDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import org.springframework.stereotype.Service;

/**
 * @author CFJG
 */
@Service
public interface LoginService extends IService<AdUser> {

    ResponseResult login(AdminLoginDto dto);

}
