package com.heima.wemedia.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.constants.AliyunAuditResultConstants;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmAuditDto;
import com.heima.model.wemedia.dtos.WmContentDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.constant.NewsConstants;
import com.heima.wemedia.service.WmAuditService;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WmAuditServiceimpl implements WmAuditService {

    @Resource
    WmNewsService wmNewsService;

    @Resource
    GreenTextScan greenTextScan;

    @Resource
    GreenImageScan greenImageScan;

    @Resource
    FileStorageService fileStorageService;

    @Override
    public void auditMedia(Integer newsId) {
        //根据id获取文章
        WmNews news = wmNewsService.getById(newsId);
        if(ObjectUtil.isEmpty(news)){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //判断是否为提交状态
        if(ObjectUtil.notEqual(news.getStatus(),WmNews.Status.SUBMIT)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        //获取文章中的文本和图片
        WmAuditDto content = this.getWmContent(news);

        //审核文本
        short flagText = this.auditText(content.getContent());

        //审核图片
        short flagImage = this.auditImage(content.getImgs());

        if(flagText == 3 || flagImage == 3){
            CustomException exception = flagText == 3 ? new CustomException(AppHttpCodeEnum.TEXT_AUDIT_ERROR) : new CustomException(AppHttpCodeEnum.IMAGE_AUDIT_ERROR);
            this.updateNews(news, WmNews.Status.FAIL,);
            throw exception;
        }else if(flagText != 0 || flagImage != 0){

        }

        //判断是否到达发布时间
        if(news.getPublishTime().toInstant().isBefore(Instant.now())){
            //还没到发布时间
            this.updateNews(news, WmNews.Status.SUCCESS,NewsConstants.SUCCESS_NOT_PUBLISHED);
        }else{
            //到达发布时间
            //保存文章到app端
            long articleId = this.saveArticle(news);
            news.setArticleId(articleId);
            this.updateNews(news,WmNews.Status.PUBLISHED,NewsConstants.SUCCESS);
        }
    }

    /**
     * 保存文章到app端
     * @param news
     * @return
     */
    private Integer saveArticle(WmNews news) {
    }

    /**
     * 更新状态
     * @param news
     * @param success
     * @param successNotPublished
     */
    private void updateNews(WmNews news, WmNews.Status status, String reason) {
    }

    /**
     * 审核图片
     * @param imgs
     * @return
     */
    private short auditImage(List<String> imgs) {
        boolean res = true;
        //下载图片
        List<byte[]> submitList = imgs.stream().distinct().map(item -> fileStorageService.downLoadFile(item)).collect(Collectors.toList());
        Map map;
        try {
            //检测图片
            map = greenImageScan.imageScan(submitList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(AppHttpCodeEnum.IMAGE_AUDIT_ERROR);
        }
        String suggestion = map.get("suggestion").toString();
        if(suggestion.equals(AliyunAuditResultConstants.PASS_MESSAGE)){
            return 1;
        }else if(suggestion.equals(AliyunAuditResultConstants.REVIEW_MESSAGE)){
            return 2;
        }else if(suggestion.equals(AliyunAuditResultConstants.BLOCK_MESSAGE)){
            return 3;
        }
        //审核错误
        throw new CustomException(AppHttpCodeEnum.AUDIT_ERROR);
    }

    /**
     * 审核文本
     * 1:通过
     * 2:人工
     * 3:失败
     * @param content
     * @return
     */
    private short auditText(String content) {
        boolean res = true;
        Map<String ,Object> map;
        try {
            map = greenTextScan.greeTextScan(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(AppHttpCodeEnum.TEXT_AUDIT_ERROR);
        }
        String suggestion = map.get("suggestion").toString();
        if(suggestion.equals(AliyunAuditResultConstants.PASS_MESSAGE)){
            return 1;
        }else if(suggestion.equals(AliyunAuditResultConstants.REVIEW_MESSAGE)){
            return 2;
        }else if(suggestion.equals(AliyunAuditResultConstants.BLOCK_MESSAGE)){
            return 3;
        }
        //审核错误
        throw new CustomException(AppHttpCodeEnum.AUDIT_ERROR);
    }

    /**
     * 提取文章内容
     * @param news
     * @return
     */
    private WmAuditDto getWmContent(WmNews news) {
        StringBuffer text = new StringBuffer("");
        List<String> imgs = new ArrayList<>();
        String content = news.getContent();
        //提取内容
        if(StrUtil.isNotBlank(content)){
            //内容不为空
            List<WmContentDto> contents = JSONUtil.toList(content, WmContentDto.class);
            for (WmContentDto wmContentDto : contents) {
                if(wmContentDto.getType().equals(NewsConstants.WM_NEWS_TYPE_TEXT)){
                    text.append(wmContentDto.getValue()).append("--");
                }else if(wmContentDto.getType().equals(NewsConstants.WM_NEWS_TYPE_IMAGE)){
                    imgs.add(wmContentDto.getValue());
                }
            }
        }
        //标签,标题,封面
        text.append(news.getTitle()).append("--").append(news.getLabels()).append("--");
        String images = news.getImages();
        String[] imgArr = images.split(",");
        for (String i : imgArr) {
            imgs.add(i);
        }
        //生成实体
        return new WmAuditDto(text.toString(),imgs);
    }

}
