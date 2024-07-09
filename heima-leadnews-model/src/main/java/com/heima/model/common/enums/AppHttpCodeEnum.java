package com.heima.model.common.enums;

/**
 * 定义异常的错误信息
 */
public enum AppHttpCodeEnum {

    // 成功段0
    SUCCESS(200,"操作成功"),
    // 登录段1~50
    NEED_LOGIN(1,"需要登录后操作"),
    LOGIN_PASSWORD_ERROR(2,"密码错误"),
    // TOKEN50~100
    TOKEN_INVALID(50,"无效的TOKEN"),
    TOKEN_EXPIRE(51,"TOKEN已过期"),
    TOKEN_REQUIRE(52,"TOKEN是必须的"),
    // SIGN验签 100~120
    SIGN_INVALID(100,"无效的SIGN"),
    SIG_TIMEOUT(101,"SIGN已过期"),
    // 参数错误 500~1000
    PARAM_REQUIRE(500,"缺少参数"),
    PARAM_INVALID(501,"无效参数"),
    PARAM_IMAGE_FORMAT_ERROR(502,"图片格式有误"),
    SERVER_ERROR(503,"服务器内部错误"),
    // 数据错误 1000~2000
    DATA_EXIST(1000,"数据已经存在"),
    AP_USER_DATA_NOT_EXIST(1001,"ApUser数据不存在"),
    DATA_NOT_EXIST(1002,"数据不存在"),
    FILE_UPLOAD_ERROR(1003,"文件上传错误"),
    // 数据错误 3000~3500
    NO_OPERATOR_AUTH(3000,"无权限操作"),
    NEED_ADMIN(3001,"需要管理员权限"),

    DELETE_ERROR(10001,"删除错误"),
    PUBLISH_ERROR(10002,"发布失败"),
    TEXT_AUDIT_ERROR(10003,"文本审核失败"),
    IMAGE_AUDIT_ERROR(10004,"图片审核失败"),
    TEXT_AUDIT_CHECK_ERROR(10003,"文本需要人工审核"),
    IMAGE_AUDIT_CHECK_ERROR(10004,"图片需要人工审核"),
    AUDIT_ERROR(10005,"审核异常"),
    APP_ARTICLE_INSERT_ERROR(10006,"文章保存异常"),
    APP_ARTICLE_CONTENT_INSERT_ERROR(10007,"文章内容保存异常"),
    APP_ARTICLE_CONFIG_INSERT_ERROR(10008,"文章配置保存异常"),
    APP_ARTICLE_CONTENT_UPDATE_ERROR(10009,"文章内容更新异常"),
    STATUS_UPDATE_ERROR(10010,"状态更新异常"),
    NEWS_UPDATE_ERROR(100011,"文章更新异常");

    int code;
    String errorMessage;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
