package com.zhice.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 全局统一API响应封装类
 */
@Data
@Schema(description = "统一API响应包装类")
public class Result<T> {

    @Schema(description = "状态码，200表示成功", example = "200")
    private Integer code;

    @Schema(description = "响应信息", example = "操作成功")
    private String message;

    @Schema(description = "响应业务数据")
    private T data;

    protected Result() {}

    protected Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 失败返回
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}