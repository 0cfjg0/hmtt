package com.heima.model.news;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsAuthDto {

    private Integer id;

    private String msg;

    private Integer page;

    private Integer size;

    private Integer status;

    private String title;

}