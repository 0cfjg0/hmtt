package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserAuthPageDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import org.springframework.stereotype.Service;

/**
 * @author CFJG
 */
@Service
public interface UserAuthService extends IService<ApUserRealname> {
    ResponseResult listAuth(UserAuthPageDto dto);

    ResponseResult authPass(UserAuthPageDto dto);

    ResponseResult authFail(UserAuthPageDto dto);
}
