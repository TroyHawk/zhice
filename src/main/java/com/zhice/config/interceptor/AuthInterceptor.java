package com.zhice.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhice.common.api.Result;
import com.zhice.common.context.UserContext;
import com.zhice.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 统一鉴权拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取 Token (格式通常为 Bearer xxxxx)
        String bearerToken = request.getHeader("Authorization");

        // 2. 校验 Token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Long userId = JwtUtils.parseToken(token);

            if (userId != null) {
                // Token 有效，存入当前线程上下文，放行
                UserContext.setUserId(userId);
                return true;
            }
        }

        // 3. 鉴权失败，返回 401 状态码和 JSON 提示
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<String> errorResult = Result.error(401, "未登录或Token已过期，请重新登录");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResult));

        return false; // 拦截请求
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后必须清理 ThreadLocal，防止内存泄漏和线程池复用导致的数据串用
        UserContext.remove();
    }
}