package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.news.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章
     * @param dto
     * @return
     */
    public ResponseResult findPage(WmNewsPageReqDto dto);

    public ResponseResult submit(WmNewsDto dto);

    ResponseResult getDetail(Long id);

    ResponseResult deleteNews(Integer id);

    ResponseResult downOrUpNews(WmNewsDto wmNewsDto);

    ResponseResult listNews(NewsAuthDto dto);

    ResponseResult auditFail(NewsAuthDto dto);

    ResponseResult auditPass(NewsAuthDto dto);
}