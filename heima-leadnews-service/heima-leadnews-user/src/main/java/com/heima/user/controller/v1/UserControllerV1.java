package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "用户登录")
@RestController
@RequestMapping("/api/v1/login")
public class UserControllerV1 {

    @Resource
    UserService userService;

    @ApiOperation("用户登录接口")
    @PostMapping("/login_auth")
    @ApiModelProperty(name = "loginDto",value = "登录数据传输载体")
    public ResponseResult login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

}
