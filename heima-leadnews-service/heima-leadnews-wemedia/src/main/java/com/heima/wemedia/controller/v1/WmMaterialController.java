package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
@Slf4j
public class WmMaterialController {

    @Autowired
    private WmMaterialService wemediaService;

    /**
     * 上传素材
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        log.info("multipartFile:{}", multipartFile);
        return wemediaService.uploadPicture(multipartFile);
    }

    /**
     * 分页显示素材
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmMaterialDto dto){
        log.info("dto:{}", dto);
        return wemediaService.findPage(dto);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable("id") Long materialId){
        log.info("id:{}",materialId.toString());
        return wemediaService.collect(materialId);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult deleteMaterial(@PathVariable("id") Long materialId){
        log.info("id:{}",materialId.toString());
        return wemediaService.deleteMaterial(materialId);
    }


}