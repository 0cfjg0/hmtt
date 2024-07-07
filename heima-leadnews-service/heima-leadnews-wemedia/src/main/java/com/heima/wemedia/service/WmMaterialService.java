package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 上传素材
     * @param multipartFile
     * @return 数据：素材详细信息
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    ResponseResult findPage(WmMaterialDto dto);

    ResponseResult collect(Long materialId);

    ResponseResult deleteMaterial(Long materialId);
}