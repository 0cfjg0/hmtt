package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.constants.AliyunAuditResultConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmAuditDto;
import com.heima.model.wemedia.dtos.WmContentDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.constant.NewsConstants;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmAuditService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.net.nntp.Article;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.awt.windows.WWindowPeer;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author CFJG
 */
@Service
@Slf4j
public class WmAuditServiceimpl implements WmAuditService{


    @Resource
    WmNewsMapper wmNewsMapper;

    @Resource
    WmUserMapper wmUserMapper;

    @Resource
    IArticleClient articleClient;

    @Resource
    WmChannelMapper wmChannelMapper;

    @Resource
    GreenTextScan greenTextScan;

    @Resource
    GreenImageScan greenImageScan;


    @Resource
    FileStorageService fileStorageService;

    @Resource
    Tess4jClient tess4jClient;

    @Resource
    WmSensitiveMapper wmSensitiveMapper;

    @Resource
    RedissonClient redissonClient;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void auditMedia(Integer newsId) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //根据id获取文章
        WmNews news = wmNewsMapper.selectById(newsId);
        if(ObjectUtil.isEmpty(news)){
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //判断是否为提交状态
        if(!news.getStatus().equals(WmNews.Status.SUBMIT.getCode())){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        //获取文章中的文本和图片
        WmAuditDto content = this.getWmContent(news);

        //进行自管理过滤敏感词
        short flag = sensitiveFilter(content.getContent(), news);
        if(flag == NewsConstants.CHECK_FAILED){
            throw new CustomException(AppHttpCodeEnum.AUDIT_ERROR);
        }



//        //审核文本
//        short flagText = this.auditText(content.getContent());

//        //审核图片
        short flagImage = this.auditImage(content.getImgs(),news);
        if (flagImage == NewsConstants.CHECK_FAILED){
            throw new CustomException(AppHttpCodeEnum.IMAGE_AUDIT_ERROR);
        }

//        if(flagText == 3 || flagImage == 3){
//            CustomException exception = flagText == 3 ? new CustomException(AppHttpCodeEnum.TEXT_AUDIT_ERROR) : new CustomException(AppHttpCodeEnum.IMAGE_AUDIT_ERROR);
//            this.updateNews(news, WmNews.Status.FAIL,exception.getMessage());
//            throw exception;
//        }else if(flagText != 0 || flagImage != 0){
//            CustomException exception = flagText == 2 ? new CustomException(AppHttpCodeEnum.TEXT_AUDIT_CHECK_ERROR) : new CustomException(AppHttpCodeEnum.IMAGE_AUDIT_CHECK_ERROR);
//            this.updateNews(news, WmNews.Status.FAIL,exception.getMessage());
//            throw exception;
//        }

        //判断是否到达发布时间
        if(news.getPublishTime().toInstant().isAfter(Instant.now())){
            updateNews(news,WmNews.Status.SUCCESS,NewsConstants.SUCCESS_NOT_PUBLISHED);
            //todo 定时发布
            //redisson延迟任务方式
            //计算差值时间
            long publishEpoch = news.getPublishTime().toInstant().toEpochMilli();
            long currentEpoch = System.currentTimeMillis();
            long duration = currentEpoch - publishEpoch;

            //创建redisson延迟队列和阻塞队列
            RBlockingQueue<Integer> blockingQueue = redissonClient.getBlockingQueue("publishBlockingQueue");
            RDelayedQueue<Integer> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
            delayedQueue.offer(news.getId(), duration, TimeUnit.MILLISECONDS);

//            Message message = MessageBuilder
//                    .withBody(news.getId().toString().getBytes(StandardCharsets.UTF_8))
//                    .setExpiration(Convert.toStr(duration))
//                    .build();
//
//            rabbitTemplate.convertAndSend("news.direct","article",message);
        }else{
            //到达发布时间
            //保存文章到app端
            long articleId = saveArticle(news);
            news.setArticleId(articleId);
            updateNews(news,WmNews.Status.PUBLISHED,NewsConstants.SUCCESS);
        }
    }

    //自敏感词过滤
    private short sensitiveFilter(String content, WmNews wmNews) {
        //过滤
        //取出敏感词列表
        Set<String> tmpSet = wmSensitiveMapper.selectList(null).stream().map(item -> item.getSensitives()).collect(Collectors.toSet());
        SensitiveWordUtil.initMap(tmpSet);
        Map<String, Integer> res = SensitiveWordUtil.matchWords(content);
        if(res.size()>0){
            //存在敏感词
            this.updateNews(wmNews, WmNews.Status.FAIL,res.keySet().toString());
            log.warn("存在敏感词");
            return NewsConstants.CHECK_FAILED;
        }
        return NewsConstants.CHECK_SUCCESS;
    }

    /**
     * 保存文章到app端
     * @param news
     * @return
     */
    @Override
    public Long saveArticle(WmNews news) {
        //保存到app端文章
        ArticleDto dto = BeanUtil.toBean(news, ArticleDto.class, CopyOptions.create(ArticleDto.class,true,"id"));
        dto.setContent(news.getContent());
        //作者
        dto.setAuthorId(news.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(news.getUserId());
        if(ObjectUtil.isNotEmpty(wmUser)){
            dto.setAuthorName(wmUser.getName());
        }
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(news.getChannelId());
        dto.setChannelName(wmChannel.getName());

        dto.setFlag((byte) 0);
        dto.setLayout(news.getType());

        //远程调用
        ResponseResult responseResult = articleClient.saveArticle(dto);
        if(responseResult.getCode() == 200){
            return (Long) responseResult.getData();
        }else{
            throw new CustomException(AppHttpCodeEnum.APP_ARTICLE_INSERT_ERROR);
        }
    }

    /**
     * 更新状态
     * @param news
     */
    @Override
    public void updateNews(WmNews news, WmNews.Status status, String reason) {
        LambdaUpdateWrapper<WmNews> wrapper = Wrappers.<WmNews>lambdaUpdate()
                .set(WmNews::getStatus, status.getCode())
                .set(WmNews::getReason, reason)
                .eq(WmNews::getId, news.getId());
        int update = wmNewsMapper.update(news, wrapper);
        if(update==0){
            throw new CustomException(AppHttpCodeEnum.NEWS_UPDATE_ERROR);
        }
    }

    /**
     * 审核图片
     * @param imgs
     * @return
     */
    private short auditImage(List<String> imgs,WmNews wmNews) {
        boolean res = true;
        //下载图片
        List<byte[]> submitList = imgs.stream().filter(item-> !item.equals("")).distinct().map(item -> fileStorageService.downLoadFile(item)).collect(Collectors.toList());
        //提取图片中文字进行审核
        String text = null;
        try {
            for (byte[] bytes : submitList) {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
                text = tess4jClient.doOCR(image);
                short flag = sensitiveFilter(text, wmNews);
            }
        } catch (IOException | TesseractException e) {
            log.warn("图片中检测到非法字符:{}",text);
            this.updateNews(wmNews, WmNews.Status.FAIL,"图片中检测到非法字符"+text);
            return NewsConstants.CHECK_FAILED;
        }
        Map map;
//        try {
//            //检测图片
//            map = greenImageScan.imageScan(submitList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomException(AppHttpCodeEnum.IMAGE_AUDIT_ERROR);
//        }
//        String suggestion = map.get("suggestion").toString();
//        if(suggestion.equals(AliyunAuditResultConstants.PASS_MESSAGE)){
//            return 1;
//        }else if(suggestion.equals(AliyunAuditResultConstants.REVIEW_MESSAGE)){
//            return 2;
//        }else if(suggestion.equals(AliyunAuditResultConstants.BLOCK_MESSAGE)){
//            return 3;
//        }
//        //审核错误
//        throw new CustomException(AppHttpCodeEnum.AUDIT_ERROR);
        return NewsConstants.CHECK_SUCCESS;
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
