package com.zhice.config;

import com.zhice.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一拦截 Controller 层及下层抛出的异常，防止将代码堆栈直接暴露给前端
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截我们手动抛出的业务参数异常 (如权限不足、用户不存在等)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("业务校验未通过: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 拦截未知的系统兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统内部异常", e);
        return Result.error(500, "服务器开小差了，请稍后再试");
    }
}