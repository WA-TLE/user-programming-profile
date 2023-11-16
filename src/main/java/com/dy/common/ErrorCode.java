package com.dy.common;

import lombok.Data;
import lombok.Getter;

/**
 * @Author: dy
 * @Date: 2023/11/16 9:20
 * @Description: 错误码
 */
@Getter
public enum ErrorCode {

    /**
     * 成功
     */
    SUCCESS(0, "ok", ""),

    /**
     * 请求参数错误
     */
    PARAMS_ERROR(40000, "请求参数错误", "请求参数错误"),

    /**
     * 请求数据为空
     */
    NULL_ERROR(40001, "请求数据为空", ""),

    /**
     * 无权限
     */
    NOT_LOGIN(40100, "无权限", ""),

    /**
     * 系统内部异常
     */
    SYSTEM_ERROR(50000, "系统内部异常", "");

    private final int code;

    private final String message;

    private final String description;


    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }


}
