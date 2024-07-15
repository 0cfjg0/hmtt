package com.heima.model.user.dtos;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

/**
 * 登录DTO
 *
 * @author admin
 * @name LoginDto
 * @date 2022-08-11 21:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("登录信息传输实体")
public class LoginDto {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码，是未加密的
     */
    private String password;


    /**
     * 用户名
     */
    private String name;

}