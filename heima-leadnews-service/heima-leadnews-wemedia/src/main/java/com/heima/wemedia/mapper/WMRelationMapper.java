package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WMRelationMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 保存关系
     * @param list
     * @param type
     * @param newsId
     */
    void saveRelations(@Param("materialIds") List<Integer> list, @Param("type") short type, @Param("newsId") Integer newsId);

}
