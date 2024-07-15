package com.admin.controller;

import com.admin.service.LoginService;
import com.heima.model.admin.dtos.AdminLoginDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    LoginService loginService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdminLoginDto loginDto){
        return loginService.login(loginDto);
    }

}
