package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.article.IArticleClient;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.constant.NewsConstants;
import com.heima.wemedia.mapper.WMRelationMapper;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmAuditService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.utils.WMThreadLocalUtils;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ghy
 * @version 1.0.1
 * @date 2024-07-06 17:48:19
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Resource
    WmNewsMapper wmNewsMapper;

    @Resource
    WMRelationMapper wmRelationMapper;

    @Resource
    WmMaterialMapper wmMaterialMapper;

    @Resource
    WmAuditService wmAuditService;

    @Resource
    IArticleClient articleClient;

    @Resource
    RedissonClient redissonClient;

    private static final Integer TEST_ID = 1;

    @Override
    public ResponseResult findPage(WmNewsPageReqDto dto) {
        // 一.校验参数
        if(dto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer wmUserId = WMThreadLocalUtils.getCurrentUser();
        if(wmUserId == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        dto.checkParam();
        // 二.处理业务
        // 分页
        IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
        // 条件
        Short status = dto.getStatus();
        Date begin = dto.getBeginPubDate();
        Date end = dto.getEndPubDate();
        Integer channelId = dto.getChannelId();
        String keyword = dto.getKeyword();
        this.lambdaQuery()
                .eq(status != null, WmNews::getStatus, status)
                .between(begin != null && end != null, WmNews::getPublishTime, begin, end)
                .eq(channelId != null, WmNews::getChannelId, channelId)
                .like(StringUtils.isNotBlank(keyword), WmNews::getTitle, keyword)
                .orderByDesc(WmNews::getCreatedTime)
                .page(page);
        // 三.封装数据
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }

    /**
     * 提交文章
     * @param dto
     * @return
     */
    @Override
//    @GlobalTransactional
    @Transactional
    public ResponseResult submit(WmNewsDto dto) {
        if(ObjectUtil.isEmpty(dto)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        //如果dto中存在id则为修改没有为新增
        if(dto.getId()!=null){
            //修改,删除关联表中的关系
            this.deleteRelation(dto);
        }

        //获取图片
        List<String> images = dto.getImages();
        //从dto中封装一个新的news对象
        WmNews news = this.createWmNews(dto);
        //新增
        //主键回填
        boolean flag = this.saveOrUpdate(news);

        //给关系填入newsId
        wmRelationMapper.update(WmNewsMaterial.builder().newsId(news.getId()).build(), Wrappers.<WmNewsMaterial>lambdaUpdate().eq(WmNewsMaterial::getNewsId,TEST_ID));

//        if(!flag){
//            throw new CustomException(AppHttpCodeEnum.PUBLISH_ERROR);
//        }
//        //判断是否为草稿
//        if(dto.getStatus().equals(NewsConstants.DRAFT_STATUS)){
//            //草稿无需保存素材关系
//            return ResponseResult.okResult(200,"提交成功");
//        }
        dto.setEnable(NewsConstants.UP);
        this.downOrUpNews(dto);

        //审核
        try {
            wmAuditService.auditMedia(news.getId());
        } catch (Exception e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.AUDIT_ERROR);
        }

        return ResponseResult.okResult(200,"发布成功");
    }

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @Override
    public ResponseResult getDetail(Long id) {
        return ResponseResult.okResult(wmNewsMapper.selectById(id));
    }

    @Override
    public ResponseResult deleteNews(Integer id) {
        LambdaQueryWrapper<WmNews> wrapper = Wrappers.<WmNews>lambdaQuery()
                .eq(WmNews::getId, id);
        this.remove(wrapper);
        return ResponseResult.okResult(200,"删除成功");
    }

    @Override
    public ResponseResult downOrUpNews(WmNewsDto wmNewsDto){
        if(ObjectUtil.isEmpty(wmNewsDto)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        Short enable = wmNewsDto.getEnable();
        WmNews wmNews = BeanUtil.toBean(wmNewsDto,WmNews.class);
        LambdaUpdateWrapper<WmNews> wrapper = Wrappers.<WmNews>lambdaUpdate()
                .set(WmNews::getEnable, enable)
                .eq(WmNews::getId,wmNewsDto.getId());
        this.wmNewsMapper.update(wmNews,wrapper);
        return ResponseResult.okResult(200,"操作成功");
    }

    /**
     * 批量保存素材关系
     */
    private void saveRelations(List<String> list,short type,Integer newsId){
        if (ObjectUtil.isNotEmpty(list)) {
            List<Integer> res = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, list))
                    .stream()
                    .map(item -> item.getId())
                    .collect(Collectors.toList());
            wmRelationMapper.saveRelations(res,type,newsId);
        }
    }

    /**
     * 删除关系
     * @param dto
     */
    private void deleteRelation(WmNewsDto dto) {
        LambdaQueryWrapper<WmNewsMaterial> wrapper = Wrappers.<WmNewsMaterial>lambdaQuery()
                .eq(WmNewsMaterial::getNewsId, dto.getId());
        int flag = wmRelationMapper.delete(wrapper);
//        if(flag==0){
//            //删除失败
//            throw new CustomException(AppHttpCodeEnum.DELETE_ERROR);
//        }
    }

    /**
     * 构建新的news对象
     * @param dto
     * @return
     */
    private WmNews createWmNews(WmNewsDto dto){
        WmNews res = BeanUtil.toBean(dto,WmNews.class);
        //封装userId
        Integer currentUser = WMThreadLocalUtils.getCurrentUser();
        res.setUserId(currentUser);
        //创建时间提交时间
        res.setCreatedTime(Convert.toDate(LocalDateTime.now()));
        res.setPublishTime(Convert.toDate(LocalDateTime.now()));
        //如果dto传入的类型为自动,需要进行内容检索
        if(dto.getType().equals(NewsConstants.WM_NEWS_TYPE_AUTO)){
            //dto中传入内容为String类型
            List<Map> maps = JSONUtil.toList(dto.getContent(), Map.class);
            List<String> imgs = new ArrayList<>();
            for (Map map : maps) {
                    //如果内容中有图片就填入集合
                    if(ObjectUtil.equals(map.get("type"),NewsConstants.WM_NEWS_TYPE_IMAGE)){
                        imgs.add(map.get("value").toString());
                    }
            }
            this.saveRelations(imgs,NewsConstants.WM_CONTENT_REFERENCE,TEST_ID);
//      * 匹配规则：
//      * 1，如果内容图片大于等于1，小于3  单图  type 1
//      * 2，如果内容图片大于等于3  多图  type 3
//      * 3，如果内容没有图片，无图  type 0
            if (ObjectUtil.isNotEmpty(imgs)) {
                switch (imgs.size()){
                    case 0:
                        res.setType(NewsConstants.WM_NEWS_NONE_IMAGE);
                        break;
                    case 1:
                    case 2:
                        res.setType(NewsConstants.WM_NEWS_SINGLE_IMAGE);
                        imgs = imgs.stream().limit(1).collect(Collectors.toList());
                        break;
                    default:
                        res.setType(NewsConstants.WM_NEWS_MANY_IMAGE);
                        imgs = imgs.stream().limit(3).collect(Collectors.toList());
                }
            } else {
                res.setType(NewsConstants.WM_NEWS_NONE_IMAGE);
            }

            res.setImages(StringUtils.join(imgs,","));
            return res;
        }

        //图片列表封装
        List<String> images = dto.getImages();
        res.setImages(StringUtils.join(images,","));

        //保存素材关系
        if(ObjectUtil.isNotEmpty(images) && !dto.getStatus().equals(NewsConstants.DRAFT_STATUS)){
            this.saveRelations(images,NewsConstants.WM_COVER_REFERENCE,TEST_ID);
        }

        return res;
    }


}
