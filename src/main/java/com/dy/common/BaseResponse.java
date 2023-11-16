package com.dy.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: dy
 * @Date: 2023/11/16 9:10
 * @Description: 通用返回类
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 1172407173615570965L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 数据
     */
    private T data;

    /**
     * 消息
     */
    private String message;

    /**
     * 描述
     */
    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResponse(int code) {
        this.code = code;
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
