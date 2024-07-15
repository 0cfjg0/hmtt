package com.heima.model.wemedia.dtos;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WmSensitivePageDto {

    /**
     * 敏感词名字
     */
    private String name;

    private Integer page;

    private Integer size;
}
