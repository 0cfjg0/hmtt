package com.heima.model.wemedia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WmAuditDto {

    //文本
    private String content;

    //图片
    private List<String> imgs;

}
