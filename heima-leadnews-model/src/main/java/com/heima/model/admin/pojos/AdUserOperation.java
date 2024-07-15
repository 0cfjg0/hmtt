package com.heima.model.admin.pojos;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("ad_user_opertion")
@Data
@Builder
public class AdUserOperation {
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    // 主键ID

    @TableField(value = "user_id")
    private Long userId;
    // 用户ID

    @TableField(value = "equipment_id")
    private Long equipmentId;
    // 登录设备ID

    @TableField(value = "ip")
    private String ip;
    // 登录IP

    @TableField(value = "address")
    private String address;
    // 登录地址

    @TableField(value = "type")
    private Integer type;
    // 操作类型，例如：1表示新增，2表示修改，3表示删除

    @TableField(value = "description")
    private String description;
    // 操作描述，例如：用户登录，修改密码等

    @TableField(value = "created_time",fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    // 登录时间，格式为yyyy-MM-dd HH:mm:ss

}