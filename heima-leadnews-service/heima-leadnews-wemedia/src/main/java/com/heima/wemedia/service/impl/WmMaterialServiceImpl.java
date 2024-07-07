package com.heima.wemedia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.file.service.impl.MinIOFileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import com.heima.wemedia.utils.WMThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author ghy
 * @version 1.0.1
 * @date 2024-07-06 14:59:55
 */
@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Resource
    private MinIOFileStorageService fileStorageService;

    @Resource
    WmMaterialMapper wmMaterialMapper;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        // 一.校验参数
        if(ObjectUtil.isEmpty(multipartFile)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 二.处理业务
        // 1.文件重命名
        String filename = multipartFile.getOriginalFilename();
        //后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        filename = uuid.concat(suffix);
        // 2.上传
        String url;
        try {
            url = fileStorageService.uploadImgFile("", filename, multipartFile.getInputStream());
        } catch (IOException e) {
            throw new CustomException(AppHttpCodeEnum.FILE_UPLOAD_ERROR);
        }
        // 三.封装数据
        Integer wmUserId = WMThreadLocalUtils.getCurrentUser();
        if(wmUserId == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(wmUserId);
        wmMaterial.setUrl(url);
        wmMaterial.setType((short)0);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult findPage(WmMaterialDto dto) {
        // 一.校验参数
        if(dto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页参数校验
        dto.checkParam();
        Integer wmUserId = WMThreadLocalUtils.getCurrentUser();
        if(wmUserId == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 二.处理业务
        // 分页
        IPage<WmMaterial> pages = new Page<>(dto.getPage(), dto.getSize());
        //是否收藏 1是 0全部
        Short isCollection = dto.getIsCollection();
        LambdaQueryWrapper<WmMaterial> wrapper = Wrappers.<WmMaterial>lambdaQuery()
                .eq(isCollection == 1, WmMaterial::getIsCollection, isCollection)
                .eq(WmMaterial::getUserId, wmUserId)
                .orderByDesc(WmMaterial::getCreatedTime);
        IPage<WmMaterial> res = wmMaterialMapper.selectPage(pages, wrapper);
        // 三.封装数据
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) res.getTotal());
        pageResponseResult.setData(res.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult collect(Long materialId) {
        if(ObjectUtil.isEmpty(materialId)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer userId = WMThreadLocalUtils.getCurrentUser();
        if(ObjectUtil.isEmpty(userId)){
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        //业务
        try {
            LambdaQueryWrapper<WmMaterial> wrapper = Wrappers.<WmMaterial>lambdaQuery()
                    .eq(WmMaterial::getUserId, userId)
                    .eq(WmMaterial::getId, materialId);
            WmMaterial res = wmMaterialMapper.selectById(materialId);
            res.setIsCollection((short) 1);
            wmMaterialMapper.update(res,wrapper);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(200,"成功");
    }

    @Override
    @Transactional
    public ResponseResult deleteMaterial(Long materialId) {
        if(ObjectUtil.isEmpty(materialId)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer userId = WMThreadLocalUtils.getCurrentUser();
        if(ObjectUtil.isEmpty(userId)){
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        //业务
        //先删除表记录
        WmMaterial res = wmMaterialMapper.selectById(materialId);
        String url = res.getUrl();
        LambdaQueryWrapper<WmMaterial> wrapper = Wrappers.<WmMaterial>lambdaQuery()
                .eq(WmMaterial::getId, materialId)
                .eq(WmMaterial::getUserId, userId);
        try {
            wmMaterialMapper.delete(wrapper);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        //再删除图片
        try {
            fileStorageService.delete(url);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(200,"删除成功");
    }
}