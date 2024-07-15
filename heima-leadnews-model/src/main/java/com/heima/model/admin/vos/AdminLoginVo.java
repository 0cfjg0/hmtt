package com.heima.model.admin.vos;

import com.heima.model.admin.pojos.AdUser;
import com.heima.model.user.pojos.ApUser;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录DTO
 *
 * @author cfjg
 * @name LoginDto
 * @date 2022-08-11 21:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("登录信息返回实体")
public class AdminLoginVo {

    private AdUser user;

    private String token;

}