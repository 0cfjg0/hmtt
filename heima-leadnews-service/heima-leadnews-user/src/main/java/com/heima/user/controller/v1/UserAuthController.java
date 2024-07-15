package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserAuthPageDto;
import com.heima.user.service.UserAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/auth")
public class UserAuthController {

    @Resource
    UserAuthService userAuthService;

    @PostMapping("/list")
    public ResponseResult listAuth(@RequestBody UserAuthPageDto dto){
        return userAuthService.listAuth(dto);
    }

    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody UserAuthPageDto dto){
        return userAuthService.authFail(dto);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody UserAuthPageDto dto){
        return userAuthService.authPass(dto);
    }
}
