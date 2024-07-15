package com.heima.model.user.pojos;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("ap_user_realname")
public class ApUserRealname {
  @TableId(value = "id" , type = IdType.AUTO)
  private Long id;

  @TableField("user_id")
  private long userId;

  @TableField("name")
  private String name;

  @TableField("idno")
  private String idno;

  @TableField("font_image")
  private String fontImage;

  @TableField("back_image")
  private String backImage;

  @TableField("hold_image")
  private String holdImage;

  @TableField("live_image")
  private String liveImage;

  @TableField("status")
  private Long status;

  @TableField("reason")
  private String reason;

  @TableField("created_time")
  private LocalDateTime createdTime;

  @TableField("submited_time")
  private LocalDateTime submitedTime;

  @TableField("updated_time")
  private LocalDateTime updatedTime;

}
