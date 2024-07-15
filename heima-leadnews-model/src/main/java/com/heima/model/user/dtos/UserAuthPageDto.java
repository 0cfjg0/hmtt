package com.heima.model.user.dtos;

import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author CFJG
 */
@Data
@Builder
public class UserAuthPageDto {

    private Integer id;

    private String msg;

    private Integer page;

    private Integer size;

    private Integer status;

}
