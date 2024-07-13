package com.heima.wemedia.constant;

import io.swagger.models.auth.In;

public class NewsConstants {

    public final static Short DRAFT_STATUS = 0;

    public final static Short COLLECT_MATERIAL = 1;//收藏

    public final static Short CANCEL_COLLECT_MATERIAL = 0;//取消收藏

    public final static String WM_NEWS_TYPE_IMAGE = "image";
    public final static String WM_NEWS_TYPE_TEXT = "content";

    public final static Short WM_NEWS_NONE_IMAGE = 0;
    public final static Short WM_NEWS_SINGLE_IMAGE = 1;
    public final static Short WM_NEWS_MANY_IMAGE = 3;
    public final static Short WM_NEWS_TYPE_AUTO = -1;

    public final static Short WM_CONTENT_REFERENCE = 0;
    public final static Short WM_COVER_REFERENCE = 1;

    public final static String SUCCESS_NOT_PUBLISHED = "审核成功待发布";
    public final static String SUCCESS = "发布成功";
//    public final static Short WM_COVER_REFERENCE = 1;
//    public final static Short WM_COVER_REFERENCE = 1;

    public final static Short CHECK_SUCCESS = 1;
    public final static Short CHECK_DOUBTFUL = 2;
    public final static Short CHECK_FAILED = 3;

    public final static Short UP = 1;//上架
    public final static Short DOWN = 0;//下架
}
