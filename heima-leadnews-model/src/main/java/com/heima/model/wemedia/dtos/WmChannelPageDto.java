package com.heima.model.wemedia.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WmChannelPageDto {
    /**
     * 敏感词名字
     */
    private String name;

    private Integer page;

    private Integer size;
}
